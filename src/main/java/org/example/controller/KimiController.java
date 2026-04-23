package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class KimiController {
    @Resource
    private ChatClient chatClient;

    // 普通调用
    @GetMapping("/ai/kimi/chat")
    public String hello(@RequestParam(value = "message",defaultValue = "hello") String message) {
        log.info("message = {}", message);
        String response = this.chatClient.prompt()
                .user(message)
                .call()
                .content();
        log.info("response = {}", response);
        return response;
    }





}
