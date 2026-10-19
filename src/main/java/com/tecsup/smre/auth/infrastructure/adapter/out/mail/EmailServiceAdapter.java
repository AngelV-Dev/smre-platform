package com.tecsup.smre.auth.infrastructure.adapter.out.mail;

import com.tecsup.smre.auth.domain.port.out.EmailServicePort;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailServiceAdapter implements EmailServicePort {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Override
    public void sendPasswordResetEmail(String toEmail, String nombre, String resetLink) {
        log.info("=======================================================");
        log.info("📧 SOLICITUD DE RECUPERACIÓN DE CONTRASEÑA");
        log.info("Destinatario : {} <{}>", nombre, toEmail);
        log.info("Enlace directo: {}", resetLink);
        log.info("=======================================================");

        if (mailSender == null) {
            log.warn("JavaMailSender no está inicializado. No se enviará el correo físico por SMTP.");
            return;
        }

        if (fromEmail == null || fromEmail.isBlank() || fromEmail.contains("tu_correo")) {
            log.warn("spring.mail.username no está configurado con credenciales válidas. Revisa application.properties.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Recuperación de contraseña - Plataforma SMRE Tecsup");

            String htmlContent = """
                <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;">
                    <div style="background-color: #003B49; padding: 24px; text-align: center;">
                        <h1 style="color: #00A9E0; margin: 0; font-size: 24px; letter-spacing: 1px;">TECSUP - SMRE</h1>
                        <p style="color: #ffffff; margin: 4px 0 0 0; font-size: 13px;">Sistema de Monitoreo de Riesgo Estudiantil</p>
                    </div>
                    <div style="padding: 32px 24px; color: #334155;">
                        <h2 style="font-size: 18px; color: #0f172a; margin-top: 0;">Hola, %s</h2>
                        <p style="font-size: 14px; line-height: 1.6; color: #475569;">
                            Hemos recibido una solicitud para restablecer la contraseña de tu cuenta institucional de Tecsup en la plataforma SMRE.
                        </p>
                        <p style="font-size: 14px; line-height: 1.6; color: #475569;">
                            Para definir una nueva contraseña, haz clic en el siguiente botón:
                        </p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="display: inline-block; background-color: #00A9E0; color: #ffffff; padding: 12px 28px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 14px;">
                                Restablecer mi contraseña
                            </a>
                        </div>
                        <p style="font-size: 12px; color: #94a3b8; line-height: 1.5;">
                            Este enlace es de un solo uso y expirará en <strong>15 minutos</strong>.<br/>
                            Si el botón no funciona, copia y pega este enlace en tu navegador:<br/>
                            <a href="%s" style="color: #00A9E0;">%s</a>
                        </p>
                        <hr style="border: none; border-top: 1px solid #f1f5f9; margin: 24px 0;" />
                        <p style="font-size: 12px; color: #94a3b8; margin-bottom: 0;">
                            Si no solicitaste este cambio, puedes ignorar este correo; tu contraseña continuará siendo la misma.
                        </p>
                    </div>
                </div>
                """.formatted(nombre != null ? nombre : "Usuario", resetLink, resetLink, resetLink);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("✅ Correo de recuperación enviado exitosamente a {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Error al enviar el correo a {}: {}", toEmail, e.getMessage());
        }
    }
}
