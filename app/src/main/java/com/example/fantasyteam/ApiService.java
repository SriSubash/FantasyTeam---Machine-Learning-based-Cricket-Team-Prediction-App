package com.example.fantasyteam;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/fantasy-team")
    Call<List<FantasyTeamResponse>> getFantasyTeam(@Body TeamInput teamInput);
}
