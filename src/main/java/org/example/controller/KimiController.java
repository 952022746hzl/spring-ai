package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@Slf4j
public class KimiController {
    @Resource
    private ChatClient chatClient;

    @Resource
    private ChatModel chatModel;


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


    // 流式传输
    @GetMapping(value = "/ai/kimi/stream",produces="text/html;charset=UTF-8")
    public Flux<String> stream(@RequestParam(value = "message",defaultValue = "hello") String message) {
        return chatClient.prompt().user(message).stream().content();
    }


    // 切换模型
    @GetMapping(value = "/ai/model/chat",produces="text/html;charset=UTF-8")
    public Flux<String> model(@RequestParam(value = "message",defaultValue = "hello") String message) {
//        ChatResponse call = chatModel.call(
//                new Prompt(
//                        message,
//                        OpenAiChatOptions.builder()
//                                //可以更换成其他大模型，如Anthropic3ChatOptions亚马逊
//                                .model("kimi-k2.5")
//                                .temperature(1D)
//                                .build()
//                )
//        );
//        return call.getResult().getOutput().getText();


        return chatClient.prompt()
                .user(message)
                .options(OpenAiChatOptions.builder()
                        .model("kimi-k2.5")
                        .temperature(1D)
                        .build())
                .stream()
                .content();
    }





}
