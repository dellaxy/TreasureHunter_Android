package com.example.lovci_pokladov.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lovci_pokladov.R;
import com.example.lovci_pokladov.entities.Region;
import com.example.lovci_pokladov.objects.PolygonView;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

public class RegionAdapter extends ArrayAdapter<Region> {

    public RegionAdapter(Context context, List<Region> regions) {
        super(context, 0, regions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.region_card, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.card_item_title);
        PolygonView polygonView = convertView.findViewById(R.id.polygonView);

        Region region = getItem(position);
        PolygonOptions polygonOptions = region.getRegionArea();

        itemText.setText(region.getName());

        polygonView.setPolygonOptions(polygonOptions);

        return convertView;
    }
}


