package com.amr.carvsblocks;

import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends Activity {

    public static boolean paused = true;
    public TextView HelperText;
    public ImageView pauseButton;
    public View view;

    public static boolean GodMode = false;
    public int BlockMaxLifeTime = 4500;
    public int Score = 0;
    public int BlockResapwnTime = 1000;

    public int BlockMaxReswpanTime = 1000;
    public int speed = 50;
    public int NextLevel = 250;
    public Handler handler = new Handler();

    public TextView ScoreScreen;
    public ImageView Car;
    public RelativeLayout Street;
    public int[] Colors = {
            Color.GRAY,
            Color.GREEN,
            Color.BLUE,
            Color.CYAN,
            Color.LTGRAY,
            Color.RED,
            Color.YELLOW,
            Color.WHITE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Car = (ImageView) findViewById(R.id.car);
        ScoreScreen = (TextView) findViewById(R.id.score);
        Street = (RelativeLayout) findViewById(R.id.street);

        pauseButton = (ImageView) findViewById(R.id.pause_button);
        HelperText = (TextView) findViewById(R.id.helper_text);
        Toast.makeText(this, "this game was developed by amr", Toast.LENGTH_LONG).show();
        Street.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!paused) {
                    Car.setX(event.getX()-50);
                }
                return true;
            }
        });

    }

    public void play(View view) {

        this.view = view;
        if(view.getTag().toString().equals("not_playing")) {

            view.setVisibility(View.INVISIBLE);
            HelperText.animate().alpha(0f).setDuration(1000);
            pauseButton.setVisibility(View.VISIBLE);
            ScoreScreen.setVisibility(View.VISIBLE);

            paused = false;
            GodMode = false;
            handler.post(new GamePlay());
            this.view.setTag("playing");

        }
    }

    public void ChangeGameDifficulty(int score) {
        if(score == NextLevel) {
            speed += 2;
            BlockMaxReswpanTime -= (BlockMaxReswpanTime > 300) ? 20: 0;
            NextLevel += 100;
        }

    }

    public void Pause(View view) {
        this.view.setVisibility(View.VISIBLE);
        GodMode = true;
        paused = true;

        HelperText.setText("tap to continue");
        HelperText.setAlpha(1f);
        this.view.setTag("not_playing");

    }

    public void ShowLoseMessage() {
        paused = true;
        HelperText.setText(String.format("Game Over!\nyour score is %s\ntap to play again ", ScoreScreen.getText().toString()));
        HelperText.setAlpha(1f);
        view.setVisibility(View.VISIBLE);

        this.view.setTag("not_playing");
        pauseButton.setVisibility(View.INVISIBLE);
        ScoreScreen.setVisibility(View.INVISIBLE);
        Reset();
    }

    public void Reset() {
        BlockMaxLifeTime = 4500;
        BlockResapwnTime = 1000;
        Score = 0;

        BlockMaxReswpanTime = 1000;
        speed = 50;
        NextLevel = 250;
        GodMode = false;
    }

    public View CreateBlock() {
        TextView Block = new TextView(this);
        Random random = new Random();
        int position = random.nextInt(getResources().getSystem().getDisplayMetrics().widthPixels);
        Block.setBackgroundColor(Colors[random.nextInt(Colors.length)]);

        Block.setWidth(100);
        Block.setHeight(100);
        Street.addView(Block);


        Block.setY(-1000);
        Block.setX(position);
        return Block;
    }

    public boolean isLost(View Block) {

        boolean InYِArea = Block.getY() >= Car.getY()-80 && Block.getY() <= Car.getY()+165;
        boolean InXArea = Block.getX() >= Car.getX()-80 && Block.getX() <= Car.getX()+165;
        return InXArea && InYِArea;
    }

    public void RespawnBlock(final View Block) {

        new CountDownTimer(BlockMaxLifeTime, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                Block.setY(Block.getY() + speed);
                Log.i("info", "block is moving it's at " + Block.getY());

                if(isLost(Block) && !GodMode) {
                    paused = true;
                    ShowLoseMessage();
                }
            }

            @Override
            public void onFinish() {
                Street.removeView(Block);
            }
        }.start();

    }

    class GamePlay implements Runnable {

        @Override
        public void run() {
            if(paused){
                return;
            }

            View Block = CreateBlock();

            if (BlockResapwnTime >= BlockMaxReswpanTime) {
                RespawnBlock(Block);
                BlockResapwnTime = 0;
                Log.i("info", "respawnBlock was called");
            } else {
                BlockResapwnTime += 100;
            }
            ScoreScreen.setText(""+ ++Score);
            ChangeGameDifficulty(Score) ;
            handler.postDelayed(this, 100);
        }

    }
}
