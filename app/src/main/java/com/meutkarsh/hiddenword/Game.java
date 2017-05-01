package com.meutkarsh.hiddenword;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    int n = 7, m = 7, turn, grid[][], x, y, endScore = 300, plus = 10, minus = 5;
    Button b[][] = new Button[n][m];
    char vowels[] = new char[]{'a', 'e', 'i', 'o', 'u'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvp1 = (TextView) findViewById(R.id.tv_player1);
        tvp2 = (TextView) findViewById(R.id.tv_player2);
        tvs1 = (TextView) findViewById(R.id.tv_score1);
        tvs2 = (TextView) findViewById(R.id.tv_score2);
        tvword = (TextView) findViewById(R.id.tv_word);
        tvturn = (TextView) findViewById(R.id.tv_turn);
        b_restart = (Button) findViewById(R.id.button_restart);
        b_challenge = (Button) findViewById(R.id.button_challenge);
        scoreP1 = scoreP2 = 0;
        turn = 0;
        x = y = 0;
        grid = new int[n][m];
        tvword.setText("");
        tvturn.setText(tvp1.getText());
        b_restart.setBackgroundColor(Color.TRANSPARENT);
        b_challenge.setBackgroundColor(Color.TRANSPARENT);

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
            Toast.makeText(this, "Could not load Dictionary", Toast.LENGTH_LONG).show();
        }

        {       //initializing button array
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

        char u;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                if(i == j || (i+j) == (n-1)){
                    u = vowels[(int) (Math.random() * 5)];
                } else {
                    u = (char) ((Math.random() * 26) + 'a');
                }
                b[i][j].setText("" + u);
                b[i][j].setBackgroundColor(Color.TRANSPARENT);
            }
        }

        b_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char u;
                for(int i = 0; i < n; i++){
                    for(int j = 0; j < m; j++){
                        if(i == j || (i+j) == (n-1)){
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
                x = y = 0;
                Toast.makeText(Game.this, "Game Restarted ...", Toast.LENGTH_SHORT).show();
                scoreP1 = scoreP2 = 0;
                tvs1.setText("0");
                tvs2.setText("0");
                tvword.setText("");
                tvturn.setText(tvp1.getText());
                b_challenge.setText("CHALLENGE");
                b_challenge.setBackgroundColor(Color.TRANSPARENT);
                b_challenge.setEnabled(true);
                turn = 0;
                //b_restart.setBackgroundColor(Color.RED);
            }
        });

        b_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(b_challenge.getText().toString().compareTo("NEW WORD") == 0){
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
                        if (!checkEnd())
                            nextTurn();
                    } else {
                        Toast.makeText(Game.this, "Good job ...", Toast.LENGTH_SHORT).show();
                        if (turn % 2 == 1) {
                            scoreP2 += word.length() * plus;
                            tvs2.setText("" + scoreP2);
                        } else {
                            scoreP1 += word.length() * plus;
                            tvs1.setText("" + scoreP1);
                        }
                        if (!checkEnd())
                            nextTurn();
                    }
                }
            }
        });

        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                final int finalJ = j;
                final int finalI = i;
                b[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int ii = 0; ii < n; ii++) {
                            for (int jj = 0; jj < m; jj++) {
                                if (grid[ii][jj] == 0 && isNeighbour(finalI, finalJ, ii, jj)) {
                                    b[ii][jj].setEnabled(true);
                                    b[ii][jj].setBackgroundColor(Color.TRANSPARENT);
                                } else if(grid[ii][jj] == 0){
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
                        if(root.isWord(word)){
                            if(turn % 2 == 0){
                                scoreP2 += word.length() * plus;
                                tvs2.setText("" + scoreP2);
                            }else{
                                scoreP1 += word.length() * plus;
                                tvs1.setText("" + scoreP1);
                            }
                            Toast.makeText(Game.this, "Correct word :- " + word, Toast.LENGTH_SHORT).show();
                            if(!checkEnd()) {
                                if(checkContinue()) {
                                    tvword.setText(word);
                                    b_challenge.setText("NEW WORD");
                                    b_challenge.setBackgroundColor(Color.rgb(250, 200, 50));
                                } else {
                                    nextTurn();
                                }
                            }
                        }else if(!root.isPrefix(word)){
                            if(turn % 2 == 0){
                                scoreP2 -= word.length() * minus;
                                tvs2.setText("" + scoreP2);
                            }else{
                                scoreP1 -= word.length() * minus;
                                tvs1.setText("" + scoreP1);
                            }
                            Toast.makeText(Game.this, "Invald prefix !", Toast.LENGTH_SHORT).show();
                            if(!checkEnd())
                                nextTurn();
                        } else {
                            tvword.setText(word);
                        }
                        if(turn % 2 == 0){
                            tvturn.setText(tvp1.getText());
                        }else{
                            tvturn.setText(tvp2.getText());
                        }
                    }
                });
            }
        }

    }

    boolean checkEnd(){
        String disha = "";
        if(scoreP2 >= endScore){
            disha = tvp2.getText() + " wins !";
        }else if(scoreP1 >= endScore){
            disha = tvp1.getText() + " wins !";
        }else if(scoreP1 <= -endScore){
            disha = tvp1.getText() + " lost !";
        }else if(scoreP2 <= -endScore){
            disha = tvp2.getText() + " lost !";
        }else if(turn >= 200){
            if(scoreP1 == scoreP2){
                disha = "Its a DRAW.";
            }else if(scoreP1 > scoreP2){
                disha = tvp1.getText() + " wins !";
            }else{
                disha = tvp2.getText() + " wins !";
            }
        }else{
            return false;
        }
        tvword.setText(disha);
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                b[i][j].setEnabled(false);
                b[i][j].setBackgroundColor(Color.LTGRAY);
            }
        }
        b_challenge.setEnabled(false);
        return true;
    }

    void nextTurn(){
        char u;
        tvword.setText("");
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                if(i == j || (i+j) == (n-1)){
                    u = vowels[(int) (Math.random() * 5)];
                } else {
                    u = (char) ((Math.random() * 26) + 'a');
                }
                x = y = 0;
                b[i][j].setText("" + u);
                b[i][j].setBackgroundColor(Color.TRANSPARENT);
                b[i][j].setEnabled(true);
                grid[i][j] = 0;
            }
        }
    }

    boolean canContinue(){
        String word = (String) tvword.getText();
        char can[] = new char[8];
        int k, z = 0;
        char ch;
        if(word.length() == 0){
            return true;
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                if(grid[i][j] == 0 && isNeighbour(x, y, i, j) && root.isPrefix(word + b[i][j].getText())){
                    ch = b[i][j].getText().charAt(0);
                    for(k = 0; k < z; k++){
                        if(can[k] == ch){
                            break;
                        }
                    }
                    if(k == z){
                        can[z++] = ch;
                    }
                }
            }
        }
        if(z == 0) {
            return false;
        }else{
            String utk = "" + can[0];
            for(k = 1; k < z; k++){
                utk += " , " + can[k];
            }
            Toast.makeText(Game.this, "You can use :- " + utk, Toast.LENGTH_LONG).show();
            return true;
        }
    }

    boolean checkContinue(){
        String word = (String) tvword.getText();
        if(word.length() == 0){
            return true;
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                if(grid[i][j] == 0 && isNeighbour(x, y, i, j) && root.isPrefix(word + b[i][j].getText())){
                    return true;
                }
            }
        }
        return false;
    }

    boolean isNeighbour(int i, int j, int x, int y){
        if(i == (x - 1) || i == (x + 1)){
            return j == (y - 1) || j == (y + 1) || j == y;
        }else if(i == x){
            return j == (y - 1) || j == (y + 1);
        }
        return false;
    }

    boolean inRange(int x, int y){
        return (x >= 0 && x < n && y >= 0 && y < m);
    }

}
