package com.level_up.usuarios.service;

import com.level_up.usuarios.client.CarritoFeignClient;
import com.level_up.usuarios.dto.TotalCarritoDTO;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.repository.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoFeignClient carritoFeignClient;

    public PaymentIntent crearIntentoDePago(Long idUsuario) throws StripeException {

        long montoA_Cobrar = calcularMontoEnCentavos(idUsuario);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(montoA_Cobrar)
                .setCurrency("clp")
                .setDescription("Compra usuario ID: " + idUsuario)
                .build();

        return PaymentIntent.create(params);
    }

    @Transactional
    public Session crearSessionDePago(Long usuarioId) throws StripeException {

        long montoA_Cobrar = calcularMontoEnCentavos(usuarioId);

        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        String customerId = usuario.getStripeCostumerId();

        if (customerId == null || customerId.isBlank()) {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(usuario.getNombre() + " " + usuario.getApellido())
                    .setEmail(usuario.getCorreo())
                    .putMetadata("app_user_id", usuario.getIdUsuario().toString())
                    .build();

            Customer customer = Customer.create(customerParams);
            customerId = customer.getId();

            usuario.setStripeCostumerId(customerId);
            usuarioRepository.save(usuario);
        }

        String domainUrl = "http://localhost:5173";

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomer(customerId)
                .setSuccessUrl(domainUrl + "/pago-exitoso?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(domainUrl + "/carrito")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("clp")
                                                .setUnitAmount(montoA_Cobrar) // ✅ Usamos la variable interna segura
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Compra en Level Up")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        return Session.create(params);
    }

    @Transactional
    public void finalizarCompra(String stripeCustomerId) {

        // 1. Busca el Usuario en tu DB por el ID de Cliente de Stripe
        UsuarioModel usuario = (UsuarioModel) usuarioRepository.findByStripeCostumerId(stripeCustomerId)
                .orElseThrow(() -> new RuntimeException("Cliente Stripe no mapeado a usuario"));

        Long idUsuario = usuario.getIdUsuario();

        // 2. CREA LA ORDEN
        // Aquí invocarías al Microservicio de Pedidos para crear la orden
        // pedidoClient.crearOrden(idUsuario, carrito);

        // 3. VACÍA EL CARRITO (La acción requerida)
        // Usamos Feign para decirle al microservicio de Carrito que vacíe la lista de ítems
        carritoFeignClient.vaciarCarrito(idUsuario);
        // Este método debe ser implementado por ti en CarritoFeignClient y en el CarritoService
    }

    // PagoService.java
    public Session verificarPagoCompletado(String sessionId) throws StripeException {

        Session session = Session.retrieve(sessionId);

        if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {

            String stripeCustomerId = session.getCustomer();

            finalizarCompra(stripeCustomerId);

            return session;
        } else {
            throw new RuntimeException("El pago de la sesión " + sessionId + " aún no ha sido completado.");
        }
    }

    private long calcularMontoEnCentavos(Long idUsuario) {
        TotalCarritoDTO carrito = carritoFeignClient.obtenerTotalCarrito(idUsuario);

        if (carrito == null || carrito.getTotal() == null) {
            throw new RuntimeException("No se pudo obtener el total del carrito. Verifica que el microservicio de carrito esté activo.");
        }

        return (long) (carrito.getTotal() * 1);
    }
}