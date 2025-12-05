package com.level_up.usuarios.controller;

import com.level_up.usuarios.exception.UsuarioNotFoundException;
import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.repository.UsuarioRepository;
import com.level_up.usuarios.service.PagoService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stripe")
@Tag(name = "Gestor de pagos", description = "Endpoints utilizados para gestionar los pagos")
public class PagoController {

    private final PagoService pagoService;
    private final UsuarioRepository usuarioRepository;

    static class CreatePaymentRequest {

    }

    static class CreatePaymentResponse {
        private String url;

        public CreatePaymentResponse(String url) { this.url = url; }
        public String getUrl() { return url; }
    }

    @Operation(summary = "Realizar pago en base al carrito del usuario")
    @ApiResponse(responseCode = "200", description = "Link de pago generado con exito")
    @ApiResponse(responseCode = "400", description = "Error al realizar el pago")
    @ApiResponse(responseCode = "403", description = "Usuario sin roles suficientes para realizar la operacion")
    @ApiResponse(responseCode = "500", description = "Error inesperado no controlado")
    @PostMapping("/pagar")
    public ResponseEntity<?> createPaymentSession() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
            }

            String correo = authentication.getName();
            UsuarioModel usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

            Session session = pagoService.crearSessionDePago(usuario.getIdUsuario());

            CreatePaymentResponse response = new CreatePaymentResponse(session.getUrl());
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (StripeException e) {
            return new ResponseEntity<>("Error Stripe: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Confirmar pago")
    @ApiResponse(responseCode = "200", description = "Confirmacion de pago realizada con exito")
    @ApiResponse(responseCode = "400", description = "Error al realizar confirmacion de pago")
    @ApiResponse(responseCode = "403", description = "Usuario sin roles suficientes para realizar la operacion")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/confirmar-pago")
    public ResponseEntity<?> confirmarPago(@RequestParam("session_id") String sessionId) {
        try {
            pagoService.verificarPagoCompletado(sessionId);

            return ResponseEntity.ok("Compra verificada y finalizada con éxito.");

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de Stripe al verificar: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al finalizar la compra: " + e.getMessage());
        }
    }
}