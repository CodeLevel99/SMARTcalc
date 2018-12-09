package com.example.derekshao.smartcalc;

public class MathExp {

    private String equation;
    private String date;

    public MathExp(String exp, String date) {
        this.equation = exp;
        this.date = date;
    }

    public MathExp() {

    }

    public String getEquation() {
        return equation;
    }

    public String getDate() {
        return date;
    }
}
