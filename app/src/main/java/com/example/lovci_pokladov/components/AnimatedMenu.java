package com.example.lovci_pokladov.components;

import static com.example.lovci_pokladov.models.ConstantsCatalog.MENU_PAGES;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.objects.Utils;
import com.example.lovci_pokladov.services.MenuClickListener;


public class AnimatedMenu extends LinearLayout {

    private boolean isMenuOpen = false;
    private CardView cardView;
    private ImageButton menuButton;
    private AnimatedVectorDrawable openMenuDrawable;
    private AnimatedVectorDrawable closeMenuDrawable;
    private LinearLayout menuLayout;
    private MenuClickListener menuClickListener;

    public AnimatedMenu(Context context) {
        super(context);
        init();
    }

    public AnimatedMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        menuClickListener = (MenuClickListener) getContext();
        LayoutInflater.from(getContext()).inflate(R.layout.layout_animated_menu, this, true);

        cardView = findViewById(R.id.menu_card);
        menuButton = findViewById(R.id.menu_button);
        menuLayout = findViewById(R.id.menu_layout);

        addButtonsToLayout();
        openMenuDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.menu_icon_opened, getContext().getTheme());
        closeMenuDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.menu_icon_closed, getContext().getTheme());

        menuButton.setImageDrawable(closeMenuDrawable);
        menuButton.setOnClickListener(view -> toggleMenu());

    }

    private void addButtonsToLayout(){
        for (MENU_PAGES page : MENU_PAGES.values()) {
            RoundedButton button = new RoundedButton(getContext(), page.getPageName(), getResources().getColor(R.color.secondary_light), Color.BLACK);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.DEFAULT_MENU)
            );
            layoutParams.setMargins(0, 0, 0, (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));

            button.setLayoutParams(layoutParams);
            button.setOnClickListener(view -> {
                if (Utils.isNotNull(menuClickListener)) {
                    menuClickListener.onMenuItemClick(page.getFragmentClass());
                }
                toggleMenu();
            });

            menuLayout.addView(button);
        }
    }

    private void toggleMenu() {
        if (openMenuDrawable.isRunning() || closeMenuDrawable.isRunning()) {
            return;
        }
        if (isMenuOpen) {
            menuButton.setImageDrawable(openMenuDrawable);
            openMenuDrawable.start();
            animateCardView((int) getResources().getDimension(R.dimen.DEFAULT_MENU), true);
        } else {
            menuButton.setImageDrawable(closeMenuDrawable);
            animateCardView((int) getResources().getDimension(R.dimen.OPENED_MENU_WIDTH), false);
            closeMenuDrawable.start();
        }
        isMenuOpen = !isMenuOpen;
    }

    private void animateCardView(int targetWidth, boolean isClosing) {
        int currentWidth = cardView.getWidth(), currentHeight = cardView.getHeight();
        int targetHeight;
        long duration = 200;

        // when the menu is being opened, I want the height to match the content.
        //But since it's animated I can't use wrap_content, so I measure the height of the content.
        if (isClosing) {
            targetHeight = (int) getResources().getDimension(R.dimen.DEFAULT_MENU);
        } else {
            cardView.measure(View.MeasureSpec.makeMeasureSpec(cardView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            targetHeight = cardView.getMeasuredHeight();
        }

        ValueAnimator widthAnimator = createAnimator(currentWidth, targetWidth,duration, animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = cardView.getLayoutParams();
            params.width = animatedValue;
            cardView.setLayoutParams(params);
        });

        ValueAnimator heightAnimator = createAnimator(currentHeight, targetHeight, duration, animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = cardView.getLayoutParams();
            params.height = animatedValue;
            cardView.setLayoutParams(params);
        });

        AnimatorSet animatorSet = new AnimatorSet();
        if(isClosing) {
            animatorSet.playSequentially(heightAnimator, widthAnimator);
        } else {
            animatorSet.playSequentially(widthAnimator, heightAnimator);
        }
        animatorSet.start();
    }

    private ValueAnimator createAnimator(int startValue, int endValue,long duration ,ValueAnimator.AnimatorUpdateListener updateListener) {
        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            updateListener.onAnimationUpdate(animation);
        });
        return animator;
    }

}
