package com.heybro.heybro.coupon;

import com.heybro.heybro.coupon.domain.Coupon;
import com.heybro.heybro.coupon.domain.CouponType;
import com.heybro.heybro.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class CouponDataInitializer implements CommandLineRunner {
    private final CouponRepository couponRepository;
    private final String COUPON_CODE_PREFIX = "BR";

    @Override
    public void run(String... args) throws Exception {
        // 애플리케이션 시작 시점에 코드 실행
        if (couponRepository.count() > 0) {
            return;
        }

        // 금액권 저장하기
        couponRepository.save(Coupon.builder()
                .name("1000원 금액권")
                .couponType(CouponType.AMOUNT)
                .price(1000)
                .code(generateCouponCode())
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/0339a8b9-bd94-42da-b8fa-5688076c651c.png")
                .build());

        couponRepository.save(Coupon.builder()
                .name("3000원 금액권")
                .couponType(CouponType.AMOUNT)
                .price(3000)
                .code(generateCouponCode())
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/0339a8b9-bd94-42da-b8fa-5688076c651c.png")
                .build());

        couponRepository.save(Coupon.builder()
                .name("5000원 금액권")
                .couponType(CouponType.AMOUNT)
                .price(5000)
                .code(generateCouponCode())
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/0339a8b9-bd94-42da-b8fa-5688076c651c.png")
                .build());

        couponRepository.save(Coupon.builder()
                .name("10000원 금액권")
                .couponType(CouponType.AMOUNT)
                .price(10000)
                .code(generateCouponCode())
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/0339a8b9-bd94-42da-b8fa-5688076c651c.png")
                .build());

        // 상품권 저장하기
        couponRepository.save(Coupon.builder()
                .name("올인원 퍼퓸 파워바")
                .couponType(CouponType.PRODUCT)
                .price(13000)
                .code(generateCouponCode())
                .detailImage("https://drive.google.com/file/d/1Jw8ZHzWKHsXD1E7so-bCXt3vkgRj0Hwm/view?usp=sharing")
                .build());

        couponRepository.save(Coupon.builder()
                .name("올인원 쿨링바")
                .couponType(CouponType.PRODUCT)
                .price(13000)
                .code(generateCouponCode())
                .detailImage("https://drive.google.com/file/d/19zPPCwbkp4aYQwWzN-HKCjsP8MMsAE1E/view?usp=sharing")
                .build());

        couponRepository.save(Coupon.builder()
                .name("워터맥스 선젤")
                .couponType(CouponType.PRODUCT)
                .price(21000)
                .code(generateCouponCode())
                .detailImage("https://drive.google.com/file/d/1G2apskepkEfRM95dVHKDulkrPv_F-JBN/view?usp=sharing")
                .build());

        couponRepository.save(Coupon.builder()
                .name("카페인 트리플 블랙 샴푸")
                .couponType(CouponType.PRODUCT)
                .price(23000)
                .code(generateCouponCode())
                .detailImage("https://drive.google.com/file/d/1ZUh6FYT6DpPL662Q1NeedR8xB6RZVWHw/view?usp=sharing")
                .build());

        couponRepository.save(Coupon.builder()
                .name("맨즈 브라운 올인원 로션")
                .couponType(CouponType.PRODUCT)
                .price(28000)
                .code(generateCouponCode())
                .detailImage("https://drive.google.com/file/d/1yDbnmT622_k9uezmXwuy45fmB18AUBhd/view?usp=sharing")
                .build());
    }

    private String generateCouponCode() {
        Random random = new Random();
        // 0부터 999999 사이의 숫자를 랜덤으로 생성
        int number = random.nextInt(1000000);
        // 6자리로 포맷팅 (앞자리가 비면 0으로 채움)
        return String.format("%s%06d", COUPON_CODE_PREFIX, number);
    }
}
