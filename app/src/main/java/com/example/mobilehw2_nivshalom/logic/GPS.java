package com.example.mobilehw2_nivshalom.logic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

/**
 * A class to monitor the user's location
 */
public class GPS {

    private LocationManager manager;
    private Location currentLocation;

    public GPS(Context ctx) {
        this.manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        this.currentLocation = null;

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, location -> { this.currentLocation = location; });
            this.IsLocationEnabled(ctx);
        }
        else {
            Log.e("ERROR:", "missing location permissions");
            System.exit(1);
        }
    }

    public Location GetCurrentLocation() { return this.currentLocation; }

    private void IsLocationEnabled(Context ctx) {
        if (!this.manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

            builder.setTitle("Location Management");
            builder.setMessage("Please enable location to use this app!");

            builder.setPositiveButton("Enable location", (dialog, i) -> {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ctx.startActivity(intent);
            });

            builder.setNegativeButton("Cancel", (dialog, i) -> {
                System.exit(1);
                dialog.cancel();
            });

            builder.create().show();
        }
    }

}
