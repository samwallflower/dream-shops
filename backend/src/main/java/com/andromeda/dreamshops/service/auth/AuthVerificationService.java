package com.andromeda.dreamshops.service.auth;

import com.andromeda.dreamshops.exceptions.GeneralException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.User;
import com.andromeda.dreamshops.repository.UserRepository;
import com.andromeda.dreamshops.request.VerifyUserRequest;
import com.andromeda.dreamshops.service.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthVerificationService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public void sendVerificationCode(User user) {
        String verificationCode = generateVerificationCode();
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiresAt( LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        userRepository.save(user);
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String body = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  .container { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 30px; background-color: #131313; border-radius: 10px; border: 1px solid #e9ecef; }" +
                "  .header { text-align: center; color: #7a7979; margin-bottom: 20px; }" +
                "  .code-box { background-color: #181717; border: 2px dashed #524cc7; padding: 20px; text-align: center; font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #6c63ff; margin: 30px 0; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }" +
                "  .text { color: #555555; font-size: 16px; line-height: 1.5; }" +
                "  .footer { font-size: 12px; color: #999999; text-align: center; margin-top: 30px; border-top: 1px solid #eeeeee; padding-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <h2 class='header'>Welcome to DreamShops!</h2>" +
                "    <p class='text'>Hi there,</p>" +
                "    <p class='text'>Thank you for registering. To complete your setup and secure your account, please enter the verification code below:</p>" +
                "    <div class='code-box'>" + verificationCode + "</div>" +
                "    <p class='text'>This code will safely expire in <strong>15 minutes</strong>. If you did not request this, you can safely ignore this email.</p>" +
                "    <div class='footer'>" +
                "      &copy; " + Year.now().getValue() + " DreamShops. All rights reserved." +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";

        try{
            emailService.sendVerificationEmail(user.getEmail(), subject, body);
        } catch (MessagingException e) {
            throw new GeneralException("Failed to send verification email. Please try again later.");
        }

    }

    public void verifyUser(VerifyUserRequest request) {
        User user = Optional.ofNullable(userRepository.findByEmail(request.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled())
            throw new RuntimeException("Account already verified.");

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Verification code has expired. Please request a new one.");

        if (user.getVerificationCode().equals(request.getVerificationCode())) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);
            userRepository.save(user);
        } else {
            throw new GeneralException("Invalid verification code.");
        }
    }

    public void resendVerificationCode(String email) {
        User user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled())
            throw new GeneralException("Account already verified.");

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        sendVerificationEmail(user);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a random 6-digit code
        return String.valueOf(code);
    }

}
