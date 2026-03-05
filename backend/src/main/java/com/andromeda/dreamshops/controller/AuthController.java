package com.andromeda.dreamshops.controller;

import com.andromeda.dreamshops.request.LoginRequest;
import com.andromeda.dreamshops.request.ResendVerificationRequest;
import com.andromeda.dreamshops.request.VerifyUserRequest;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.response.JwtResponse;
import com.andromeda.dreamshops.security.jwt.JwtUtils;
import com.andromeda.dreamshops.security.user.ShopUserDetails;
import com.andromeda.dreamshops.service.auth.AuthVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final AuthVerificationService verificationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request){
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt);
            return ResponseEntity.ok(new ApiResponse("Login successful", jwtResponse));
        } catch (DisabledException e) {
            return ResponseEntity.status(FORBIDDEN).body(new ApiResponse("Your account is not verified yet. Please Check your email for the verification code"+e.getMessage(), null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // verify email
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestBody VerifyUserRequest request) {
        try {
            verificationService.verifyUser(request);
            return ResponseEntity.ok(new ApiResponse("Account verified successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // resend verification code
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerificationCode(@RequestBody ResendVerificationRequest request) {
        try {
            verificationService.resendVerificationCode(request.getEmail());
            return ResponseEntity.ok(new ApiResponse("Verification code resent successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
