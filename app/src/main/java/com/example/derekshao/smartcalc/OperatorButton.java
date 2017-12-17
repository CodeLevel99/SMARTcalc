package com.example.derekshao.smartcalc;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatButton;

/*
* Sole purpose of this class is so there isn't 4 more switch statements
* when a button is clicked.
* */

public class OperatorButton extends AppCompatButton {

    public OperatorButton(Context context) {
        super(context);
    }

    public OperatorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
