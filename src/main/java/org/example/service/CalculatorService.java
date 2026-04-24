package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
public class CalculatorService {

    @Tool(description = "加法运算")
    public int addOperation(int a, int b) {
        log.info("加法运算函数被调用了:" + a + "," + b );
        return a + b;
    }


    @Tool(description = "乘法运算")
    public int mulOperation(int a, int b) {
        log.info("乘法运算函数被调用了:" + a + "," + b);
        return a * b;
    }

}
