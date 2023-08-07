package com.example.api.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    //1. producer instance 를 생성하는데 필요한 설정값들을 설정해야 함.
    //2. 손쉽게 설정값들을 설정할 수 있도록 ProducerFactory 라는 Interface 를 제공해줌.
    //3. ProducerFactory 생성하기 위한 method 생성
    @Bean
    public ProducerFactory<String, Long> producerFactory() {
        //4. 설정값 설정
        Map<String, Object> config = new HashMap<>();
        // 4-1. 서버정보 추가
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 4-2. key serializer class 정보 추가
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 4-3. value serializer class 정보 추가
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    //1. kafka topic 에 데이터 전송하기 위한 kafka template 생성
    @Bean
    public KafkaTemplate<String, Long> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
