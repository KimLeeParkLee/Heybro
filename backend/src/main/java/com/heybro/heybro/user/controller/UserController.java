package com.heybro.heybro.user.controller;

import com.heybro.heybro.coupon.dto.response.CouponPurchaseResponseDto;
import com.heybro.heybro.coupon.service.CouponService;
import com.heybro.heybro.user.dto.request.FcmTokenUpdateRequestDto;
import com.heybro.heybro.user.dto.request.PasswordRequestDto;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.*;
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
    private final CouponService couponService;

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

    @Operation(summary = "쿠폰 구매 내역 조회")
    @GetMapping("/coupons/purchases")
    public CouponPurchaseResponseDto getPurchasesByUser(@AuthenticationPrincipal UserDetails userDetails) {
        return couponService.findPurchasesByUser(userDetails.getUsername());
    }

    @Operation(summary = "닉네임 중복 검증")
    @GetMapping("/check-nickname")
    public NicknameAvailableResponseDto getPurchasesByUser(@RequestParam("nickname") String nickname) {
        return userService.isNicknameAvailable(nickname);
    }

    @Operation(summary = "FCM 토큰 업데이트")
    @PatchMapping("/fcm-token")
    public void updateFcmToken(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FcmTokenUpdateRequestDto requestDto) {
        userService.updateFcmToken(userDetails.getUsername(), requestDto.getFcmToken());
    }

    @Operation(summary = "랭킹 조회")
    @GetMapping("/ranking")
    public UserRankingResponseDto getRanking(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserRankings(userDetails.getUsername());
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    public void deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userDetails.getUsername());
    }

    @Operation(summary = "비밀번호 변경")
    @PatchMapping("/password")
    public void updatePassword(@RequestBody PasswordRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        userService.updatePassword(requestDto.getPassword(), userDetails.getUsername());
    }
}
