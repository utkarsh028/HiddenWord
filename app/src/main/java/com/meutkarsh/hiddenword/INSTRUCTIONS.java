package com.meutkarsh.hiddenword;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class INSTRUCTIONS extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        tv = (TextView) findViewById(R.id.textView);
        String s = "";
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("aaa.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = in.readLine()) != null) {
                s += line + "\n";
            }
            tv.setText(s);
        } catch (IOException e) {
            Toast toast = Toast.makeText(INSTRUCTIONS.this, "Could not load data", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
