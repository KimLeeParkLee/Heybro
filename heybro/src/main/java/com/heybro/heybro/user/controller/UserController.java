package com.heybro.heybro.user.controller;

import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.UserRegistrationResponseDto;
import com.heybro.heybro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "회원", description = "회원 API")
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping
    public ResponseEntity<UserRegistrationResponseDto> registerUser(@RequestBody UserRegistrationRequestDto requestDto) {
        UserRegistrationResponseDto responseDto = userService.registerNewUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
