package com.example.lovci_pokladov.objects;

import static com.example.lovci_pokladov.objects.ConstantsCatalog.DEFAULT_MENU_HEIGHT;
import static com.example.lovci_pokladov.objects.ConstantsCatalog.DEFAULT_MENU_WIDTH;
import static com.example.lovci_pokladov.objects.ConstantsCatalog.OPEN_MENU_HEIGHT;
import static com.example.lovci_pokladov.objects.ConstantsCatalog.OPEN_MENU_WIDTH;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.example.lovci_pokladov.R;

public class AnimatedMenu extends LinearLayout {

    private boolean isMenuOpen = false;
    private CardView cardView;
    private ImageButton menuButton;
    private AnimatedVectorDrawable openMenuDrawable;
    private AnimatedVectorDrawable closeMenuDrawable;

    public AnimatedMenu(Context context) {
        super(context);
        init();
    }

    public AnimatedMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.menu_layout, this, true);

        cardView = findViewById(R.id.menu_card);
        menuButton = findViewById(R.id.menu_button);
        openMenuDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.menu_icon_opened, getContext().getTheme());
        closeMenuDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.menu_icon_closed, getContext().getTheme());

        menuButton.setImageDrawable(closeMenuDrawable);

        menuButton.setOnClickListener(view -> toggleMenu());
    }

    private void toggleMenu() {
        if (openMenuDrawable.isRunning() || closeMenuDrawable.isRunning()) {
            return;
        }
        if (isMenuOpen) {
            menuButton.setImageDrawable(openMenuDrawable);
            openMenuDrawable.start();
            animateCardView(DEFAULT_MENU_WIDTH, DEFAULT_MENU_HEIGHT, true);
        } else {
            menuButton.setImageDrawable(closeMenuDrawable);
            animateCardView(OPEN_MENU_WIDTH, OPEN_MENU_HEIGHT, false);
            closeMenuDrawable.start();
        }
        isMenuOpen = !isMenuOpen;
    }

    private void animateCardView(int targetWidth, int targetHeight, boolean isClosing) {
        int currentWidth = cardView.getWidth();
        int currentHeight = cardView.getHeight();

        ValueAnimator widthAnimator = ValueAnimator.ofInt(currentWidth, targetWidth);
        widthAnimator.setDuration(200);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = cardView.getLayoutParams();
                params.width = animatedValue;
                cardView.setLayoutParams(params);
            }
        });

        ValueAnimator heightAnimator = ValueAnimator.ofInt(currentHeight, targetHeight);
        heightAnimator.setDuration(200);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = cardView.getLayoutParams();
                params.height = animatedValue;
                cardView.setLayoutParams(params);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        if (isClosing) {
            animatorSet.playSequentially(heightAnimator, widthAnimator);
        } else {
            animatorSet.playSequentially(widthAnimator, heightAnimator);
        }
        animatorSet.start();
    }

}
