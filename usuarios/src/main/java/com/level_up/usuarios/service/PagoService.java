package com.level_up.usuarios.service;

import com.level_up.usuarios.model.UsuarioModel;
import com.level_up.usuarios.repository.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public PaymentIntent createPaymentIntent(Long amount, Long usuarioId) throws StripeException {

        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para el ID: " + usuarioId));

        // Obtiene el ID de cliente de Stripe
        String customerId = usuario.getStripeCostumerId();

        // Crear el cliente si no existe
        if (customerId == null || customerId.isBlank()) {
            // Prepara los datos del cliente para Stripe
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(usuario.getNombre() + " " + usuario.getApellido())
                    .setEmail(usuario.getCorreo())
                    .putMetadata("app_user_id", usuario.getIdUsuario().toString()) // Buena práctica
                    .build();

            // Crea el cliente en Stripe
            Customer customer = Customer.create(customerParams);

            // Actualiza nuestra variable local y nuestro objeto de usuario
            customerId = customer.getId();
            usuario.setStripeCostumerId(customerId); // OJO: setStripeCostumerId

            // ¡IMPORTANTE! Guarda el usuario actualizado en la base de datos
            usuarioRepository.save(usuario);
        }

        // Crea el pago asociado al cliente
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency("clp")
                        .setCustomer(customerId)

                        // Le dice a Stripe que queremos guardar esta tarjeta
                        .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.ON_SESSION)
                        // ------------------------------
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        return PaymentIntent.create(params);
    }
}