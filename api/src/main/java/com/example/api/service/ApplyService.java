package com.example.api.service;

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

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository, CouponCreateProducer couponCreateProducer, AppliedUserRepository appliedUserRepository){
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
        this.appliedUserRepository = appliedUserRepository;
    }

    //쿠폰발급 method
    public void apply(Long userId){
        //long count = couponRepository.count();
        //redis 를 활용한 발급 : 쿠폰 개수가 많을수록 rdb 에 부하를 줄 수 있음.  db 서버의 resource 를 많이 사용함.
        //사용하는 RDB 가 다른 작업도 수행한다면 ? 다른 서비스도 장애, timeout

        //**조건 추가 ) 1명 쿠폰 1개만 발급받도록 하는 방법 (2)
        // 범위로 lock 을 잡고, 처음에 쿠폰 발급여부를 가져와서 판단
        //lock start
        //쿠폰발급 여부
        // if(발급됐다면) return
        // -> consumer 에서 작업을 처리하는 동안,
        // 아직 db 에 저장이 되지 않은 상태에서 재요청이 있다면, 중복 발급 가능하게 됨.

        //**조건 추가 ) 1명 쿠폰 1개만 발급받도록 하는 방법 (3)
        //redis set 이용
        Long apply = appliedUserRepository.add(userId);

        if(apply != 1){
            return;
        }

        Long count = couponCountRepository.increment();

        if (count>100){
            return;
        }
        //couponRepository.save(new Coupon(userId));
        couponCreateProducer.create(userId); //topic 에 userId 를 전송하도록 변경


        //lock end
    }
}
