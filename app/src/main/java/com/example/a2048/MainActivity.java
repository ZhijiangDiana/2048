package com.example.a2048;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private TextView textScore;
    private Button buttonReplay;

    private int score = 0;

    public static MainActivity mainActivity;

    public MainActivity(){
        mainActivity = this;
    }

    public void addScore(int score) {
        this.score += score;
        textScore.setText("分数：" + this.score);
    }

    public void clearScore(){
        score = 0;
        textScore.setText("分数：" + 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textScore = findViewById(R.id.textScore);
        buttonReplay = findViewById(R.id.buttonReplay);

        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameView.gameView.replayGame();
            }
        });
    }
}