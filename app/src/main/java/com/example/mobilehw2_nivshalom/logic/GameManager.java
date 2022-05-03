package com.example.mobilehw2_nivshalom.logic;

import java.util.Random;

/**
 * A class to manage a game instance
 */
public class GameManager {

    // Constants
    private static final Random rand = new Random();
    private static final int COIN_SCORE_REWARD = 5;
    private static final int COIN_TTL = 10;
    private static final float COIN_GEN_CHANCE = 0.3f;

    public enum DIRECTION {
        DOWN,
        RIGHT,
        UP,
        LEFT
    }

    // Game data
    private final int[] dimensions;
    private final int max_lives;

    // Entity positions
    private final int[] hunter_pos;
    private final int[] wolf_pos;
    private final int[] coin_pos;

    // Internal variables
    private int hunter_dir;
    private int lives;
    private int score;
    private int coin_ttl;

    // Listeners
    private Runnable on_hit;
    private Runnable on_coin_pickup;

    /**
     * Create a new GameManager object
     * @param width The amount of cells on the x-axis
     * @param height The amount of cells on the y-axis
     * @param lives The amount of lives the player has before game over
     */
    public GameManager(int width, int height, int lives) {
        this.dimensions = new int[] {width, height};
        this.max_lives = lives;

        this.hunter_pos = new int[2];
        this.wolf_pos = new int[2];
        this.coin_pos = new int[] {-1, -1};
        this.RandomizePositions();

        this.hunter_dir = rand.nextInt(4);
        this.lives = lives;
        this.score = 0;

        this.on_hit = null;
    }

    /**
     * Generates random positions for the game entities
     */
    private void RandomizePositions() {
        int mid_width = this.dimensions[0] / 2;

        this.hunter_pos[0] = rand.nextInt(mid_width);
        this.hunter_pos[1] = rand.nextInt(this.dimensions[1]);

        this.wolf_pos[0] = mid_width + rand.nextInt(mid_width);
        this.wolf_pos[1] = rand.nextInt(this.dimensions[1]);
    }

    /**
     * Moves the hunter according the last provided direction
     */
    private void MoveHunter() {
        int x_step = hunter_dir % 2, y_step = 1 - (hunter_dir % 2);
        if (this.hunter_dir > 1) {
            x_step *= -1;
            y_step *= -1;
        }

        int new_x = this.hunter_pos[0] + x_step;
        int new_y = this.hunter_pos[1] + y_step;

        if (new_x < 0 || new_x >= this.dimensions[0] || new_y < 0 || new_y >= this.dimensions[1])
            return;

        this.hunter_pos[0] = new_x;
        this.hunter_pos[1] = new_y;
    }

    /**
     * Moves the wolf towards the hunter
     */
    private void MoveWolf() {
        // Compute direction to hunter
        int x_step = (int) Math.signum(this.hunter_pos[0] - this.wolf_pos[0]);
        int y_step = (int) Math.signum(this.hunter_pos[1] - this.wolf_pos[1]);

        // Choose axis of movement
        boolean axis = rand.nextBoolean();
        if (axis && x_step != 0)
            this.wolf_pos[0] += x_step;
        else if (y_step != 0)
            this.wolf_pos[1] += y_step;
        else
            this.wolf_pos[0] += x_step;
    }

    /**
     * Detect and resolve collisions between the game entities
     * @return True if collision occurred between the hunter and the wolf, false otherwise
     */
    private boolean HandleCollision() {
        boolean wolf_collision = this.hunter_pos[0] == this.wolf_pos[0] && this.hunter_pos[1] == this.wolf_pos[1];
        if (wolf_collision && --this.lives > 0)
        {
            this.hunter_dir = rand.nextInt(4);
            this.RandomizePositions();

            if (this.on_hit != null)
                this.on_hit.run();
        }

        boolean coin_collision = this.hunter_pos[0] == this.coin_pos[0] && this.hunter_pos[1] == this.coin_pos[1];
        if (coin_collision)
        {
            this.score += COIN_SCORE_REWARD;
            this.coin_pos[0] = -1;
            this.coin_pos[1] = -1;

            if (this.on_coin_pickup != null)
                this.on_coin_pickup.run();
        }

        return wolf_collision;
    }

    /**
     * Progress the game by one tick, if the game is not over
     */
    public void Tick() {
        if (this.IsGameOver())
            return;

        this.MoveHunter();
        if (this.HandleCollision())
            return;

        this.MoveWolf();
        if (!this.HandleCollision())
            this.score++;

        if (this.coin_pos[0] == -1 && Math.random() < COIN_GEN_CHANCE) {
            this.coin_ttl = COIN_TTL;

            this.coin_pos[0] = rand.nextInt(this.dimensions[0]);
            this.coin_pos[1] = rand.nextInt(this.dimensions[1]);
        }
        else if (--this.coin_ttl == 0) {
            this.coin_pos[0] = -1;
            this.coin_pos[1] = -1;
        }
    }

    /**
     * Resets the game
     */
    public void ResetGame() {
        this.hunter_dir = rand.nextInt(4);
        this.RandomizePositions();

        this.lives = this.max_lives;
        this.score = 0;

        this.coin_pos[0] = -1;
        this.coin_pos[1] = -1;
    }

    /**
     * @return The position of the hunter on the game board
     */
    public int[] GetHunterPosition() { return new int[] {this.hunter_pos[0], this.hunter_pos[1]}; }

    /**
     * @return The position of the wolf on the game board
     */
    public int[] GetWolfPosition() { return new int[] {this.wolf_pos[0], this.wolf_pos[1]}; }

    /**
     * @return The position of the coin on the game board
     */
    public int[] GetCoinPosition() { return new int[] {this.coin_pos[0], this.coin_pos[1]}; }

    /**
     * @return True if game is over, false otherwise
     */
    public boolean IsGameOver() { return this.lives == 0; }

    /**
     * @return The amount of lives left before game over
     */
    public int GetRemainingLives() { return this.lives; }

    /**
     * @return The score the player accumulated by now
     */
    public int GetScore() { return this.score; }

    /**
     * Sets the hunter's movement direction
     * @param direction A direction value
     */
    public void SetHunterDirection(DIRECTION direction) { this.hunter_dir = direction.ordinal(); }

    /**
     * @param on_hit A method to run every time the player gets hit
     */
    public void SetOnHitListener(Runnable on_hit) { this.on_hit = on_hit; }

    /**
     * @param on_coin_pickup A method to run every time the player picks up a coin
     */
    public void SetOnCoinPickupListener(Runnable on_coin_pickup) { this.on_coin_pickup = on_coin_pickup; }

}
