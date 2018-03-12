package com.meutkarsh.hiddenword;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    Button b_one_player, b_two_players, b_instructions, b_exit;
    Context THIS = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b_one_player = (Button) findViewById(R.id.button_one_player);
        b_two_players = (Button) findViewById(R.id.button_two_players);
        b_instructions = (Button) findViewById(R.id.button_instructions);
        b_exit = (Button) findViewById(R.id.button_exit);

        b_one_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i =new Intent(MainActivity.this, Game.class);
                i.putExtra("onePlayer", true);

                LayoutInflater li = LayoutInflater.from(THIS);
                View view = li.inflate(R.layout.single_player_game, null);
                AlertDialog.Builder adb = new AlertDialog.Builder(THIS);
                adb.setView(view);

                final EditText compStrength = (EditText) view.findViewById(R.id.comp_strength);

                adb.setTitle("Enter Computer Strength Between 1 to 99");
                adb.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = compStrength.getText().toString();
                        int strength = 50;
                        if(!s.isEmpty()) {
                            int data = Integer.parseInt(s);
                            if(data < 10)   data = 10;
                            strength = data;
                        }
                        i.putExtra("compStrength", strength);
                        startActivity(i);
                    }
                });
                AlertDialog ad = adb.create();
                ad.show();
            }
        });

        b_two_players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Game.class);
                i.putExtra("onePlayer", false);
                startActivity(i);
            }
        });

        b_instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, INSTRUCTIONS.class);
                startActivity(i);
            }
        });

        //b_instructions.setTypeface();

        b_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
