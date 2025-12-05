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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stripe")
public class PagoController {

    private final PagoService pagoService;
    private final UsuarioRepository usuarioRepository;

    // 1. Modificamos el DTO: Ya no necesitamos recibir 'amount' desde el frontend
    static class CreatePaymentRequest {
        // Puedes dejarlo vacío o agregar otros campos si necesitas (ej. dirección)
        // Por ahora no necesitamos nada del cuerpo si el token trae la identidad.
    }

    static class CreatePaymentResponse {
        private String url;

        public CreatePaymentResponse(String url) { this.url = url; }
        public String getUrl() { return url; }
    }

    @PostMapping("/pagar")
    public ResponseEntity<?> createPaymentSession(/* @RequestBody CreatePaymentRequest request */) {
        // Nota: Si no envías nada en el body desde frontend, puedes quitar @RequestBody
        try {
            // 2. ELIMINADO: Ya no validamos request.getAmount() porque no confiamos en el front.

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

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, "TU_CLAVE_SECRETA_DE_WEBHOOK");
        } catch (SignatureVerificationException e) {
            // Firma inválida
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma inválida");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null) {
                String customerId = session.getCustomer();

                pagoService.finalizarCompra(customerId);
            }
        }

        return ResponseEntity.ok("Recibido");
    }

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