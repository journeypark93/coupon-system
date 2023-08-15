package com.example.consumer.config;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.beans.BeanProperty;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean //환경설정
    public ConsumerFactory<String, Long> consumerFactory(){
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //서버정보 추가
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1"); //그룹 이름 추가
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean //토픽으로 부터 메시지를 전달받기위한 kafkalistener 를 생성하기 위한 kafkaListenercontainerFactory 생성하는 method
    public ConcurrentKafkaListenerContainerFactory<String, Long> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
