package dev.haguel.orbistay.factory;

import dev.haguel.orbistay.entity.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Year;

@Slf4j
@Component
public class EmailMessageFactory implements MessageFactory {
    @Value("${frontend.host}")
    private static String frontendHost;

    public String getVerificationMessage(AppUser appUser) {
        String verificationLink = frontendHost + "/verify-email?token=" + appUser.getEmailVerification().getToken();

        String htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Verify Your Email – Orbistay</title>
                    <style>
                      body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f4f4f4;
                        padding: 20px;
                      }
                      .email-wrapper {
                        background: #ffffff;
                        border-radius: 10px;
                        padding: 30px;
                        margin: 20px auto;
                        width: 100%%;
                        max-width: 600px;
                        box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
                      }
                      h1 {
                        color: #2c3e50;
                        font-size: 24px;
                        margin-bottom: 20px;
                      }
                      p {
                        font-size: 16px;
                        color: #555;
                      }
                      a.button {
                        display: inline-block;
                        padding: 12px 24px;
                        font-size: 16px;
                        font-weight: bold;
                        color: #ffffff;
                        background-color: #3498db;
                        border-radius: 5px;
                        text-decoration: none;
                        margin-top: 20px;
                      }
                      a.button:hover {
                        background-color: #2980b9;
                      }
                      .footer {
                        font-size: 12px;
                        color: #888;
                        margin-top: 30px;
                        text-align: center;
                      }
                      .footer a {
                        color: #888;
                        text-decoration: none;
                      }
                      .footer a:hover {
                        text-decoration: underline;
                      }
                    </style>
                  </head>
                  <body>
                    <div class="email-wrapper">
                      <h1>Welcome to Orbistay! 🌟</h1>
                      <p>Hi <strong>%s</strong>,</p>
                      <p>Thank you for signing up for Orbistay! Please verify your email address by clicking the button below:</p>
                      <a href="%s" class="button">Verify Your Email</a>
                      <p>If the button doesn't work, you can also verify your account by copying and pasting the following link into your browser:</p>
                      <p><a href="%s">%s</a></p>
                      <p>This link will expire in 24 hours.</p>
                      <p>If you didn’t sign up for Orbistay, please ignore this email.</p>
                      <div class="footer">
                        <p>Thank you for choosing Orbistay! 🌍</p>
                        <p>&copy; %d Orbistay. All rights reserved.</p>
                        <p><a href="%s">Visit our website</a> | <a href="%s/support">Support</a></p>
                      </div>
                    </div>
                  </body>
                </html>
            """;

        String formattedHtmlContent = String.format(htmlContent,
                appUser.getUsername(),
                verificationLink, verificationLink, verificationLink,
                Year.now().getValue(),
                frontendHost, frontendHost
        );

        return formattedHtmlContent;
    }

    @Override
    public String getResetPasswordMessage(String token) {
        String resetPasswordLink = frontendHost + "/reset-password?token=" + token;

        String htmlContent = """
            <!DOCTYPE html>
            <html lang="en">
              <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reset Your Password – Orbistay</title>
                <style>
                  body {
                    font-family: Arial, sans-serif;
                    line-height: 1.6;
                    color: #333;
                    background-color: #f4f4f4;
                    padding: 20px;
                  }
                  .email-wrapper {
                    background: #ffffff;
                    border-radius: 10px;
                    padding: 30px;
                    margin: 20px auto;
                    width: 100%%;
                    max-width: 600px;
                    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
                  }
                  h1 {
                    color: #2c3e50;
                    font-size: 24px;
                    margin-bottom: 20px;
                  }
                  p {
                    font-size: 16px;
                    color: #555;
                  }
                  a.button {
                    display: inline-block;
                    padding: 12px 24px;
                    font-size: 16px;
                    font-weight: bold;
                    color: #ffffff;
                    background-color: #3498db;
                    border-radius: 5px;
                    text-decoration: none;
                    margin-top: 20px;
                  }
                  a.button:hover {
                    background-color: #2980b9;
                  }
                  .footer {
                    font-size: 12px;
                    color: #888;
                    margin-top: 30px;
                    text-align: center;
                  }
                  .footer a {
                    color: #888;
                    text-decoration: none;
                  }
                  .footer a:hover {
                    text-decoration: underline;
                  }
                </style>
              </head>
              <body>
                <div class="email-wrapper">
                  <h1>Reset Your Password</h1>
                  <p>You recently requested to reset your password. Click the button below to set a new password:</p>
                  <a href="%s" class="button">Reset Password</a>
                  <p>If the button doesn't work, you can also reset your password by copying and pasting the following link into your browser:</p>
                  <p><a href="%s">%s</a></p>
                  <p>This link will expire in 24 hours.</p>
                  <p>If you didn’t request a password reset, please ignore this email.</p>
                  <div class="footer">
                    <p>Thank you for choosing Orbistay! 🌍</p>
                    <p>&copy; %d Orbistay. All rights reserved.</p>
                    <p><a href="%s">Visit our website</a> | <a href="%s/support">Support</a></p>
                  </div>
                </div>
              </body>
            </html>
        """;

        String formattedHtmlContent = String.format(htmlContent,
                resetPasswordLink, resetPasswordLink, resetPasswordLink,
                Year.now().getValue(),
                frontendHost, frontendHost
        );

        return formattedHtmlContent;
    }
}
