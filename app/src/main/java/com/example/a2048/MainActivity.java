package com.example.a2048;


import android.os.StrictMode;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private TextView textScore;
    private Button buttonReplay;

    public static MainActivity mainActivity;

    public MainActivity(){
        mainActivity = this;
    }

    public void addScore(int newScore) {
        Varible.score += newScore;
        textScore.setText("分数：" + Varible.score);
    }

    public void clearScore(){
        Varible.score = 0;
        textScore.setText("分数：" + 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Varible.et = findViewById(R.id.name);
        textScore = findViewById(R.id.textScore);
        buttonReplay = findViewById(R.id.buttonReplay);

        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameView.gameView.gameOver();
                GameView.gameView.replayGame();
            }
        });
    }
}