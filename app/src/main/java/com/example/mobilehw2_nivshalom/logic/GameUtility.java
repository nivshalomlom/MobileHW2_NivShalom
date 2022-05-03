package com.example.mobilehw2_nivshalom.logic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobilehw2_nivshalom.MainActivity;
import com.example.mobilehw2_nivshalom.R;
import com.example.mobilehw2_nivshalom.data.LeaderboardsItem;

import java.util.ArrayList;

/**
 * A class for utility methods used throughout the project
 */
public class GameUtility {

    private static final Handler handler = new Handler();

    public static final int CAVEMAN_IMG = R.drawable.caveman;
    public static final int WOLF_IMG = R.drawable.wolf;
    public static final int COIN_IMG = R.drawable.coin;

    public static int grid_width = 5;
    public static int grid_height = 8;
    public static int lives = 3;

    /**
     * Returns the current leaderboards
     * @param ctx The context that is calling this method
     * @return The leaderboards
     */
    public static ArrayList<LeaderboardsItem> GetLeaderboards(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (sharedPref.contains("leaderboards")) {
            ArrayList<LeaderboardsItem> output = new ArrayList<LeaderboardsItem>();

            for (String item : sharedPref.getString("leaderboards", "").split("\\|")) {
                String[] values = item.split(",");
                output.add(new LeaderboardsItem(
                        Integer.parseInt(values[0]),
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2])
                ));
            }

            return output;
        }
        else return new ArrayList<>();
    }

    /**
     * Saves a given leaderboards in the SharedPreferences
     * @param ctx The context that is calling this method
     * @param leaderboards The leaderboards
     */
    public static void SaveLeaderboards(Context ctx, ArrayList<LeaderboardsItem> leaderboards) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        StringBuilder leaderboards_str = new StringBuilder();
        for (LeaderboardsItem item : leaderboards) {
            leaderboards_str.append(item.GetScore()).append(",");
            leaderboards_str.append(item.GetLat()).append(",");
            leaderboards_str.append(item.GetLon()).append("|");
        }

        leaderboards_str.deleteCharAt(leaderboards_str.length() - 1);
        editor.putString("leaderboards", leaderboards_str.toString());

        editor.apply();
    }

    /**
     * Copy a int array into another array
     * @param destination The array to copy to
     * @param source The array to copy from
     */
    private static void copyArr(int[] destination, int[] source) {
        if (destination.length != source.length)
            return;

        for (int i = 0; i < source.length; i++)
            destination[i] = source[i];
    }

    /**
     * Preform a game tick using the given parameters
     * @param manager The game manager
     * @param grid The UI game grid
     * @param hunter_pos The hunter position on the last tick
     * @param wolf_pos The wolf position on the last tick
     * @param coin_pos The coin position on the last tick
     * @param score_text The text view to display the score text
     * @param lives_text The text view to display the lives text
     */
    private static void GameTick(GameManager manager, ImageView[][] grid, int[] hunter_pos, int[] wolf_pos, int[] coin_pos, TextView score_text, TextView lives_text) {
        manager.Tick();

        int[] h_pos = manager.GetHunterPosition();
        int[] w_pos = manager.GetWolfPosition();
        int[] c_pos = manager.GetCoinPosition();

        if (coin_pos[0] != -1)
            grid[coin_pos[0]][coin_pos[1]].setImageResource(0);

        if (c_pos[0] != -1)
            grid[c_pos[0]][c_pos[1]].setImageResource(COIN_IMG);

        GameUtility.MoveIMG(grid, hunter_pos, h_pos, CAVEMAN_IMG);
        GameUtility.MoveIMG(grid, wolf_pos, w_pos, WOLF_IMG);

        copyArr(hunter_pos, h_pos);
        copyArr(wolf_pos, w_pos);
        copyArr(coin_pos, c_pos);
    }

    /**
     * Generate a image view grid in a given linear layout
     * @param grid_main_frame The grid container
     * @return The image view grid that was created
     */
    public static ImageView[][] CreateGrid(LinearLayout grid_main_frame) {
        // Parameters for grid cell size
        LinearLayout.LayoutParams cell_params = new LinearLayout.LayoutParams(
                grid_main_frame.getWidth() / grid_width,
                grid_main_frame.getHeight() / grid_height
        );

        // Draw the grid
        ImageView[][] grid = new ImageView[grid_width][grid_height];
        for (int j = 0; j < grid_height; j++)
        {
            LinearLayout row = new LinearLayout(grid_main_frame.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < grid_width; i++)
            {
                grid[i][j] = new ImageView(row.getContext());

                grid[i][j].setBackgroundResource(R.drawable.imageview_border);
                grid[i][j].setLayoutParams(cell_params);

                row.addView(grid[i][j]);
            }

            grid_main_frame.addView(row);
        }

        return grid;
    }

    /**
     * Moves a given image on a given grid
     * @param grid The grid to move on
     * @param from The move start position
     * @param to The move end position
     * @param image The resource image to move
     */
    public static void MoveIMG(ImageView[][] grid, int[] from, int[] to, int image) {
        grid[from[0]][from[1]].setImageResource(0);
        grid[to[0]][to[1]].setImageResource(image);
    }

    /**
     * Creates a clock to preform game ticks in a given delay
     * Note: the clock stops running when the player runs out of lives
     * @param ctx The activity requesting the clock
     * @param tick_delay The delay between ticks in milliseconds
     * @param manager The game manager
     * @param grid The UI game grid
     * @param score_text The text view to display the score text
     * @param lives_text The text view to display the lives text
     * @param on_game_over A method to run the game is over
     */
    public static void StartGameClock(Context ctx, long tick_delay, GameManager manager, ImageView[][] grid, TextView score_text, TextView lives_text, Runnable on_game_over) {
        Resources res = ctx.getResources();
        String score_label = res.getString(R.string.score_text_label);
        String lives_label = res.getString(R.string.lives_text_label);

        score_text.setText(String.format(score_label, manager.GetScore()));
        lives_text.setText(String.format(lives_label, lives));

        int[] hunter_pos = manager.GetHunterPosition();
        grid[hunter_pos[0]][hunter_pos[1]].setImageResource(CAVEMAN_IMG);

        int[] wolf_pos = manager.GetWolfPosition();
        grid[wolf_pos[0]][wolf_pos[1]].setImageResource(WOLF_IMG);

        int[] coin_pos = manager.GetCoinPosition();
        handler.postDelayed(new Runnable() {
            public void run() {
                GameUtility.GameTick(manager, grid, hunter_pos, wolf_pos, coin_pos, score_text, lives_text);

                score_text.setText(String.format(score_label, manager.GetScore()));
                lives_text.setText(String.format(lives_label, manager.GetRemainingLives()));

                if (manager.GetRemainingLives() > 0)
                    handler.postDelayed(this, tick_delay);
                else on_game_over.run();
            }
        }, tick_delay);
    }

    /**
     * Plays the given sound in a given context
     * @param ctx The context to play the sound in
     * @param sound_resource The sound to play
     */
    public static void PlaySound(Context ctx, int sound_resource) {
        MediaPlayer mp = MediaPlayer.create(ctx, sound_resource);
        mp.start();
    }

    /**
     * Launches a given activity
     * @param ctx The context that called this method
     * @param destination The target activity
     * @param <T> The target activity type
     */
    public static <T extends Activity> void GoToActivity(Context ctx, Class<T> destination) {
        Intent intent = new Intent(ctx, destination);

        ((Activity) ctx).finish();
        ctx.startActivity(intent);
    }

    /**
     * Setups and operates the game in a given activity
     * @param view The activity hosting the game
     * @param manager The game manager
     * @param grid The UI grid
     * @param score_txt_id The score text view
     * @param lives_txt_id The lives text view
     */
    public static void SetupGame(Activity view, GameManager manager, ImageView[][] grid, int score_txt_id, int lives_txt_id) {
        // Add sounds to hit and coin pickup
        manager.SetOnHitListener(() -> GameUtility.PlaySound(view, R.raw.hit));
        manager.SetOnCoinPickupListener(() -> GameUtility.PlaySound(view, R.raw.pickup));

        // Get the UI text views
        TextView score = view.findViewById(score_txt_id);
        TextView lives = view.findViewById(lives_txt_id);

        // Get leaderboards
        ArrayList<LeaderboardsItem> temp = GetLeaderboards(view);
        final ArrayList<LeaderboardsItem> leaderboards = temp == null ? (new ArrayList<>()) : temp;

        // Handle game over popup
        Runnable on_game_over = new Runnable() {
            @Override
            public void run() {
                // Inflate popup
                Dialog popup = new Dialog(view);
                popup.setContentView(R.layout.end_game_popup);

                // Setup buttons
                Button retry = popup.findViewById(R.id.retry_btn);
                Button menu = popup.findViewById(R.id.return_btn);

                // Save score in leaderboards
                Location location = MainActivity.gps.GetCurrentLocation();
                leaderboards.add(new LeaderboardsItem(
                        manager.GetScore(),
                        location.getLatitude(),
                        location.getLongitude()
                ));

                // Setup popup buttons
                retry.setOnClickListener((event) -> {
                    int[] w_pos = manager.GetWolfPosition();
                    int[] c_pos = manager.GetCoinPosition();

                    grid[w_pos[0]][w_pos[1]].setImageResource(0);
                    if (c_pos[0] != -1)
                        grid[c_pos[0]][c_pos[1]].setImageResource(0);

                    manager.ResetGame();
                    popup.dismiss();

                    GameUtility.StartGameClock(view, 1000, manager, grid, score, lives, this);
                });

                menu.setOnClickListener((event) -> {
                    leaderboards.sort((a, b) -> b.GetScore() - a.GetScore());
                    int sublist_len = Math.min(leaderboards.size(), 10);

                    ArrayList<LeaderboardsItem> sublist = new ArrayList<>();
                    for (int i = 0; i < sublist_len; i++)
                        sublist.add(leaderboards.get(i));

                    SaveLeaderboards(view, sublist);

                    popup.dismiss();
                    GameUtility.GoToActivity(view, MainActivity.class);
                });

                // Show popup
                popup.show();
            }
        };

        GameUtility.StartGameClock(view, 1000, manager, grid, score, lives, on_game_over);
    }

}
