package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class KimiTestController {
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


    // 提示词
    @GetMapping("/ai/prompt")
    public String prompt(@RequestParam(value = "name",defaultValue = "李四")
                         String name,
                         @RequestParam(value = "voice",defaultValue = "北京")
                         String voice){
        String userText= """
        给我推荐北京的至少三种美食
        """;
        UserMessage userMessage = new UserMessage(userText);
        String systemText= """
        你是一个美食咨询助手，可以帮助人们查询美食信息。
        你的名字是{name},
        你应该用你的名字和{voice}的饮食习惯回复用户的请求。
        """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        //替换占位符
        Message systemMessage = systemPromptTemplate
                .createMessage(Map.of("name", name, "voice", voice));
        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));
        List<Generation> results = chatModel.call(prompt).getResults();
        return results.stream().map(x->x.getOutput().getText()).collect(Collectors.joining(""));
    }






}
