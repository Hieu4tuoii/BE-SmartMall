package vn.hieu4tuoi.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
  private final JavaMailSender mailSender;

  public void sendOtp(String toEmail, String otp, String name) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setTo(toEmail);
    helper.setSubject("Mã OTP xác thực của bạn");

    String htmlContent = String.format(
        """
                               <body style="margin:0;padding:0;background-color:#f4f6f8;font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial;">

              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8;padding:24px 16px;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="max-width:600px;background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">

                      <tr>
                        <td style="padding:20px 24px;background:linear-gradient(90deg,#0ea5e9,#6366f1);color:#fff;">
                          <table role="presentation" width="100%%">
                            <tr>
                              <td style="font-size:18px;font-weight:600;">SmartMall</td>
                              <td align="right" style="font-size:12px;opacity:0.95;">Mã xác thực</td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <tr>
                        <td style="padding:28px 24px 16px 24px;color:#111827;">
                          <h1 style="margin:0 0 12px 0;font-size:20px;font-weight:700;">Xin chào %s,</h1>
                          <p style="margin:0 0 18px 0;font-size:14px;line-height:1.5;color:#374151;">
                            Mã xác thực (OTP) của bạn là:
                          </p>

                          <div style="margin:12px 0 20px 0;padding:18px 14px;background:#f8fafc;border:1px dashed #e6eef8;border-radius:8px;display:inline-block;">
                            <span style="font-family: 'Courier New', monospace;font-size:28px;letter-spacing:4px;font-weight:700;color:#0b5394;">
                              %s
                            </span>
                          </div>

                          <p style="margin:0 0 16px 0;font-size:13px;color:#6b7280;">
                           Vui lòng không chia sẻ mã này với bất kỳ ai.
                          </p>


                          <hr style="border:none;border-top:1px solid #eef2f6;margin:18px 0;"/>
                        </td>
                      </tr>


                    </table>
                  </td>
                </tr>
              </table>

            </body>
                            """,
        name, otp);

    helper.setText(htmlContent, true);
    mailSender.send(message);
  }
}