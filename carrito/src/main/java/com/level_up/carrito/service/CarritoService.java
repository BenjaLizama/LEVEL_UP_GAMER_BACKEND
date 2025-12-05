package com.level_up.carrito.service;

import com.level_up.carrito.client.ProductoFeignClient;
import com.level_up.carrito.dto.AgregarItemDTO;
import com.level_up.carrito.dto.CarritoRetornoDTO;
import com.level_up.carrito.dto.ItemCarritoRetornoDTO;
import com.level_up.carrito.dto.ProductoExternoDTO;
import com.level_up.carrito.model.CarritoModel;
import com.level_up.carrito.model.ItemCarritoModel;
import com.level_up.carrito.repository.CarritoRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoFeignClient productoFeignClient;

    public CarritoRetornoDTO agregarItem(Long idUsuario, AgregarItemDTO dto) {
        ProductoExternoDTO productoExterno = obtenerProductoExterno(dto.getCodigoProducto());

        if (productoExterno.getCantidadStockProducto() < dto.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Solo quedan " + productoExterno.getCantidadStockProducto() + " unidades.");
        }

        CarritoModel carrito = carritoRepository.findByIdUsuario(idUsuario)
                .orElseGet(() -> crearNuevoCarrito(idUsuario));

        Optional<ItemCarritoModel> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getCodigoProducto().equals(dto.getCodigoProducto()))
                .findFirst();

        if (itemExistente.isPresent()) {
            itemExistente.get().setCantidad(itemExistente.get().getCantidad() + dto.getCantidad());
        } else {
            ItemCarritoModel nuevoItem = new ItemCarritoModel();
            nuevoItem.setCodigoProducto(dto.getCodigoProducto());
            nuevoItem.setCantidad(dto.getCantidad());
            nuevoItem.setPrecioUnitario(productoExterno.getPrecioProducto());
            nuevoItem.setCarrito(carrito);

            carrito.getItems().add(nuevoItem);
        }

        recalcularTotal(carrito);
        CarritoModel carritoGuardado = carritoRepository.save(carrito);
        return mapearCarritoADTO(carritoGuardado);
    }

    public CarritoRetornoDTO obtenerCarrito(Long idUsuario) {
        try {
            CarritoModel carrito = carritoRepository.findByIdUsuario(idUsuario)
                    .orElseThrow(() -> new RuntimeException("El usuario " + idUsuario + " no tiene un carrito activo."));

            return mapearCarritoADTO(carrito);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener el carrito: " + e.getMessage());
        }
    }

    public CarritoRetornoDTO eliminarItem(Long idUsuario, String codigoProducto) {
        CarritoModel carrito = obtenerCarritoModel(idUsuario);

        Boolean itemEliminado = carrito.getItems().removeIf(item -> item.getCodigoProducto().equals(codigoProducto));

        if (!itemEliminado) {
            throw new RuntimeException("El producto " + codigoProducto + " no esta en el carrito para ser eliminado");
        }

        recalcularTotal(carrito);

        CarritoModel carritoGuardado = carritoRepository.save(carrito);
        return mapearCarritoADTO(carritoGuardado);
    }

    public void inicializarCarrito(Long idUsuario) {
        crearNuevoCarrito(idUsuario);
    }

    public CarritoRetornoDTO quitarItemDelCarrito(Long idUsuario, String codigoProducto) {
        CarritoModel carritoDelUsuario = obtenerCarritoModel(idUsuario);

        ItemCarritoModel itemBuscado = carritoDelUsuario.getItems().stream()
                .filter(item -> item.getCodigoProducto().equals(codigoProducto))
                .findFirst()
                .orElse(null);

        if (itemBuscado == null) {
            System.out.println("El item no se encuentra en el carrito!");
            return mapearCarritoADTO(carritoDelUsuario);
        }

        if (itemBuscado.getCantidad() <= 1) {
            carritoDelUsuario.getItems().remove(itemBuscado);
        } else {
            itemBuscado.setCantidad(itemBuscado.getCantidad() -1);
        }

        recalcularTotal(carritoDelUsuario);
        carritoRepository.save(carritoDelUsuario);
        return mapearCarritoADTO(carritoDelUsuario);
    }

    private ProductoExternoDTO obtenerProductoExterno(String codigoProducto) {
        try {
            return productoFeignClient.obtenerProductoPorCodigo(codigoProducto);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("El producto con codigo " + codigoProducto + " no existe.");
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de productos: " + e.getMessage());
        }
    }

    private CarritoModel obtenerCarritoModel(Long idUsuario) {
        return carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("El usuario " + idUsuario + " no tiene un carrito activo."));
    }

    private CarritoModel crearNuevoCarrito(Long idUsuario) {
        CarritoModel nuevoCarrito = new CarritoModel();
        nuevoCarrito.setIdUsuario(idUsuario);
        nuevoCarrito.setTotal(0.0);
        return carritoRepository.save(nuevoCarrito);
    }

    private void recalcularTotal(CarritoModel carrito) {
        Double nuevototal = carrito.getItems().stream()
                .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                .sum();
        carrito.setTotal(nuevototal);
    }

    private CarritoRetornoDTO mapearCarritoADTO(CarritoModel carrito) {
        CarritoRetornoDTO dto = new CarritoRetornoDTO();
        dto.setIdCarrito(carrito.getIdCarrito());
        dto.setIdUsuario(carrito.getIdUsuario());
        dto.setTotal(carrito.getTotal());

        List<ItemCarritoRetornoDTO> itemsDTO = carrito.getItems().stream()
                .map(this::mapearItemADTO)
                .toList();

        dto.setItems(itemsDTO);
        return dto;
    }

    private ItemCarritoRetornoDTO mapearItemADTO(ItemCarritoModel item) {
        ItemCarritoRetornoDTO dto = new ItemCarritoRetornoDTO();
        dto.setCodigoProducto(item.getCodigoProducto());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());

        dto.setSubtotal(item.getPrecioUnitario() * item.getCantidad());

        return dto;
    }

    public void vaciarCarrito(Long idUsuario) {
        CarritoModel carritoDelUsuario = obtenerCarritoModel(idUsuario);
        carritoDelUsuario.getItems().clear();
        recalcularTotal(carritoDelUsuario);
    }
}