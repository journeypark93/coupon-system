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

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository, CouponCreateProducer couponCreateProducer){
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
    }

    //쿠폰발급 method
    public void apply(Long userId){
        //long count = couponRepository.count();
        //redis 를 활용한 발급 : 쿠폰 개수가 많을수록 rdb 에 부하를 줄 수 있음.  db 서버의 resource 를 많이 사용함.
        //사용하는 RDB 가 다른 작업도 수행한다면 ? 다른 서비스도 장애, timeout
        Long count = couponCountRepository.increment();

        if (count>100){
            return;
        }
        //couponRepository.save(new Coupon(userId));
        couponCreateProducer.create(userId); //topic 에 userId 를 전송하도록 변경
    }
}
