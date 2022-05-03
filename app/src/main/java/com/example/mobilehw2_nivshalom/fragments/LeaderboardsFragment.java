package com.example.mobilehw2_nivshalom.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehw2_nivshalom.R;
import com.example.mobilehw2_nivshalom.data.LeaderboardsAdapter;
import com.example.mobilehw2_nivshalom.data.LeaderboardsItem;
import com.example.mobilehw2_nivshalom.logic.GameUtility;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LeaderboardsFragment extends Fragment {

    private LeaderboardsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboards, container, false);
        Context ctx = view.getContext();

        ArrayList<LeaderboardsItem> leaderboards = GameUtility.GetLeaderboards(requireActivity());
        this.adapter = new LeaderboardsAdapter(ctx, leaderboards);

        RecyclerView leaderboards_rv = view.findViewById(R.id.leaderboards_list);
        leaderboards_rv.setAdapter(this.adapter);

        DividerItemDecoration div = new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL);
        leaderboards_rv.addItemDecoration(div);

        return view;
    }

    public void SetOnLeaderboardsItemSelected(Consumer<LeaderboardsItem> listener) { this.adapter.SetOnItemCLickListener(listener); }

}
