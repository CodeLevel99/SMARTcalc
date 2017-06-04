package com.example.derekshao.smartcalc;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/*
* Sole purpose of this class is so there isn't 4 more switch statements
* when a button is clicked.
* */

public class OperatorButton extends Button {

    public OperatorButton(Context context) {
        super(context);
    }

    public OperatorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
