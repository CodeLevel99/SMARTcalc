package com.example.derekshao.smartcalc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MathOperationTest {

    @Test
    public void algorithm_check() {

        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

        assertEquals("Check if correctly converts infix string to postfix string",
                expressionEvaluator.infix("9+3x5-3"),
                new ArrayList<String>(Arrays.asList("9","3","5","x","3","-","+")));

        assertEquals("Check if 9+3x5-3 evaluates to 21",
                expressionEvaluator.postfix_evaluate(expressionEvaluator.infix("9+3x5-3")),
                "21");
    }
}