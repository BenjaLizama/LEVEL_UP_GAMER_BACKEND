package com.level_up.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.level_up.usuarios.dto.AgregarUsuarioDTO;
import com.level_up.usuarios.dto.LoginDTO;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
@org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase(replace = org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();


    }


    @Test
    void crearUsuario() throws Exception {

        AgregarUsuarioDTO nuevoUsuario = crearDtoPrueba("nuevo@labs.com");
        String requestBody = objectMapper.writeValueAsString(nuevoUsuario);


        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status()
                        .isCreated());


           UsuarioModel usuarioGuardado = usuarioRepository.findByCorreo("nuevo@labs.com").orElse(null);

           assertNotNull(usuarioGuardado);
           assertEquals("bilbaolab",usuarioGuardado.getNombreUsuario());
           assertNotEquals("soypao",usuarioGuardado.getContrasena());

    }

    @Test
    void crearUsuarioCorreoDuplicado() throws Exception {
        UsuarioModel usuarioOriginal = crearModeloUsuario("nuevo@labs.com");

        usuarioRepository.save(usuarioOriginal);

        AgregarUsuarioDTO UsuarioDuplicado = crearDtoPrueba("nuevo@labs.com");
        String requestBodyDuplicado= objectMapper.writeValueAsString(UsuarioDuplicado);


        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyDuplicado))
                .andExpect(status()
                        .isConflict());

    }


    @Test
    void inicioDeSesionCorrecto() throws Exception {

        UsuarioModel usuarioFalso = crearModeloUsuario("bilbao@labs.com");


        usuarioRepository.save(usuarioFalso);

        LoginDTO loginDTOFalso = crearLogin("bilbao@labs.com","soypao");
        String loginRequest = objectMapper.writeValueAsString(loginDTOFalso);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.correo").value("bilbao@labs.com"));
    }

    @Test
    void inicioDeSesionIncorrecto() throws Exception {
        UsuarioModel usuarioFalso = crearModeloUsuario("holaSoyUnCorreo@gmail.com");


        usuarioRepository.save(usuarioFalso);

        LoginDTO loginDTOFalso = crearLogin("holaSoyUnCorreo@gmail.com","HolaSoyUnaContrasenaMaliciosa");

        String loginRequest = objectMapper.writeValueAsString(loginDTOFalso);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist());

    }


    private AgregarUsuarioDTO crearDtoPrueba(String correo) {
        AgregarUsuarioDTO dto = new AgregarUsuarioDTO();
        dto.setNombre("bilbao");
        dto.setApellido("labs");
        dto.setNombreUsuario("bilbaolab");
        dto.setCorreo(correo);
        dto.setFechaNacimiento(LocalDate.of(2001, 2, 13));
        dto.setContrasena("soypao");
        return dto;
    }

    private UsuarioModel crearModeloUsuario(String correo) {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre("bilbao");
        usuario.setApellido("labs");
        usuario.setNombreUsuario("bilbaolab");
        usuario.setCorreo(correo);
        usuario.setFechaNacimiento(LocalDate.of(2001, 2, 13));
        usuario.setContrasena(passwordEncoder.encode("soypao"));
        usuario.setRol("USER");
        return usuario;
    }

    private LoginDTO crearLogin(String correo, String contrasena){
      LoginDTO login = new LoginDTO();
      login.setContrasena(contrasena);
      login.setCorreo(correo);
        return login;
    }
}


