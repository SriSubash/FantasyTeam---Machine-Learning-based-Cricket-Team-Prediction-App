package com.example.fantasyteam;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputActivity extends AppCompatActivity {

    private EditText etYear, etTeam1, etTeam2;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        etYear = findViewById(R.id.year);
        etTeam1 = findViewById(R.id.yourTeam);
        etTeam2 = findViewById(R.id.oppTeam);
        btnSubmit = findViewById(R.id.button);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFantasyTeamRequest();
            }
        });
    }

    private void sendFantasyTeamRequest() {
        String year = etYear.getText().toString();
        String team1 = etTeam1.getText().toString();
        String team2 = etTeam2.getText().toString();
        int batsmen = 4;
        int bowlers = 3;
        int allRounders = 3;
        int wicketkeepers = 1;

        TeamInput teamInput = new TeamInput(year, team1, team2, batsmen, bowlers, allRounders, wicketkeepers);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<FantasyTeamResponse>> call = apiService.getFantasyTeam(teamInput);
        ArrayList<String> playerList = new ArrayList<>();

        call.enqueue(new Callback<List<FantasyTeamResponse>>() {
            @Override
            public void onResponse(Call<List<FantasyTeamResponse>> call, Response<List<FantasyTeamResponse>> response) {
                if (response.isSuccessful()) {
                    List<FantasyTeamResponse> fantasyTeam = response.body();
                    if (fantasyTeam != null) {
                        for (FantasyTeamResponse player : fantasyTeam) {
                            Log.d("FantasyTeam", "Player: " + player.getPlayerName() + ", Role: " + player.getRole() + ", Predicted Performance: " + player.getPredictedPerformance());
                            String p = player.getPlayerName() + " - " + player.getRole();
                            playerList.add(p);
                        }
                        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                        intent.putStringArrayListExtra("FantasyTeam", playerList);
                        startActivity(intent);
                        Toast.makeText(InputActivity.this, "Fantasy Team fetched successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InputActivity.this, "Error: Unable to fetch team", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FantasyTeamResponse>> call, Throwable t) {
                Log.e("Error", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(InputActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


