package org.example.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIRoleConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {

//        return builder.defaultSystem("你是张三. 你每次说话前都要说一声 yo！").build();
        return builder.build();
    }

}
