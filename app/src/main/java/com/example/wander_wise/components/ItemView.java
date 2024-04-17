package com.example.wander_wise.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wander_wise.R;

public class ItemView extends LinearLayout {
    private String title;
    private Drawable image;

    public ItemView(Context context) {
        super(context);
    }

    public ItemView(Context context, String title, Drawable image) {
        super(context);
        this.title = title;
        this.image = image;
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.layout_item, this);
        ImageView itemImage = (ImageView) findViewById(R.id.itemImage);
        TextView itemText = (TextView) findViewById(R.id.itemText);
        itemImage.setImageDrawable(image);
        itemText.setText(title);
    }
}
