package com.example.city_tours.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.example.city_tours.R;

public class RoundedButton extends AppCompatButton {

    public RoundedButton(@NonNull Context context) {
        super(context);
        setBackgroundResource(R.drawable.rounded_button);
    }

    public RoundedButton(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.rounded_button);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton);
        int backgroundColor = typedArray.getColor(R.styleable.RoundedButton_buttonColor, getResources().getColor(R.color.primary, null));
        int textColor = typedArray.getColor(R.styleable.RoundedButton_buttonTextColor, getResources().getColor(R.color.white, null));

        getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);
        setTextColor(textColor);

        typedArray.recycle();
    }


    public RoundedButton(@NonNull Context context, String text, int backgroundColor, int textColor) {
        super(context);
        setText(text);
        setBackgroundResource(R.drawable.rounded_button);
        getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);
        setBackground(getBackground());
        setTextColor(textColor);
    }
}
