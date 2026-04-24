package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.example.service.CalculatorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
public class CalculatorController {

    @Resource
    private ChatClient chatClient;

    @Resource
    private CalculatorService calculatorService;

    @GetMapping(value = "/ai/calculator", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public String ragJsonText(@RequestParam(value = "userMessage") String userMessage){
        String response = chatClient
                .prompt()
                .system("""
                您是算术计算器的代理。支持加法和乘法运算。
                当需要执行计算时，按以下格式输出函数调用：
                [FUNCTION_CALL: addOperation(a=数字1, b=数字2)]
                或
                [FUNCTION_CALL: mulOperation(m=数字1, n=数字2)]
                然后等待执行结果。最后输出最终答案。
                请讲中文。
                """)
                .user(userMessage)
                .call()
                .content()
                ;

        return processResponse(response);
    }

    private String processResponse(String response) {
        String result = response;

        // 匹配并执行 addOperation
        Pattern addPattern = Pattern.compile("\\[FUNCTION_CALL: addOperation\\(a=(\\d+),\\s*b=(\\d+)\\)\\]");
        Matcher addMatcher = addPattern.matcher(result);
        while (addMatcher.find()) {
            int a = Integer.parseInt(addMatcher.group(1));
            int b = Integer.parseInt(addMatcher.group(2));
            int res = calculatorService.addOperation(a, b);
            result = result.replace(addMatcher.group(0), "计算结果: " + a + " + " + b + " = " + res);
            log.info("执行加法运算: {} + {} = {}", a, b, res);
        }

        // 匹配并执行 mulOperation
        Pattern mulPattern = Pattern.compile("\\[FUNCTION_CALL: mulOperation\\(m=(\\d+),\\s*n=(\\d+)\\)\\]");
        Matcher mulMatcher = mulPattern.matcher(result);
        while (mulMatcher.find()) {
            int m = Integer.parseInt(mulMatcher.group(1));
            int n = Integer.parseInt(mulMatcher.group(2));
            int res = calculatorService.mulOperation(m, n);
            result = result.replace(mulMatcher.group(0), "计算结果: " + m + " × " + n + " = " + res);
            log.info("执行乘法运算: {} × {} = {}", m, n, res);
        }
        return result;
    }

}
