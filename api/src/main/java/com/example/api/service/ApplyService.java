package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {
    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
    }

    //쿠폰 발급
    public void apply(Long userId) {
        //쿠폰 개수 가져옴
//        long count = couponRepository.count();
        long count = couponCountRepository.increment();

        if(count > 100) return;

        couponRepository.save(
                new Coupon(userId)
        );
    }
}
