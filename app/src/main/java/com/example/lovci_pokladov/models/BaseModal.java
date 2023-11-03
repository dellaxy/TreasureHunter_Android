package com.example.lovci_pokladov.models;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.lovci_pokladov.R;

public abstract class BaseModal extends LinearLayout {
    protected Context context;
    protected View modalView;
    private PopupWindow popupWindow;
    private boolean isPopupOpen = false;

    public BaseModal(Context context,int resource ) {
        super (context);
        this.context = context;
        modalView = LayoutInflater.from(context).inflate(resource, null);
        popupWindow = new PopupWindow(modalView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
    }

    public void openPopup() {
        if (!isPopupOpen) {
            beforeModalOpen();
            Animation slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_side);
            popupWindow.getContentView().startAnimation(slideInAnimation);
            popupWindow.showAtLocation(popupWindow.getContentView(), Gravity.CENTER, 0, 0);
            isPopupOpen = true;
        }
    }

    public void closePopup() {
        beforeModalClose();
        Animation slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_side);
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                popupWindow.dismiss();
                isPopupOpen = false;
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        popupWindow.getContentView().startAnimation(slideOutAnimation);
    }

    public abstract void beforeModalOpen();
    public abstract void beforeModalClose();


}
