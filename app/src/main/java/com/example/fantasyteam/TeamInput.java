package com.example.fantasyteam;

import com.google.gson.annotations.SerializedName;

public class TeamInput {
    @SerializedName("year")
    private String year;
    @SerializedName("team1")
    private String team1;
    @SerializedName("team2")
    private String team2;
    @SerializedName("num_batsmen")
    private int numBatsmen;
    @SerializedName("num_bowlers")
    private int numBowlers;
    @SerializedName("num_all_rounders")
    private int numAllRounders;
    @SerializedName("num_wicketkeepers")
    private int numWicketkeepers;

    public TeamInput(String year, String team1, String team2, int numBatsmen, int numBowlers, int numAllRounders, int numWicketkeepers) {
        this.year = year;
        this.team1 = team1;
        this.team2 = team2;
        this.numBatsmen = numBatsmen;
        this.numBowlers = numBowlers;
        this.numAllRounders = numAllRounders;
        this.numWicketkeepers = numWicketkeepers;
    }
}

