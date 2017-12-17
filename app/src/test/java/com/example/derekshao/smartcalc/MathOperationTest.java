package com.example.derekshao.smartcalc;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

public class MathOperationTest extends TestCase {

    private ExpressionEvaluator expressionEvaluator;

    public MathOperationTest() {
        setUp();
    }

    protected void setUp() {
        expressionEvaluator = new ExpressionEvaluator();
    }

    protected void tearDown() {
        expressionEvaluator = null;
    }

    public void testOperation() {

        assertEquals("Checking if correctly converts infix string to postfix string",
                expressionEvaluator.infix("9+3x5-3"),
                new ArrayList<String>(Arrays.asList("9","3","5","x","3","-","+")));

        assertEquals("Checking addition",
                expressionEvaluator.postfix_evaluate(expressionEvaluator.infix("9+2")),
                "11");

        assertEquals("Checking subtraction",
                expressionEvaluator.postfix_evaluate(expressionEvaluator.infix("-6-5")),
                "-11");

        assertEquals("Checking multiplication",
                expressionEvaluator.postfix_evaluate(expressionEvaluator.infix("6x2")),
                "12");

        assertEquals("Checking division",
                expressionEvaluator.postfix_evaluate(expressionEvaluator.infix("19/2")),
                "9.5");

        assertEquals("Checking if bedmas principle works properly",
                expressionEvaluator.postfix_evaluate(expressionEvaluator.infix("9+3x5-3")),
                "21");

        assertEquals("Checking decimals",
                expressionEvaluator.postfix_evaluate(expressionEvaluator.infix("6.3x2.5")),
                "15.75");

        assertEquals("Checking exponents",
                expressionEvaluator.postfix_evaluate(expressionEvaluator.infix("4^3")),
                "64");

        tearDown();
    }
}