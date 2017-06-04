package com.example.derekshao.smartcalc;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ExpressionAdapter extends ArrayAdapter<MathExp> {
    public ExpressionAdapter(Context context, int resource, List<MathExp> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MathExp mathExp = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mathexp, parent, false);
        }

        TextView math = (TextView) convertView.findViewById(R.id.expDisplay);
        TextView date = (TextView) convertView.findViewById(R.id.dateDisplay);

        math.setText(mathExp.getEquation());
        date.setText(mathExp.getDate());

        return convertView;
    }
}
