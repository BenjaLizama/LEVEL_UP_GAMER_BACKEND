package com.level_up.usuarios.config;

import com.level_up.usuarios.dto.AgregarUsuarioDTO;
import com.level_up.usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{

    private final UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("----- VERIFICANDO USUARIO ADMIN -----");

        AgregarUsuarioDTO nuevoAdmin = new AgregarUsuarioDTO();
        nuevoAdmin.setNombre("Administrador");
        nuevoAdmin.setApellido("Level-up");
        nuevoAdmin.setCorreo("admin@levelup.com");
        nuevoAdmin.setContrasena("administrador");
        nuevoAdmin.setNombreUsuario("Admin");

        usuarioService.saveAdmin(nuevoAdmin);
        System.out.println("✅ Proceso de inicialización de Admin finalizado.");
    }
}
