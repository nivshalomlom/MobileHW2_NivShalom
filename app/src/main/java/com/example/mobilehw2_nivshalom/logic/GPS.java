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

import java.util.List;

/**
 * A class to monitor the user's location
 */
public class GPS {

    private LocationManager manager;
    private Context ctx;

    private Location currentLocation;

    /**
     * Creates a new GPS object
     * @param ctx The context creating this GPS
     */
    public GPS(Context ctx) {
        this.manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        this.ctx = ctx;

        this.currentLocation = null;
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, location -> this.currentLocation = location);
            this.IsLocationEnabled();
        }
        else {
            Log.e("ERROR:", "missing location permissions");
            System.exit(1);
        }
    }

    /**
     * @return Returns the current user location
     */
    public Location GetCurrentLocation() {
        // If current location unknown, get last known location
        if (this.currentLocation == null) {
            // Check all providers for most accurate reading
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                List<String> providers = this.manager.getProviders(true);
                Location best_location = null;

                for (String provider : providers)
                {
                    Location test_location = this.manager.getLastKnownLocation(provider);
                    if (test_location == null)
                        continue;

                    if (best_location == null || test_location.getAccuracy() < best_location.getAccuracy())
                        best_location = test_location;
                }

                if (best_location == null) {
                    Log.e("ERROR:", "cannot find location");
                    System.exit(1);
                }
                else return best_location;
            }
            else {
                Log.e("ERROR:", "missing location permissions");
                System.exit(1);
            }
        }
        return this.currentLocation;
    }

    /**
     * Makes sure GPS signal is on
     */
    private void IsLocationEnabled() {
        if (!this.manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.ctx);

            builder.setTitle("Location Management");
            builder.setMessage("Please enable location to use this app!");

            builder.setPositiveButton("Enable location", (dialog, i) -> {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                this.ctx.startActivity(intent);
            });

            builder.setNegativeButton("Cancel", (dialog, i) -> {
                System.exit(1);
                dialog.cancel();
            });

            builder.create().show();
        }
    }

}
