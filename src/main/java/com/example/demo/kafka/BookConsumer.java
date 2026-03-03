package com.example.demo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookConsumer {

    @KafkaListener(topics = "book-topic", groupId = "book-group")
    public void consume(String message) {
        System.out.println("Consumed message: The name of best book is :" + message);
    }
}