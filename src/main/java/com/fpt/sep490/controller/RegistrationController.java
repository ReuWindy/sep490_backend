package com.fpt.sep490.controller;

import com.fpt.sep490.security.dto.RegistrationRequest;
import com.fpt.sep490.security.dto.RegistrationResponse;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.utils.GeneralMessageAccessor;
import com.fpt.sep490.utils.SendMail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {
    private final GeneralMessageAccessor generalMessageAccessor;
    private static final String REGISTRATION_SUCCESSFUL = "registration_successful";
    private final UserService userService;
    private final SendMail sendMail;

    @PostMapping
    public ResponseEntity<RegistrationResponse> registrationRequest(@Valid @RequestBody RegistrationRequest registrationRequest) {

        final RegistrationResponse registrationResponse = userService.registration(registrationRequest);
        final String username = registrationRequest.getUsername();
        final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, username);

        if (registrationResponse.getMessage().endsWith(registrationSuccessMessage)) {
            String subject = "[Rice-Hub] Chúc mừng bạn đã đăng ký thành công!";
            String body = createSuccessEmailContent(username);
            sendMail.sendMailRender(registrationRequest.getEmail(), subject, body);
            return ResponseEntity.status(HttpStatus.CREATED).body(registrationResponse);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationResponse);
    }

    private String createSuccessEmailContent(String username) {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Arial', sans-serif; margin: 0; padding: 0; background-color: #f4f7f6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }" +
                ".header { background-color: #4CAF50; padding: 20px; text-align: center; color: white; border-radius: 8px 8px 0 0; }" +
                ".header h2 { margin: 0; font-size: 24px; }" +
                ".body { padding: 20px; line-height: 1.6; font-size: 16px; }" +
                ".footer { text-align: center; font-size: 14px; color: #777; margin-top: 20px; }" +
                ".footer p { margin: 5px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>Chúc mừng " + username + "!</h2>" +
                "</div>" +
                "<div class='body'>" +
                "<p>Chúng tôi vui mừng thông báo bạn đã đăng ký thành công trên hệ thống của chúng tôi.</p>" +
                "<p>Hãy bắt đầu trải nghiệm các tính năng tuyệt vời mà chúng tôi cung cấp.</p>" +
                "<br>" +
                "<p>Trân trọng,</p>" +
                "<p>Đội ngũ hỗ trợ</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Cảm ơn bạn đã chọn chúng tôi!</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}