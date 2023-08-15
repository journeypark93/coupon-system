package com.example.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long userId;

    //**조건 추가 ) 1명 쿠폰 1개만 발급받도록 하는 방법 (1)
    // userId/ couponType 컬럼 추가, unique key 를 걸어 db level 에서 막는 방법
    // -> 한 유저가 같은 타입의 쿠폰을 여러개 가지는 서비스가 많을 수 있어 실용적이지 않음.
    //private Long couponType;

    public Coupon() {
    }

    public Coupon(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }
}
