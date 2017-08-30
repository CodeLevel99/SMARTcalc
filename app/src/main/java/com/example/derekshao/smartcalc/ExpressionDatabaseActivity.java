package com.example.derekshao.smartcalc;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExpressionDatabaseActivity extends AppCompatActivity {

    GestureDetector detector;

    //views
    private ListView listView;

    //firebase
    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private ExpressionAdapter adapter;

    //java
    private ArrayList<MathExp> arrayOfMathExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expression_database);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("expressions");

        implementList();
        attachDatabaseReadListener();
    }

    private void implementList() {

        arrayOfMathExp = new ArrayList<>();

        adapter = new ExpressionAdapter(this, R.layout.item_mathexp, arrayOfMathExp);

        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(adapter);

        listView.setEmptyView(findViewById(R.id.emptyView));

        detector = new GestureDetector(this, new MyGestureDetector());

        View.OnTouchListener gestureListener = new View.OnTouchListener()  {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        };

        listView.setOnTouchListener(gestureListener);
    }

    private void myOnItemClick(int position) {
        //gets equation at position clicked and passes equation back to main activity
        MathExp expression = (MathExp)listView.getItemAtPosition(position);

        Intent result = new Intent();
        result.putExtra("mathexp", expression.getEquation());
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //saves the position of item clicked
            int pos = listView.pointToPosition((int)e.getX(), (int)e.getY());
            myOnItemClick(pos);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //onFling: deletes the selected equation

            int pos = listView.pointToPosition((int)e1.getX(),(int)e1.getY());

            ArrayAdapter adapter = (ArrayAdapter)listView.getAdapter();
            MathExp expToRemove = (MathExp)adapter.getItem(pos);

            Query expressionQuery  = mDatabaseReference.orderByChild("equation").equalTo(expToRemove.getEquation());
            expressionQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot eqSnapshot : dataSnapshot.getChildren()) {
                        eqSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("smartcalc", databaseError.getMessage());
                }
            });

            arrayOfMathExp.remove(expToRemove);
            adapter.notifyDataSetChanged();

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

    private void attachDatabaseReadListener() {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MathExp newExp = dataSnapshot.getValue(MathExp.class);
                    adapter.add(newExp);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    MathExp expRemoved = dataSnapshot.getValue(MathExp.class);
                    String expression = expRemoved.getEquation();
                    Toast.makeText(ExpressionDatabaseActivity.this, expression + " removed.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        }
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }
}
