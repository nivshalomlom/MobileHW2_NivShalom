package com.example.mobilehw2_nivshalom.activities;

// Import game constants
import static com.example.mobilehw2_nivshalom.logic.GameUtility.grid_height;
import static com.example.mobilehw2_nivshalom.logic.GameUtility.grid_width;
import static com.example.mobilehw2_nivshalom.logic.GameUtility.lives;

// Import classes
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobilehw2_nivshalom.R;
import com.example.mobilehw2_nivshalom.logic.GameManager;
import com.example.mobilehw2_nivshalom.logic.GameUtility;

public class GameSensorActivity extends Activity implements SensorEventListener {

    private final GameManager manager = new GameManager(grid_width, grid_height, lives);
    private ImageView[][] grid;

    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_sensor);

        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        LinearLayout grid_main_frame = findViewById(R.id.sensor_grid_main_frame);
        grid_main_frame.post(() -> {
            this.grid = GameUtility.CreateGrid(grid_main_frame);
            GameUtility.SetupGame(this, manager, grid, R.id.sensor_score_text, R.id.sensor_lives_text);
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];

        if (Math.abs(x) > Math.abs(y)) {
            if (x < 0) this.manager.SetHunterDirection(GameManager.DIRECTION.RIGHT);
            if (x > 0) this.manager.SetHunterDirection(GameManager.DIRECTION.LEFT);
        } else {
            if (y < 0) this.manager.SetHunterDirection(GameManager.DIRECTION.UP);
            if (y > 0) this.manager.SetHunterDirection(GameManager.DIRECTION.DOWN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

}