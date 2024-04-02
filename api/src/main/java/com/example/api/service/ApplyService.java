package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.AppliedUserRepository;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {
    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedUserRepository appliedUserRepository;

    public ApplyService(
            CouponRepository couponRepository,
            CouponCountRepository couponCountRepository,
            CouponCreateProducer couponCreateProducer,
            AppliedUserRepository appliedUserRepository
            ) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
        this.appliedUserRepository = appliedUserRepository;
    }

    //쿠폰 발급
    //만약 DB가 1분에 100개의 insert만 가능하다면?
    // 동시성은 해결했지만 트래픽에 따라 전체 서버에 영향을 줄 수 있음
    public void apply(Long userId) {
        //쿠폰 개수 가져옴
//        long count = couponRepository.count();

        Long apply = appliedUserRepository.add(userId);
        if(apply != 1) {
            //발급받은 유저
            return;
        }

        //lock start
        //쿠폰 발급 여부
        //if(발급됐다면) return --> 쿠폰 생성은 consumer 에서 하기때문에 간차 발생할 수 있음
        long count = couponCountRepository.increment();

        if(count > 100) return;

        couponCreateProducer.create(userId);
//        couponRepository.save(
//                new Coupon(userId)
//        );

        //lock end --> lock 범위가 넓어서 성능 이슈 발생 가능성
    }
}
