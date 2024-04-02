package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {
    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository, CouponCreateProducer couponCreateProducer) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
    }

    //쿠폰 발급
    //만약 DB가 1분에 100개의 insert만 가능하다면?
    // 동시성은 해결했지만 트래픽에 따라 전체 서버에 영향을 줄 수 있음
    public void apply(Long userId) {
        //쿠폰 개수 가져옴
//        long count = couponRepository.count();
        long count = couponCountRepository.increment();

        if(count > 100) return;

        couponCreateProducer.create(userId);
//        couponRepository.save(
//                new Coupon(userId)
//        );
    }
}
