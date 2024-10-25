package com.example.fantasyteam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL */
public class ResultActivity extends AppCompatActivity {

    private ListView result;
    private TextView text;

    @Override
    protected void onCreate(Bundle instance) {
        super.onCreate(instance);
        setContentView(R.layout.activity_result);

        result = findViewById(R.id.listViewPlayers);
        text = findViewById(R.id.textViewResult);

        // Get the intent data
        Intent i = getIntent();
        ArrayList<String> playerList = getIntent().getStringArrayListExtra("FantasyTeam");

        // If the player list is not empty, populate the ListView with a custom adapter
        if (playerList != null && !playerList.isEmpty()) {
            // Create and set custom adapter
            CustomPlayerAdapter adapter = new CustomPlayerAdapter(this, playerList);
            result.setAdapter(adapter);
        } else {
            text.setText("No Players Available");
        }
    }
}
