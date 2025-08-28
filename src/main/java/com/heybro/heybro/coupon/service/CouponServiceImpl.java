package com.heybro.heybro.coupon.service;

import com.heybro.heybro.coupon.domain.Coupon;
import com.heybro.heybro.coupon.domain.CouponType;
import com.heybro.heybro.coupon.dto.response.CouponListResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponResponseDto;
import com.heybro.heybro.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    @Override
    public CouponListResponseDto getCoupons() {
        List<CouponResponseDto> amounts = new ArrayList<>();
        List<CouponResponseDto> products = new ArrayList<>();

        List<Coupon> coupons = couponRepository.findAllByOrderByIdAsc();

        // 쿠폰을 타입에 맞게 분류 : 금액권/상품권
        for (Coupon coupon : coupons) {
            CouponResponseDto dto = CouponResponseDto.builder()
                    .couponId(coupon.getId())
                    .name(coupon.getName())
                    .couponType(coupon.getCouponType())
                    .detailImage(coupon.getDetailImage())
                    .build();

            if (coupon.getCouponType() == CouponType.AMOUNT) amounts.add(dto);
            else if (coupon.getCouponType() == CouponType.PRODUCT) products.add(dto);
        }

        return CouponListResponseDto.builder()
                .amounts(amounts)
                .products(products)
                .build();
    }
}
