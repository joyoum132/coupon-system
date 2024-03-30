package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {
    private final CouponRepository couponRepository;

    public ApplyService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    //쿠폰 발급
    public void apply(Long userId) {
        //쿠폰 개수 가져옴
        long count = couponRepository.count();

        if(count > 100) return;

        couponRepository.save(
                new Coupon(userId)
        );
    }
}
