package com.example.city_tours.services;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.city_tours.R;
import com.example.city_tours.components.ItemView;
import com.example.city_tours.entities.ResourceManager;
import com.example.city_tours.entities.puzzles.Item;

import java.util.ArrayList;


public abstract class FetchManager {
    private Context context;
    private PreferencesManager preferencesManager;
    private final LinearLayout bottomInfoLayout, fetchLayout;
    private ArrayList<Item> foundItems = new ArrayList<>();
    private boolean isItemSelected = false;

    public FetchManager(Context context, LinearLayout bottomInfoLayout, LinearLayout fetchLayout) {
        this.context = context;
        this.preferencesManager = PreferencesManager.getInstance(context);
        this.bottomInfoLayout = bottomInfoLayout;
        this.fetchLayout = fetchLayout;
        init();
    }

    private void init() {
    }

    private void onItemClick(int itemId) {
        Item clickedItem = foundItems.get(itemId);
        if (clickedItem.isCorrectItem()) {
            correctItemSelected();
            toggleItemSelect(false);
        } else {
            if (!isItemSelected) {
                TextView hintText = fetchLayout.findViewById(R.id.wrongItemText);
                isItemSelected = true;
                String previousText = hintText.getText().toString();
                hintText.setText(ResourceManager.getString(R.string.wrongItem));
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    hintText.setText(previousText);
                    isItemSelected = false;
                }, 5000);
            }
        }
    }

    private void openItemsList() {
        GridLayout parentLayout = fetchLayout.findViewById(R.id.itemGridLayout);
        for (Item item : foundItems) {
            ItemView itemView = new ItemView(context, item.getItemName(), item.getImage());
            itemView.setOnClickListener(v -> onItemClick(foundItems.indexOf(item)));

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            parentLayout.addView(itemView, layoutParams);

        }
    }

    public void itemCollected(Item item) {
        foundItems.add(item);
    }

    public void toggleItemSelect(boolean changeViewToQuest) {
        if (changeViewToQuest) {
            if (foundItems.size() > 0) {
                bottomInfoLayout.setVisibility(View.GONE);
                fetchLayout.setVisibility(View.VISIBLE);
                openItemsList();
            }
        } else {
            bottomInfoLayout.setVisibility(View.VISIBLE);
            fetchLayout.setVisibility(View.GONE);
        }
    }


    public abstract void correctItemSelected();

}
