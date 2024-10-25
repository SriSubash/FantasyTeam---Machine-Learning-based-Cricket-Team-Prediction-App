package com.example.fantasyteam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FantasyTeamResponse implements Serializable {

    @SerializedName("player_name")
    private String playerName;

    @SerializedName("role")
    private String role;

    @SerializedName("predicted_performance")
    private double predictedPerformance;

    // Getters and setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getPredictedPerformance() {
        return predictedPerformance;
    }

    public void setPredictedPerformance(double predictedPerformance) {
        this.predictedPerformance = predictedPerformance;
    }
}

