package com.example.fantasyteam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomPlayerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> players;

    public CustomPlayerAdapter(Context context, List<String> players) {
        super(context, R.layout.list_item_player, players);
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Inflate the custom layout for each list item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_player, parent, false);

        // Get the text views in the custom layout
        TextView playerNameView = rowView.findViewById(R.id.player_name);
        TextView playerRoleView = rowView.findViewById(R.id.player_role);

        // Assuming each string in the list is formatted as "PlayerName - Role"
        String[] playerInfo = players.get(position).split(" - ");
        String playerName = playerInfo[0];
        String playerRole = playerInfo[1].toUpperCase();

        // Set the text for player name and role
        playerNameView.setText(playerName);
        playerRoleView.setText(playerRole);

        return rowView;
    }
}

