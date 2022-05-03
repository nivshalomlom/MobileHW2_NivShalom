package com.example.mobilehw2_nivshalom.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehw2_nivshalom.R;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LeaderboardsAdapter extends RecyclerView.Adapter<LeaderboardsAdapter.ViewHolder> {

    protected final String SCORE_TEMPLATE;
    protected final String LOCATION_TEMPLATE;

    private ArrayList<LeaderboardsItem> items;
    private Consumer<LeaderboardsItem> onItemClickListener = null;

    public LeaderboardsAdapter(Context ctx, ArrayList<LeaderboardsItem> items) {
        this.items = items;

        this.SCORE_TEMPLATE = ctx.getString(R.string.score_text_label);
        this.LOCATION_TEMPLATE = ctx.getString(R.string.location_text_label);
    }

    public void SetOnItemCLickListener(Consumer<LeaderboardsItem> onItemClickListener) { this.onItemClickListener = onItemClickListener; }

    @NonNull
    @Override
    public LeaderboardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboards_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardsAdapter.ViewHolder holder, int position) {
        LeaderboardsItem item = items.get(position);
        holder.SetScore(item.GetScore());
        holder.SetLocation(item.GetLat(), item.GetLon());

        holder.itemView.setOnClickListener((event) -> {
            if (this.onItemClickListener != null)
                this.onItemClickListener.accept(item);
        });
    }

    @Override
    public int getItemCount() { return this.items.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView score;
        private TextView location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.score = itemView.findViewById(R.id.leaderboards_score);
            this.location = itemView.findViewById(R.id.leaderboards_location);
        }

        public void SetScore(int score) { this.score.setText(String.format(SCORE_TEMPLATE, score)); }

        public void SetLocation(double lat, double lon) { this.location.setText(String.format(LOCATION_TEMPLATE, lat, lon)); }

    }

}
