package com.example.lovci_pokladov.objects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovci_pokladov.R;

import java.util.ArrayList;
import java.util.List;

public class CheckpointAdapter extends RecyclerView.Adapter<CheckpointAdapter.CheckpointViewHolder> {
    private List<String> checkpoints;

    public CheckpointAdapter() {
        checkpoints = new ArrayList<>();
    }

    public void addCheckpoint(String checkpoint) {
        checkpoints.add(checkpoint);
    }

    @NonNull
    @Override
    public CheckpointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_checkpoint_card, parent, false);
        return new CheckpointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckpointViewHolder holder, int position) {
        String checkpointText = checkpoints.get(position);
        holder.bind(checkpointText);
    }

    @Override
    public int getItemCount() {
        return checkpoints.size();
    }

    public static class CheckpointViewHolder extends RecyclerView.ViewHolder {
        TextView checkpointDescription;

        public CheckpointViewHolder(View itemView) {
            super(itemView);
            checkpointDescription = itemView.findViewById(R.id.checkpointDescription);
        }

        public void bind(String checkpointText) {
            checkpointDescription.setText(checkpointText);
        }
    }
}

