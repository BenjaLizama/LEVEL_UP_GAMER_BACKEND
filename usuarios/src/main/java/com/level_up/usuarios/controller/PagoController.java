package com.level_up.usuarios.controller;

import com.level_up.usuarios.exception.UsuarioNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.level_up.usuarios.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin
public class PagoController {

    @Autowired
    private PagoService pagoService;

    static class CreatePaymentRequest {
        private Long amount;

        public Long getAmount() { return amount; }
        public void setAmount(Long amount) { this.amount = amount; }
    }

    static class CreatePaymentResponse {
        private String clientSecret;

        public CreatePaymentResponse(String clientSecret) { this.clientSecret = clientSecret; }
        public String getClientSecret() { return clientSecret; }
    }


    @Operation(summary = "Crear una nueva intención de pago en Stripe")
    @ApiResponse(responseCode = "201", description = "Intención de pago creada exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreatePaymentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. falta el monto)")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor (ej. fallo en Stripe)")
    @PostMapping("/pagar")
    public ResponseEntity<?> createPaymentIntent(@RequestBody CreatePaymentRequest request, HttpSession session) {
        try {
            if (request.getAmount() == null || request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("El monto es requerido y debe ser mayor a 0");
            }

            Long usuarioId = (Long) session.getAttribute("usuarioId");

            if (usuarioId == null) {
                throw new UsuarioNotFoundException("Debes iniciar sesion para realizar esta accion.");
            }

            PaymentIntent paymentIntent = pagoService.createPaymentIntent(request.getAmount(), usuarioId);

            CreatePaymentResponse response = new CreatePaymentResponse(paymentIntent.getClientSecret());
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (StripeException e) {
            return new ResponseEntity<>("Error al procesar el pago: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Error inesperado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}