package com.example.lovci_pokladov.objects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;

import com.example.lovci_pokladov.R;

public class AnimatedMenuButton extends androidx.appcompat.widget.AppCompatImageButton {

    private boolean isMenuOpen = false;
    private AnimatedVectorDrawable openMenuDrawable;
    private AnimatedVectorDrawable closeMenuDrawable;

    public AnimatedMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        openMenuDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.menu_icon_opened, getContext().getTheme());
        closeMenuDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.menu_icon_closed, getContext().getTheme());

        setBackgroundColor(Color.TRANSPARENT);
        setImageDrawable(closeMenuDrawable);

        setOnClickListener(view -> {
            if (openMenuDrawable.isRunning() || closeMenuDrawable.isRunning()) {
                return;
            }
            if (isMenuOpen) {
                setImageDrawable(openMenuDrawable);
                openMenuDrawable.start();
            } else {
                setImageDrawable(closeMenuDrawable);
                closeMenuDrawable.start();
            }
            isMenuOpen = !isMenuOpen;
        });
    }
}