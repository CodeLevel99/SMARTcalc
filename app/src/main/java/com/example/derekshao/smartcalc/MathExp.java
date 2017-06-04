package com.example.derekshao.smartcalc;

import java.util.ArrayList;

public class MathExp {

    private String equation;
    private String date;

    public MathExp() {

    }

    public MathExp(String exp, String date) {
        this.equation = exp;
        this.date = date;
    }

    public String getEquation() {
        return equation;
    }

    public String getDate() {
        return date;
    }


}
