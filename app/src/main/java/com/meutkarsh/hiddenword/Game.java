package com.meutkarsh.hiddenword;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Game extends AppCompatActivity {

    TextView  tvp1, tvp2, tvs1, tvs2, tvword, tvturn;
    Button b_restart, b_challenge;
    TrieNode root;
    int scoreP1, scoreP2, MIN_WORD_LENGTH = 3;
    int turn, x, y, n = 7, m = 7, grid[][];
    int endScore = 300, plus = 10, minus = 5;
    Button b[][] = new Button[n][m];
    char vowels[] = new char[]{'a', 'e', 'i', 'o', 'u'};
    public MediaPlayer mp;
    Context THIS = this;
    int nextX[] = new int[]{-1, -1, -1, 0, 0, 1, 1, 1};
    int nextY[] = new int[]{-1, 0, 1, -1, 1, -1, 0, 1};
    int nextLEN  = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mp = MediaPlayer.create(this, R.raw.abc);
        mp.start();

        tvp1 = (TextView) findViewById(R.id.tv_player1);
        tvp2 = (TextView) findViewById(R.id.tv_player2);
        tvs1 = (TextView) findViewById(R.id.tv_score1);
        tvs2 = (TextView) findViewById(R.id.tv_score2);
        tvword = (TextView) findViewById(R.id.tv_word);
        tvturn = (TextView) findViewById(R.id.tv_turn);
        b_restart = (Button) findViewById(R.id.button_restart);
        b_challenge = (Button) findViewById(R.id.button_challenge);

        // Dictionary Loading using Async Task
        new LoadDictionary().execute();

        LayoutInflater li = LayoutInflater.from(this);
        View input_data_view = li.inflate(R.layout.start_game, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(input_data_view);

        final EditText p1_name = (EditText) input_data_view.findViewById(R.id.p1_name);
        final EditText p2_name = (EditText) input_data_view.findViewById(R.id.p2_name);
        final EditText score = (EditText) input_data_view.findViewById(R.id.max_score);

        adb.setPositiveButton("Start",null);
        AlertDialog alert = adb.create();

        adb.setCancelable(false);
        adb.setPositiveButton("Start",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String p1 = p1_name.getText().toString();
                if (!p1.isEmpty()) tvp1.setText(p1);
                String p2 = p2_name.getText().toString();
                if (!p2.isEmpty()) tvp2.setText(p2);
                String mm = score.getText().toString().trim();
                if (mm.length() > 0) endScore = Integer.parseInt(mm);
                tvturn.setText(tvp1.getText());

                String message = "It is not important to complete the word, " +
                        "just find the correct letter which will lead you to victory.";
                AlertDialog.Builder tip = new AlertDialog.Builder (
                        new ContextThemeWrapper (Game.this, R.style.AlertDialogCustom));
                tip.setTitle("Important Tip:");
                tip.setMessage(message);
                tip.setCancelable(false);

                tip.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Empty
                    }
                });
                AlertDialog al=tip.create();
                al.setCanceledOnTouchOutside(false);
                al.show();
            }
        });
        AlertDialog ale =adb.create();
        ale.setCanceledOnTouchOutside(false);
        ale.show();

        scoreP1 = scoreP2 = 0;
        turn = 0;
        grid = new int[n][m];
        b_restart.setBackgroundColor(Color.TRANSPARENT);
        b_challenge.setBackgroundColor(Color.TRANSPARENT);

        //initializing button array
        initializeButtons();
        nextTurn();

        b_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(THIS);
                View dialog_view = li.inflate(R.layout.restart, null);
                AlertDialog.Builder ad = new AlertDialog.Builder(THIS);
                ad.setView(dialog_view);
                ad.setCancelable(false);
                ad.setMessage("Are you sure you want to restart ?");
                final EditText score = (EditText) dialog_view.findViewById(R.id.max_score);
                ad.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        String mm = score.getText().toString().trim();
                        if(mm.length() > 0) endScore = Integer.parseInt(mm);
                        nextTurn();
                        Toast.makeText(Game.this, "Game Restarted ...", Toast.LENGTH_SHORT).show();
                        scoreP1 = scoreP2 = 0;
                        tvs1.setText("0");
                        tvs2.setText("0");
                        tvturn.setText(tvp1.getText());
                        b_challenge.setText("CHALLENGE");
                        b_challenge.setBackgroundColor(Color.TRANSPARENT);
                        b_challenge.setEnabled(true);
                        turn = 0;
                    }
                });
                ad.setNegativeButton("Cancel", null);
                ad.show();
            }
        });

        b_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(b_challenge.getText().toString().charAt(0) == 'N') {
                    b_challenge.setText("CHALLENGE");
                    b_challenge.setBackgroundColor(Color.TRANSPARENT);
                    nextTurn();
                } else {
                    String word = (String) tvword.getText();
                    if (word.length() == 0) {
                        return;
                    }
                    if (canContinue()) {
                        if (turn % 2 == 1) {
                            scoreP2 -= word.length() * minus;
                            tvs2.setText("" + scoreP2);
                        } else {
                            scoreP1 -= word.length() * minus;
                            tvs1.setText("" + scoreP1);
                        }
                        if (!checkEnd()) {
                            b_challenge.setText("NEXT");
                            b_challenge.setBackgroundColor(Color.rgb(250, 200, 50));
                        }
                    } else {
                        Toast.makeText(Game.this, "Good job ...", Toast.LENGTH_SHORT).show();
                        if (turn % 2 == 1) {
                            scoreP2 += word.length() * plus;
                            tvs2.setText("" + scoreP2);
                        } else {
                            scoreP1 += word.length() * plus;
                            tvs1.setText("" + scoreP1);
                        }
                        if (!checkEnd()) nextTurn();
                    }
                }
            }
        });

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                final int finalI = i;
                final int finalJ = j;
                b[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int ii = 0; ii < n; ii++) {
                            for (int jj = 0; jj < m; jj++) {
                                if (grid[ii][jj] == 0 && isNeighbour(finalI, finalJ, ii, jj)) {
                                    b[ii][jj].setEnabled(true);
                                    b[ii][jj].setBackgroundColor(Color.TRANSPARENT);
                                } else if(grid[ii][jj] == 0) {
                                    b[ii][jj].setEnabled(false);
                                    b[ii][jj].setBackgroundColor(Color.LTGRAY);
                                } else {
                                    b[ii][jj].setEnabled(false);
                                }
                            }
                        }
                        b_challenge.setText("CHALLENGE");
                        b_challenge.setBackgroundColor(Color.TRANSPARENT);
                        x = finalI;
                        y = finalJ;
                        b[finalI][finalJ].setBackgroundColor(Color.rgb(240, 160, 50));
                        b[finalI][finalJ].setEnabled(false);
                        grid[finalI][finalJ] = 1;
                        turn++;
                        String word = "" + tvword.getText() + b[finalI][finalJ].getText();
                        Log.d("Utkarsh", "" + word.length());
                        if(root.isWord(word)) {
                            if(turn % 2 == 0) {
                                scoreP2 += word.length() * plus;
                                tvs2.setText("" + scoreP2);
                            } else {
                                scoreP1 += word.length() * plus;
                                tvs1.setText("" + scoreP1);
                            }
                            // Add Meaning
                            Toast.makeText(Game.this, "Correct word :- " + word, Toast.LENGTH_SHORT).show();
                            if(!checkEnd()) {
                                if(checkContinue()) {
                                    tvword.setText(word);
                                    b_challenge.setText("NEXT");
                                    b_challenge.setBackgroundColor(Color.rgb(250, 200, 50));
                                } else {
                                    nextTurn();
                                }
                            }
                        } else if( !root.isPrefix(word) ) {
                            if(turn % 2 == 0) {
                                scoreP2 -= word.length() * minus;
                                tvs2.setText("" + scoreP2);
                            } else {
                                scoreP1 -= word.length() * minus;
                                tvs1.setText("" + scoreP1);
                            }
                            Toast.makeText(Game.this, "Invald prefix !", Toast.LENGTH_SHORT).show();
                            if(!checkEnd()) nextTurn();
                        } else {
                            tvword.setText(word);
                        }
                        if(turn % 2 == 0) {
                            tvturn.setText(tvp1.getText());
                        }else {
                            tvturn.setText(tvp2.getText());
                        }
                    }
                });
            }
        }

    }

    void initializeButtons() {
        b[0][0] = (Button) findViewById(R.id.a1);
        b[0][1] = (Button) findViewById(R.id.a2);
        b[0][2] = (Button) findViewById(R.id.a3);
        b[0][3] = (Button) findViewById(R.id.a4);
        b[0][4] = (Button) findViewById(R.id.a5);
        b[0][5] = (Button) findViewById(R.id.a6);
        b[0][6] = (Button) findViewById(R.id.a7);

        b[1][0] = (Button) findViewById(R.id.b1);
        b[1][1] = (Button) findViewById(R.id.b2);
        b[1][2] = (Button) findViewById(R.id.b3);
        b[1][3] = (Button) findViewById(R.id.b4);
        b[1][4] = (Button) findViewById(R.id.b5);
        b[1][5] = (Button) findViewById(R.id.b6);
        b[1][6] = (Button) findViewById(R.id.b7);

        b[2][0] = (Button) findViewById(R.id.c1);
        b[2][1] = (Button) findViewById(R.id.c2);
        b[2][2] = (Button) findViewById(R.id.c3);
        b[2][3] = (Button) findViewById(R.id.c4);
        b[2][4] = (Button) findViewById(R.id.c5);
        b[2][5] = (Button) findViewById(R.id.c6);
        b[2][6] = (Button) findViewById(R.id.c7);

        b[3][0] = (Button) findViewById(R.id.d1);
        b[3][1] = (Button) findViewById(R.id.d2);
        b[3][2] = (Button) findViewById(R.id.d3);
        b[3][3] = (Button) findViewById(R.id.d4);
        b[3][4] = (Button) findViewById(R.id.d5);
        b[3][5] = (Button) findViewById(R.id.d6);
        b[3][6] = (Button) findViewById(R.id.d7);

        b[4][0] = (Button) findViewById(R.id.e1);
        b[4][1] = (Button) findViewById(R.id.e2);
        b[4][2] = (Button) findViewById(R.id.e3);
        b[4][3] = (Button) findViewById(R.id.e4);
        b[4][4] = (Button) findViewById(R.id.e5);
        b[4][5] = (Button) findViewById(R.id.e6);
        b[4][6] = (Button) findViewById(R.id.e7);

        b[5][0] = (Button) findViewById(R.id.f1);
        b[5][1] = (Button) findViewById(R.id.f2);
        b[5][2] = (Button) findViewById(R.id.f3);
        b[5][3] = (Button) findViewById(R.id.f4);
        b[5][4] = (Button) findViewById(R.id.f5);
        b[5][5] = (Button) findViewById(R.id.f6);
        b[5][6] = (Button) findViewById(R.id.f7);

        b[6][0] = (Button) findViewById(R.id.g1);
        b[6][1] = (Button) findViewById(R.id.g2);
        b[6][2] = (Button) findViewById(R.id.g3);
        b[6][3] = (Button) findViewById(R.id.g4);
        b[6][4] = (Button) findViewById(R.id.g5);
        b[6][5] = (Button) findViewById(R.id.g6);
        b[6][6] = (Button) findViewById(R.id.g7);
    }

    boolean checkEnd() {
        String dp = "";
        if(scoreP2 >= endScore) {
            dp = tvp2.getText() + " wins !";
        } else if(scoreP1 >= endScore) {
            dp = tvp1.getText() + " wins !";
        } else if(scoreP1 <= -endScore) {
            dp = tvp1.getText() + " lost !";
        } else if(scoreP2 <= -endScore) {
            dp = tvp2.getText() + " lost !";
        } else if(turn >= 200) {
            if(scoreP1 == scoreP2) {
                dp = "It's a DRAW.";
            } else if(scoreP1 > scoreP2) {
                dp = tvp1.getText() + " wins !";
            } else {
                dp = tvp2.getText() + " wins !";
            }
        } else {
            return false;
        }
        tvword.setText(dp);
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                b[i][j].setEnabled(false);
                b[i][j].setBackgroundColor(Color.LTGRAY);
            }
        }
        b_challenge.setEnabled(false);
        return true;
    }

    void nextTurn() {
        char u;
        x = y = 0;
        tvword.setText("");
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                if(i == j || (i+j) == (n-1)) {
                    u = vowels[(int) (Math.random() * 5)];
                } else {
                    u = (char) ((Math.random() * 26) + 'a');
                }
                b[i][j].setText("" + u);
                b[i][j].setBackgroundColor(Color.TRANSPARENT);
                b[i][j].setEnabled(true);
                grid[i][j] = 0;
            }
        }
    }

    boolean canContinue() {
        String word = (String) tvword.getText();
        if(word.length() == 0)  return true;
        boolean can = false;
        b[x][y].setEnabled(false);
        for(int i = 0; i < nextLEN; i++) {
            int u = x + nextX[i];
            int v = y + nextY[i];
            if(inRange(u, v)) {
                b[u][v].setEnabled(false);
                if(grid[u][v] == 0 && root.isPrefix(word + b[u][v].getText())) {
                    can = true;
                    b[u][v].setBackgroundColor(Color.rgb(100, 200, 10));
                }
            }
        }
        return can;
    }

    boolean checkContinue() {
        String word = (String) tvword.getText();
        if(word.length() == 0)  return true;
        for(int i = 0; i < nextLEN; i++) {
            int u = x + nextX[i];
            int v = y + nextY[i];
            if(inRange(u, v) && grid[u][v] == 0 &&
                    root.isPrefix(word + b[u][v].getText())) return true;
        }
        return false;
    }

    boolean isNeighbour(int i, int j, int x, int y) {
        if(i == (x - 1) || i == (x + 1)) {
            return j == (y - 1) || j == (y + 1) || j == y;
        } else if(i == x) {
            return j == (y - 1) || j == (y + 1);
        }
        return false;
    }

    boolean inRange(int x, int y){
        return (x >= 0 && x < n && y >= 0 && y < m);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.audio){
            if(mp.isPlaying()){
                mp.pause();
                item.setIcon(R.drawable.ic_volume_off_white_24dp);
            }else{
                mp.start();
                item.setIcon(R.drawable.ic_volume_up_white_24dp);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.pause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mp.start();
        // will start even if icon is off -> bug
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();
    }

    private class LoadDictionary extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            AssetManager assetManager = getAssets();
            try{
                InputStream inputStream = assetManager.open("words.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                root = new TrieNode();
                String line = null;
                while((line = in.readLine()) != null) {
                    String word = line.trim();
                    if (word.length() >= MIN_WORD_LENGTH)
                        root.add(word);
                }
            }catch (IOException e){
                Toast.makeText(THIS, "Could not load Dictionary", Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }

}
