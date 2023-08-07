package com.example.api.service;

import com.example.api.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    public void 한번만응모() {
        applyService.apply(1L);

        long count = couponRepository.count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 여러명응모() throws InterruptedException {
        int threadCount = 1000; //천개 요청, multi-thread

        //병렬작업을 간단하게 할 수 있게 도와주는 자바 api
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        //모든 요청이 끝날때까지 기다림, 다른 thread 에서 수행하는 작업을 기다리도록 도와주는 class
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i=0; i< threadCount; i++){
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long count = couponRepository.count();

        assertThat(count).isEqualTo(100);
        //org.opentest4j.AssertionFailedError:
        //expected: 100L
        //but was: 120L
        //laze condition 이 발생함 -> 2개 이상의 thread 가 공유 데이터에 access 하고 동시에 작업을 하려고 할 때 발생하는 문제
        // 해결방안 1) single thread : 성능 좋지 않음. 먼저 요청 이후, 다른 사람 쿠폰발급이 가능해지므로...
        // 해결방안 2) java synchronized : server 가 여러대이면 동일한 이슈발생
        // 해결방안 3) redis, mysql lock 구현 : 쿠폰 개수에 대한 정확성 > 발급된 쿠폰 개수 조회 --> 쿠폰 생성 까지 lock 걸어서 성능 불이익
        //                                  저장하는데 lock 2초... 그러면 사용자들이 2초를 기다려야 함.
        // 해결방안 4) redis incr 명령어 사용(쿠폰 개수에 대한 정확성만 관리하면 됨.):  key에 대한 value를 1씩 증가시킴.
        //                                    redis 는 single thread 기반으로 laze condition 해결, incr 성능도 빠름. 데이터 정합성도 지킬 수 있음.
    }

}