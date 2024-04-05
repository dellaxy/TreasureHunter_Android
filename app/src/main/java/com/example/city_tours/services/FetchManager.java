package com.example.city_tours.services;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.city_tours.R;
import com.example.city_tours.entities.puzzles.Item;

import java.util.ArrayList;


public abstract class FetchManager {
    private Context context;
    private PreferencesManager preferencesManager;
    private final LinearLayout bottomInfoLayout, fetchLayout;
    private ArrayList<Item> foundItems = new ArrayList<>();

    public FetchManager(Context context, LinearLayout bottomInfoLayout, LinearLayout fetchLayout) {
        this.context = context;
        this.preferencesManager = PreferencesManager.getInstance(context);
        this.bottomInfoLayout = bottomInfoLayout;
        this.fetchLayout = fetchLayout;
        init();
    }

    private void init() {
        RecyclerView recyclerView = fetchLayout.findViewById(R.id.fetch_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
    }

    private void onItemClick(int itemId) {
        Item clickedItem = foundItems.get(itemId);
        if (clickedItem.isCorrectItem()) {
            correctItemSelected();
        }
    }

    public void itemCollected(Item item) {
        foundItems.add(item);
    }

    public void toggleItemSelect(boolean changeViewToQuest) {
        for (Item item : foundItems) {
            Log.d("Item", item.getItemName());
        }
        if (changeViewToQuest) {
            if (foundItems.size() > 0) {
                bottomInfoLayout.setVisibility(View.GONE);
                fetchLayout.setVisibility(View.VISIBLE);
            }
        } else {
            bottomInfoLayout.setVisibility(View.VISIBLE);
            fetchLayout.setVisibility(View.GONE);
        }
    }

    public abstract void correctItemSelected();

}
