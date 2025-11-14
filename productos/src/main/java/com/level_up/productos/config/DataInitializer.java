package com.level_up.productos.config;

import com.level_up.productos.enums.CategoriaEnum;
import com.level_up.productos.dto.ProductoDTO; // <-- 1. Importa tu DTO
import com.level_up.productos.service.ProductoService; // <-- 2. Importa tu Service (ajusta la ruta si es necesario)
import com.level_up.productos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final ProductoService productoService; // <-- 3. Inyecta el Servicio

    // 4. Actualiza el constructor
    public DataInitializer(ProductoRepository productoRepository, ProductoService productoService) {
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Esto se queda igual
        if (productoRepository.count() == 0) {
            System.out.println("Base de datos vacía. Inicializando productos...");
            crearProductosPorDefecto();
        } else {
            System.out.println("La base de datos ya tiene datos. No se inicializa.");
        }
    }

    private void crearProductosPorDefecto() {
        try {
            // --- Producto 1: Catan (El que ya tenías) ---
            ProductoDTO producto1_DTO = new ProductoDTO();
            producto1_DTO.setNombreProducto("Catan");
            producto1_DTO.setPrecioProducto(29990.0);
            producto1_DTO.setCategoriaProducto(CategoriaEnum.JUEGO_MESA);
            producto1_DTO.setDescripcionProducto("Un clásico juego de estrategia donde los jugadores compiten por colonizar y expandirse en la isla de Catan. Ideal para 3-4 jugadores y perfecto para noches de juego en familia o con amigos.");
            producto1_DTO.setCantidadInicial(6);
            List<String> producto1Imagenes = new ArrayList<>();
            producto1Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_990398-MLA96120779215_102025-F.webp"); // Ejemplo
            producto1_DTO.setImagenesUrl(producto1Imagenes);
            productoService.agregarProducto(producto1_DTO);

            // --- Producto 2: Carcassonne ---
            ProductoDTO producto2_DTO = new ProductoDTO();
            producto2_DTO.setNombreProducto("Carcassonne");
            producto2_DTO.setPrecioProducto(24990.0);
            producto2_DTO.setCategoriaProducto(CategoriaEnum.JUEGO_MESA);
            producto2_DTO.setDescripcionProducto("Un juego de colocación de fichas donde los jugadores construyen el paisaje alrededor de la fortaleza medieval de Carcassonne. Ideal para 2-5 jugadores y fácil de aprender.");
            producto2_DTO.setCantidadInicial(10);
            List<String> producto2Imagenes = new ArrayList<>();
            producto2Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_957213-MLA95402375200_102025-F.webp");
            producto2Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_857596-MLA80239811299_102024-F.webp");
            producto2Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_686899-MLA94316605409_102025-F.webp");
            producto2Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_948202-MLA79984723774_102024-F.webp");
            producto2_DTO.setImagenesUrl(producto2Imagenes);
            productoService.agregarProducto(producto2_DTO);

            // --- Producto 3: Controlador Inalámbrico Xbox Series X ---
            ProductoDTO producto3_DTO = new ProductoDTO();
            producto3_DTO.setNombreProducto("Controlador Inalámbrico Xbox Series X");
            producto3_DTO.setPrecioProducto(59990.0);
            producto3_DTO.setCategoriaProducto(CategoriaEnum.ACCESORIOS);
            producto3_DTO.setDescripcionProducto("Ofrece una experiencia de juego cómoda con botones mapeables y una respuesta táctil mejorada. Compatible con consolas Xbox y PC.");
            producto3_DTO.setCantidadInicial(15);
            List<String> producto3Imagenes = new ArrayList<>();
            producto3Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_615921-MLA96135232001_102025-F.webp");
            producto3Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_760543-MLU75976359927_042024-F.webp");
            producto3Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_903265-MLU72824211081_112023-F.webp");
            producto3_DTO.setImagenesUrl(producto3Imagenes);
            productoService.agregarProducto(producto3_DTO);

            // --- Producto 4: Auriculares Gamer HyperX Cloud II ---
            ProductoDTO producto4_DTO = new ProductoDTO();
            producto4_DTO.setNombreProducto("Auriculares Gamer HyperX Cloud II");
            producto4_DTO.setPrecioProducto(79990.0);
            producto4_DTO.setCategoriaProducto(CategoriaEnum.ACCESORIOS);
            producto4_DTO.setDescripcionProducto("Proporcionan un sonido envolvente de calidad con un micrófono desmontable y almohadillas de espuma viscoelástica para mayor comodidad durante largas sesiones de juego.");
            producto4_DTO.setCantidadInicial(12);
            List<String> producto4Imagenes = new ArrayList<>();
            producto4Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_974353-MLA93089130726_092025-F.webp");
            producto4Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_934692-MLA93089021828_092025-F.webp");
            producto4Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_935883-MLA93089021834_092025-F.webp");
            producto4Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_964760-MLA93089239676_092025-F.webp");
            producto4_DTO.setImagenesUrl(producto4Imagenes);
            productoService.agregarProducto(producto4_DTO);

            // --- Producto 5: PlayStation 5 ---
            ProductoDTO producto5_DTO = new ProductoDTO();
            producto5_DTO.setNombreProducto("PlayStation 5");
            producto5_DTO.setPrecioProducto(549990.0);
            producto5_DTO.setCategoriaProducto(CategoriaEnum.CONSOLAS);
            producto5_DTO.setDescripcionProducto("La consola de última generación de Sony, que ofrece gráficos impresionantes y tiempos de carga ultrarrápidos para una experiencia de juego inmersiva.");
            producto5_DTO.setCantidadInicial(5);
            List<String> producto5Imagenes = new ArrayList<>();
            producto5Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_795279-MLA95697943594_102025-F.webp");
            producto5Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_809182-MLA86475260308_062025-F.webp");
            producto5Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_754048-MLA89073642211_072025-F.webp");
            producto5Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_951245-MLA89073651943_072025-F.webp");
            producto5_DTO.setImagenesUrl(producto5Imagenes);
            productoService.agregarProducto(producto5_DTO);

            // --- Producto 6: PC Gamer ASUS ROG Strix ---
            ProductoDTO producto6_DTO = new ProductoDTO();
            producto6_DTO.setNombreProducto("PC Gamer ASUS ROG Strix");
            producto6_DTO.setPrecioProducto(1299990.0);
            producto6_DTO.setCategoriaProducto(CategoriaEnum.COMPUTADORES_GAMERS);
            producto6_DTO.setDescripcionProducto("Un potente equipo diseñado para los gamers más exigentes, equipado con los últimos componentes para ofrecer un rendimiento excepcional en cualquier juego.");
            producto6_DTO.setCantidadInicial(3);
            List<String> producto6Imagenes = new ArrayList<>();
            producto6Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_867779-MLA96100027881_102025-F.webp");
            producto6Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_954348-MLA92124217356_092025-F.webp");
            producto6Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_952533-MLA92529399217_092025-F.webp");
            producto6Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_701839-MLA92529399231_092025-F.webp");
            producto6Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_918343-MLA92529379501_092025-F.webp");
            producto6_DTO.setImagenesUrl(producto6Imagenes);
            productoService.agregarProducto(producto6_DTO);

            // --- Producto 7: Silla Gamer Secretlab Titan ---
            ProductoDTO producto7_DTO = new ProductoDTO();
            producto7_DTO.setNombreProducto("Silla Gamer Secretlab Titan");
            producto7_DTO.setPrecioProducto(349990.0);
            producto7_DTO.setCategoriaProducto(CategoriaEnum.SILLAS_GAMERS);
            producto7_DTO.setDescripcionProducto("Diseñada para el máximo confort, esta silla ofrece un soporte ergonómico y personalización ajustable para sesiones de juego prolongadas.");
            producto7_DTO.setCantidadInicial(8);
            List<String> producto7Imagenes = new ArrayList<>();
            producto7Imagenes.add("https://i0.wp.com/centralgamer.cl/wp-content/uploads/2025/03/1_1741802989919.jpg?fit=600%2C600&ssl=1");
            producto7Imagenes.add("https://i0.wp.com/centralgamer.cl/wp-content/uploads/2024/12/2_1728280078000.png?fit=600%2C480&ssl=1");
            producto7Imagenes.add("https://i0.wp.com/centralgamer.cl/wp-content/uploads/2025/03/1_1741802989919.jpg?fit=600%2C600&ssl=1");
            producto7Imagenes.add("https://i0.wp.com/centralgamer.cl/wp-content/uploads/2024/12/4_1728280086000.png?fit=600%2C480&ssl=1");
            producto7Imagenes.add("https://i0.wp.com/centralgamer.cl/wp-content/uploads/2024/12/5_1728280090000.png?fit=600%2C480&ssl=1");
            producto7Imagenes.add("https://i0.wp.com/centralgamer.cl/wp-content/uploads/2024/12/6_1728280095000.png?fit=600%2C480&ssl=1");
            producto7_DTO.setImagenesUrl(producto7Imagenes);
            productoService.agregarProducto(producto7_DTO);

            // --- Producto 8: Mouse Gamer Logitech G502 HERO ---
            ProductoDTO producto8_DTO = new ProductoDTO();
            producto8_DTO.setNombreProducto("Mouse Gamer Logitech G502 HERO");
            producto8_DTO.setPrecioProducto(49990.0);
            producto8_DTO.setCategoriaProducto(CategoriaEnum.MOUSE);
            producto8_DTO.setDescripcionProducto("Con sensor de alta precisión y botones personalizables, este mouse es ideal para gamers que buscan un control preciso y personalización.");
            producto8_DTO.setCantidadInicial(20);
            List<String> producto8Imagenes = new ArrayList<>();
            producto8Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_650155-MLA95691271012_102025-F.webp");
            producto8Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_674182-MLA84549535524_052025-F.webp");
            producto8Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_903619-MLA84549478516_052025-F.webp");
            producto8Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_905380-MLA84549535526_052025-F.webp");
            producto8_DTO.setImagenesUrl(producto8Imagenes);
            productoService.agregarProducto(producto8_DTO);

            // --- Producto 9: Mousepad Razer Goliathus Extended Chroma ---
            ProductoDTO producto9_DTO = new ProductoDTO();
            producto9_DTO.setNombreProducto("Mousepad Razer Goliathus Chroma");
            producto9_DTO.setPrecioProducto(55990.0);
            producto9_DTO.setCategoriaProducto(CategoriaEnum.MOUSE_PAD);
            producto9_DTO.setDescripcionProducto("Ofrece un área de juego amplia con iluminación RGB personalizable, asegurando una superficie suave y uniforme para el movimiento del mouse.");
            producto9_DTO.setCantidadInicial(25);
            List<String> producto9Imagenes = new ArrayList<>();
            producto9Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_804502-MLA93597357666_102025-F.webp");
            producto9Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_867843-MLA93596913818_102025-F.webp");
            producto9Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_890920-MLA93597022818_102025-F.webp");
            producto9Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_853281-MLA93597337822_102025-F.webp");
            producto9Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_791369-MLA94018593321_102025-F.webp");
            producto9_DTO.setImagenesUrl(producto9Imagenes);
            productoService.agregarProducto(producto9_DTO);

            // --- Producto 10: Polera Gamer Personalizada 'Level-Up' ---
            ProductoDTO producto10_DTO = new ProductoDTO();
            producto10_DTO.setNombreProducto("Polera Gamer Personalizada 'Level-Up'");
            producto10_DTO.setPrecioProducto(14990.0);
            producto10_DTO.setCategoriaProducto(CategoriaEnum.POLERAS_PERSONALIZADAS);
            producto10_DTO.setDescripcionProducto("Una camiseta cómoda y estilizada, con la posibilidad de personalizarla con tu gamer tag o diseño favorito.");
            producto10_DTO.setCantidadInicial(30);
            List<String> producto10Imagenes = new ArrayList<>();
            producto10Imagenes.add("https://http2.mlstatic.com/D_NQ_NP_2X_838768-CBT83057883268_032025-F-c-game-roblox-camiseta-de-manga-corta-para-hombre-de-moda.webp");
            producto10_DTO.setImagenesUrl(producto10Imagenes);
            productoService.agregarProducto(producto10_DTO);


            System.out.println("Se crearon 10 productos por defecto.");

        } catch (Exception e) {
            System.err.println("Error al inicializar productos por defecto: " + e.getMessage());
        }
    }
}