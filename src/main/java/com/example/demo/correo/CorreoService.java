package com.example.demo.correo;

import com.example.demo.curso.Curso;
import com.example.demo.estudiante.Estudiante;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Arma y envia el correo HTML de asignacion, AGRUPADO por carrera.
 */
@Service
public class CorreoService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remitente;

    public CorreoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarAsignacion(Estudiante estudiante, Map<String, List<Curso>> grupos) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setTo(estudiante.getCorreo());
            if (remitente != null && !remitente.isBlank()) {
                helper.setFrom(remitente);
            }
            helper.setSubject("Asignacion de cursos");
            helper.setText(construirHtml(estudiante, grupos), true);
            mailSender.send(mensaje);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }

    private String construirHtml(Estudiante estudiante, Map<String, List<Curso>> grupos) {
        StringBuilder cuerpo = new StringBuilder();
        int total = 0;

        for (Map.Entry<String, List<Curso>> grupo : grupos.entrySet()) {
            cuerpo.append("""
                <tr>
                  <td style="padding:16px 18px 6px;">
                    <div style="font-family:Georgia,serif;font-size:16px;color:#0F6E56;font-weight:bold;">%s</div>
                  </td>
                </tr>
                """.formatted(escapar(grupo.getKey())));

            int n = 1;
            for (Curso c : grupo.getValue()) {
                total++;
                cuerpo.append("""
                    <tr>
                      <td style="padding:10px 18px;border-bottom:1px solid #ECE7DC;">
                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td width="40" valign="top">
                              <div style="width:30px;height:30px;border-radius:8px;background:#0F6E56;color:#ffffff;font-family:Georgia,serif;font-size:14px;font-weight:bold;text-align:center;line-height:30px;">%d</div>
                            </td>
                            <td valign="middle" style="font-family:Arial,sans-serif;">
                              <div style="font-size:15px;color:#16302B;font-weight:bold;">%s</div>
                              <div style="font-size:12px;color:#8A8375;letter-spacing:.04em;">%s</div>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    """.formatted(n++, escapar(c.getNombre()), escapar(c.getCodigo())));
            }
        }

        return """
            <!DOCTYPE html>
            <html lang="es">
            <body style="margin:0;padding:0;background:#F2EFE8;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#F2EFE8;padding:32px 12px;">
                <tr><td align="center">
                  <table role="presentation" width="560" cellpadding="0" cellspacing="0" style="max-width:560px;background:#FFFFFF;border-radius:18px;overflow:hidden;border:1px solid #E4DFD4;">
                    <tr>
                      <td style="background:#04342C;padding:34px 34px 28px;">
                        <div style="font-family:Arial,sans-serif;font-size:11px;letter-spacing:.18em;text-transform:uppercase;color:#EF9F27;font-weight:bold;">Universidad Mariano Galvez</div>
                        <div style="font-family:Georgia,serif;font-size:26px;color:#ffffff;margin-top:8px;line-height:1.2;">Asignacion de cursos</div>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:30px 34px 4px;font-family:Arial,sans-serif;">
                        <div style="font-size:15px;color:#16302B;">Estimado estudiante <strong>%s</strong>,</div>
                        <div style="font-size:15px;color:#4A544F;margin-top:10px;line-height:1.55;">
                          Le informamos que se le asignaron los siguientes cursos:
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:14px 34px 6px;">
                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#FBFAF6;border:1px solid #ECE7DC;border-radius:12px;">
                          %s
                        </table>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:20px 34px 4px;font-family:Arial,sans-serif;">
                        <div style="font-size:13px;color:#6C7B74;line-height:1.55;">
                          Total de cursos asignados: <strong style="color:#0F6E56;">%d</strong>. Si detecta algun error, comuniquese con control academico.
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:26px 34px 30px;border-top:1px solid #ECE7DC;font-family:Arial,sans-serif;">
                        <div style="font-size:12px;color:#9A9384;">Este es un correo automatico generado por el sistema academico. No responda a este mensaje.</div>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(escapar(estudiante.getNombreCompleto()), cuerpo.toString(), total);
    }

    private String escapar(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}