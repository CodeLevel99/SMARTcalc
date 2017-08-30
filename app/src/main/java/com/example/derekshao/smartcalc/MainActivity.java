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
import java.util.Date;

import static com.example.derekshao.smartcalc.ExpressionEvaluator.infix;
import static com.example.derekshao.smartcalc.ExpressionEvaluator.postfix_evaluate;

public class MainActivity extends AppCompatActivity {

    //request codes
    public static final int SELECT_EQUATION = 1;

    //views
    private TextView inputField;

    //firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseReference;

    //java
    private boolean dataPushed = false;//prevent multiple copies of same expression being pushed to database
    private boolean operatorInserted = false;//prevents input with operators next to each other
    private boolean enableDatabase = true;//check if user wants to store equations in database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = (TextView)findViewById(R.id.input_field);

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
                if (enableDatabase) {
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

    public String calculate(String expression) {

        return postfix_evaluate(infix(expression));
    }

    public void registerClick(View view) {
        Button button = (Button)view;
        switch (button.getId()) {
            case R.id.numpad_equals:
                if (!dataPushed && enableDatabase) {

                    DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
                    Date date = new Date();

                    final MathExp mathExpression = new MathExp(inputField.getText().toString(), dateFormat.format(date));

                    mFirebaseReference.push().setValue(mathExpression);

                }
                dataPushed = true;

                if (!operatorInserted && inputField.getEditableText().toString().length() > 1) {

                    String evaluated = calculate(inputField.getText().toString());

                    inputField.setText(evaluated);
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
                    if (!inputField.getEditableText().toString().equals("0") && !operatorInserted) {
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
