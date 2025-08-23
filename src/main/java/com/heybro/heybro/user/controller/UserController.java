package com.heybro.heybro.user.controller;

import com.heybro.heybro.onboarding.dto.response.UserTypeResponseDto;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.EmailValidationResponseDto;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "회원", description = "회원 API")
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDto registerUser(@RequestBody UserRegistrationRequestDto requestDto) {
        return userService.registerNewUser(requestDto);
    }

    @Operation(summary = "이메일 중복 체크")
    @GetMapping
    public EmailValidationResponseDto checkEmail(@RequestParam("email") String email) {
        return userService.checkEmail(email);
    }

    @Operation(summary = "회원 유형 조회")
    @GetMapping("/user-types")
    public UserTypeResponseDto getUserType(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserType(userDetails.getUsername());
    }
}
