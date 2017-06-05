package com.example.derekshao.smartcalc;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //request codes
    static final int SELECT_EQUATION = 1;

    //views
    TextView inputField;

    //firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseReference;

    //java
    private HashMap<String, Integer> prec = new HashMap<>();
    private ArrayList<MathExp> expressions = new ArrayList<>();
    private boolean dataPushed = false;//prevent multiple copies of same expression being pushed to database
    private boolean operatorInserted = false;//prevents input with operators next to each other
    private boolean enableDatabase = true;//check if user wants to store equations in database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = (TextView)findViewById(R.id.input_field);

        //operator precedence
        prec.put("^", 5);
        prec.put("x", 4);
        prec.put("/", 3);
        prec.put("-", 2);
        prec.put("+", 1);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseDatabase.getReference().child("expressions");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.historyItem:
                Intent intent = new Intent(this, ExpressionDatabaseActivity.class);
                startActivityForResult(intent, SELECT_EQUATION);
                break;
            case R.id.enableDatabaseItem:
                if (enableDatabase == true) {
                    item.setIcon(R.drawable.ic_menu_dont_save);
                    enableDatabase = false;
                    Snackbar.make(findViewById(R.id.activity_main), "Does not store equations.", Snackbar.LENGTH_LONG).show();
                }
                else {
                    item.setIcon(R.drawable.ic_menu_save);
                    enableDatabase = true;
                    Snackbar.make(findViewById(R.id.activity_main), "Stores equations.", Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isHigher(String c, String topStack) {

        //if the operator on top of stack is higher or equal than operator scanned, return true
        return prec.get(topStack) >= prec.get(c);
    }

    public ArrayList<String> infix() {

        String infix = inputField.getText().toString();

        //remove all white spaces
        infix = infix.replaceAll("\\s", "");

        //split each character from string into an array
        String [] infixString = infix.split("(?!^)");

        Stack<String> stack= new Stack<String>();
        ArrayList<String> postfix = new ArrayList<String>();

        String curNumber = "";

        for (String c : infixString) {

            //if character c is an operator
            if (prec.containsKey(c)) {

                //if curNumber == "0", the expression contains an operator as its first character
                if (curNumber.equals("")) {
                    curNumber = "0";
                }

                //curNumber currently contains set of numbers, add to postfix string
                postfix.add(curNumber);
                //reset curNumber string for next set of numbers after the operator
                curNumber = "";

                while (!stack.isEmpty() && isHigher(c, stack.peek())) {
                    //if new operator is higher than the current operator at top of stack

                    postfix.add(stack.pop());
                }
                stack.push(c);
            }
            else {
                //c is an operand or decimal, concat with curNumber string
                curNumber = curNumber.concat(c);
            }
        }

        //add the remaining number
        if (curNumber != null && !curNumber.isEmpty()) {
            postfix.add(curNumber);
        }

        //add the remaining operators
        while (!stack.isEmpty()) {
            postfix.add(stack.pop());
        }

        return postfix;
    }

    public void postfix_evaluate() {
        //postfix evaluator

        ArrayList<String> postfix = infix();

        Stack<String> post = new Stack<String>();

        for (String n : postfix) {

            if (prec.containsKey(n)) {

                String topStack = post.pop();

                double retVal = 0;

                switch(n) {
                    case "^":
                        retVal = Math.pow(Double.parseDouble(post.pop()), Double.parseDouble(topStack));
                        break;
                    case "x":
                        retVal = Double.parseDouble(post.pop()) * Double.parseDouble(topStack);
                        break;
                    case "+":
                        retVal = Double.parseDouble(post.pop()) + Double.parseDouble(topStack);
                        break;
                    case "-":
                        retVal = Double.parseDouble(post.pop()) -  Double.parseDouble(topStack);
                        break;
                    case "/":
                        retVal = Double.parseDouble(post.pop()) / Double.parseDouble(topStack);
                        break;
                    default:
                        break;
                }

                post.push(Double.toString(retVal));
            }
            else {
                //operand
                post.push(n);
            }
        }

        //final calculated value
        String finalValue = post.peek();

        //removes decimal point if .0
        if (finalValue.charAt(finalValue.length() - 1) == '0' && finalValue.charAt(finalValue.length() - 2) == '.') {
            inputField.setText(finalValue.substring(0, finalValue.length() - 2));
        }
        else {
            inputField.setText(finalValue);
        }
    }

    public void calculate() {
        postfix_evaluate();
    }

    public void registerClick(View view) {
        Button button = (Button)view;
        switch (button.getId()) {
            case R.id.numpad_equals:
                if (dataPushed == false && enableDatabase == true) {

                    DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
                    Date date = new Date();

                    final MathExp mathExpression = new MathExp(inputField.getText().toString(), dateFormat.format(date));
                    expressions.add(mathExpression);

                    mFirebaseReference.push().setValue(mathExpression);

                }
                dataPushed = true;

                if (!operatorInserted && inputField.getEditableText().toString().length() > 1) {
                    calculate();
                }
                break;
            case R.id.numpad_clear:
                inputField.setText("0");
                break;
            case R.id.numpad_sign_change:
                if (!inputField.getEditableText().toString().equals("0")) {
                    inputField.getEditableText().insert(0, "-");
                }
                break;
            case R.id.numpad_del:
                if (inputField.getEditableText().toString().length() == 1) {
                    inputField.setText("0");
                }
                else if (!inputField.getEditableText().toString().equals("0")) {
                    inputField.getEditableText().delete(inputField.getEditableText().toString().length()-1,inputField.getEditableText().toString().length());
                }
                break;
            default:
                dataPushed = false;
                if (view instanceof OperatorButton) {
                    if (!inputField.getEditableText().toString().equals("0") && operatorInserted == false) {
                        inputField.append(button.getText().toString());
                        operatorInserted = true;
                    }
                }
                else if (inputField.getEditableText().toString().equals("0")) {
                    inputField.setText(button.getText().toString());
                }
                else {
                    operatorInserted = false;
                    inputField.append(button.getText().toString());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_EQUATION && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String results = extras.getString("mathexp");
            inputField.setText(results);
            dataPushed = true;
        }
    }
}
