package com.example.mobilehw2_nivshalom.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Button;

import com.example.mobilehw2_nivshalom.MainActivity;
import com.example.mobilehw2_nivshalom.R;
import com.example.mobilehw2_nivshalom.fragments.LeaderboardsFragment;
import com.example.mobilehw2_nivshalom.logic.GameUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LeaderBoardsActivity extends FragmentActivity implements OnMapReadyCallback {

    private LeaderboardsFragment leaderboardsFragment;
    private Marker prevMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_boards);

        FragmentManager fm = getSupportFragmentManager();
        this.leaderboardsFragment = (LeaderboardsFragment) fm.findFragmentById(R.id.leaderboards_container);
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_fragment);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Button back = findViewById(R.id.leaderboards_back_btn);
        back.setOnClickListener((event) -> GameUtility.GoToActivity(this, MainActivity.class));

        Button clear = findViewById(R.id.leaderboards_clear_btn);
        clear.setOnClickListener((event) -> {
            if (this.prevMarker != null)
                this.prevMarker.remove();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        assert this.leaderboardsFragment != null;

        this.leaderboardsFragment.SetOnLeaderboardsItemSelected((item) -> {
            LatLng user_pos = new LatLng(item.GetLat(), item.GetLon());

            if (this.prevMarker != null)
                this.prevMarker.remove();

            this.prevMarker = googleMap.addMarker(new MarkerOptions().position(user_pos).title("Player Position"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(user_pos));
        });
    }
}