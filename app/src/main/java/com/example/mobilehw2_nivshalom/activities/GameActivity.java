package com.example.mobilehw2_nivshalom.activities;

// Import game constants
import static com.example.mobilehw2_nivshalom.logic.GameUtility.grid_width;
import static com.example.mobilehw2_nivshalom.logic.GameUtility.grid_height;
import static com.example.mobilehw2_nivshalom.logic.GameUtility.lives;

// Import classes
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilehw2_nivshalom.R;
import com.example.mobilehw2_nivshalom.logic.GameUtility;
import com.example.mobilehw2_nivshalom.logic.GameManager;

public class GameActivity extends AppCompatActivity {

    private final GameManager manager = new GameManager(grid_width, grid_height, lives);
    private ImageView[][] grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout grid_main_frame = findViewById(R.id.grid_main_frame);
        grid_main_frame.post(() -> {
            this.grid = GameUtility.CreateGrid(grid_main_frame);
            SetupControls();

            GameUtility.SetupGame(this, manager, grid, R.id.score_text, R.id.lives_text);
        });
    }

    /**
     * Connects all buttons to the correct movement options
     */
    private void SetupControls() {
        Button up = findViewById(R.id.up_btn);
        Button down = findViewById(R.id.down_btn);
        Button right = findViewById(R.id.right_btn);
        Button left = findViewById(R.id.left_btn);

        up.setOnClickListener(view -> manager.SetHunterDirection(GameManager.DIRECTION.UP));
        down.setOnClickListener(view -> manager.SetHunterDirection(GameManager.DIRECTION.DOWN));
        right.setOnClickListener(view -> manager.SetHunterDirection(GameManager.DIRECTION.RIGHT));
        left.setOnClickListener(view -> manager.SetHunterDirection(GameManager.DIRECTION.LEFT));
    }

}
