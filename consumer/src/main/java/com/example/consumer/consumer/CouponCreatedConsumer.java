package com.example.consumer.consumer;

import com.example.consumer.domain.Coupon;
import com.example.consumer.domain.FailedEvent;
import com.example.consumer.repository.CouponRepository;
import com.example.consumer.repository.FailedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;

    private final FailedEventRepository failedEventRepository;

    private final Logger logger = LoggerFactory.getLogger(CouponCreatedConsumer.class);

    public CouponCreatedConsumer(CouponRepository couponRepository, FailedEventRepository failedEventRepository) {
        this.couponRepository = couponRepository;
        this.failedEventRepository = failedEventRepository;
    }


    //kafka사용 시, api 를 직접 사용하는거에 비해 처리량을 조절할 수 있음.
    //db 부하 줄일 수 있음.
    //약간의 텀이 발생할 수 있음.
    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId){
        try{
            couponRepository.save(new Coupon(userId));
        }catch (Exception e){
            //쿠폰을 발급하는 과정에서 error 가 나면, error log 를 남겨놓고, 배치프로그램을 통해 쿠폰을 발급하도록 한다.
            logger.error("failed to create coupon::"+ userId);
            failedEventRepository.save(new FailedEvent(userId));
        }
    }
}
