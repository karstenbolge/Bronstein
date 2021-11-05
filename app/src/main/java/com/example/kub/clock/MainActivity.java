package com.example.kub.clock;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private EditText matchLength;
    private EditText secondsPerPoint;
    private EditText secondsPerMove;

    private  TextView textView1;
    private  TextView textView2;
    private  TextView textView3;

    private Button button;
    private Button[] playerButton = new Button[2];

    private int[] playerMin = new int[2];
    private int[] playerSec = new int[2];
    private int[] playerBufferSec = new int[2];

    private int ml;
    private int spp;
    private int spm;

    static final String PLAYER_1_MIN = "player1min";
    static final String PLAYER_2_MIN = "player2min";
    static final String PLAYER_1_SEC = "player1sec";
    static final String PLAYER_2_SEC = "player2sec";
    static final String IN_GAME = "InGame";

    private Timer timer = null;

    private boolean inGame = false;
    private int onMove = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            playerMin[0] = savedInstanceState.getInt(PLAYER_1_MIN);
            playerMin[1] = savedInstanceState.getInt(PLAYER_2_MIN);
            playerSec[0] = savedInstanceState.getInt(PLAYER_1_SEC);
            playerSec[0] = savedInstanceState.getInt(PLAYER_2_SEC);

            inGame = savedInstanceState.getBoolean(IN_GAME);
            if (inGame) {
                startButtonOnClick(null);
                return;
            }
        }

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        matchLength = (EditText) findViewById(R.id.matchLength);
        matchLength.setText("17");

        secondsPerPoint = (EditText) findViewById(R.id.secondsPerPoint);
        secondsPerPoint.setText("120");

        secondsPerMove = (EditText) findViewById(R.id.secondsPerMove);
        secondsPerMove.setText("12");

        textView1 = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);

        button = (Button) findViewById(R.id.button);
        playerButton[0] = (Button) findViewById(R.id.player1);
        playerButton[1] = (Button) findViewById(R.id.player2);

        playerButton[0].setVisibility(View.INVISIBLE);
        playerButton[1].setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        playerMin[0] = savedInstanceState.getInt(PLAYER_1_MIN);
        playerMin[1] = savedInstanceState.getInt(PLAYER_2_MIN);
        playerSec[0] = savedInstanceState.getInt(PLAYER_1_SEC);
        playerSec[0] = savedInstanceState.getInt(PLAYER_2_SEC);

        inGame = savedInstanceState.getBoolean(IN_GAME);

        if (inGame) startButtonOnClick(null);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state

        super.onSaveInstanceState(savedInstanceState);
        // Save the user's current game state
        savedInstanceState.putInt(PLAYER_1_MIN, playerMin[0]);
        savedInstanceState.putInt(PLAYER_2_MIN, playerMin[1]);
        savedInstanceState.putInt(PLAYER_1_SEC, playerSec[0]);
        savedInstanceState.putInt(PLAYER_2_SEC, playerSec[1]);
        savedInstanceState.putBoolean(IN_GAME, inGame);
    }

    public void startButtonOnClick(View v) {
        button.setText("Paused");
        button.setBackgroundColor(Color.GREEN);
        playerButton[0].setBackgroundColor(Color.LTGRAY);
        playerButton[0].setTextColor(Color.BLACK);
        playerButton[1].setBackgroundColor(Color.LTGRAY);
        playerButton[1].setTextColor(Color.BLACK);

        if (!inGame) {
            matchLength.setVisibility(View.INVISIBLE);
            secondsPerPoint.setVisibility(View.INVISIBLE);
            secondsPerMove.setVisibility(View.INVISIBLE);

            textView1.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            textView3.setVisibility(View.INVISIBLE);

            playerButton[0].setVisibility(View.VISIBLE);
            playerButton[1].setVisibility(View.VISIBLE);

            ml = Integer.parseInt(matchLength.getText().toString());
            spp = Integer.parseInt(secondsPerPoint.getText().toString());
            spm = Integer.parseInt(secondsPerMove.getText().toString());

            playerMin[0] = playerMin[1] = (ml * spp) / 60;
            playerSec[0] = playerSec[1] = (ml * spp) - playerMin[0] * 60;

            playerBufferSec[0] = playerBufferSec[1] = spm;

            setPlayerText(0);
            setPlayerText(1);

            inGame = true;
        }
        else {
            onMove = -1;
            if (timer != null)
            {
                timer.cancel();
                timer = null;
            }
            playerBufferSec[0] = playerBufferSec[1] = spm;
            setPlayerText(0);
            setPlayerText(1);
        }
    }

    private void setPlayerText(int player) {
        if (player == 0 || player == 1)
        {
            playerButton[player].setText(String.format("%02d:%02d%s00:%02d", playerMin[player], playerSec[player], System.getProperty("line.separator"), playerBufferSec[player]));
        }
    }

    public void TimeIsUp(){
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }

        button.setText("Finished");
        button.setBackgroundColor(Color.RED);
        button.setTextColor(Color.WHITE);

        onMove = -1;

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
    }

    public void OnTimer() {
        if (onMove == 0 || onMove == 1) {
            if (playerBufferSec[onMove] > 0) {
                playerBufferSec[onMove]--;
            }
            else {
                playerButton[onMove].setBackgroundColor(Color.RED);
                playerButton[onMove].setTextColor(Color.WHITE);
                if (playerSec[onMove] > 0) {
                    playerSec[onMove]--;
                }
                else {
                    if (playerMin[onMove] > 0)
                    {
                        playerMin[onMove]--;
                        playerSec[onMove] = 59;
                    }
                    else {
                        TimeIsUp();
                    }
                }
            }
            setPlayerText(onMove);
        }
    }

    public void startTimer() {
        button.setText("Pause");
        button.setBackgroundColor(Color.LTGRAY);

        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    public void run() {
                        OnTimer();
                    }
                });

            }
        }, 1000, 1000);
    }

    public void player1ButtonOnClick(View v) {
        if (onMove != 1) {
            if (onMove == -1) onMove = 0;

            changeOnMove();
        }
    }

    private void changeOnMove() {
        if (onMove == 0 || onMove == 1) {
            playerBufferSec[onMove] = spm;
            setPlayerText(onMove);
            playerButton[onMove].setBackgroundColor(Color.LTGRAY);
            playerButton[onMove].setTextColor(Color.BLACK);

            //change on move
            onMove = 1 - onMove;

            playerButton[onMove].setBackgroundColor(Color.GREEN);
            playerButton[onMove].setTextColor(Color.BLACK);

            startTimer();
        }
    }

    public void player2ButtonOnClick(View v) {
        if (onMove != 0) {
            if (onMove == -1) onMove = 1;

            changeOnMove();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
