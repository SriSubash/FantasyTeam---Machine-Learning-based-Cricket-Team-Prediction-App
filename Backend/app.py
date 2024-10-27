from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense

app = Flask(__name__)

ball_by_ball_data = pd.read_csv('IPL Ball-by-Ball 2008-2020.csv')
match_data = pd.read_csv('IPL Matches 2008-2020.csv')

@app.route('/fantasy-team', methods=['POST'])
def fantasy_team():
    input_data = request.json
    year = str(input_data.get('year'))
    team1 = input_data.get('team1')
    team2 = input_data.get('team2')
    num_batsmen = input_data.get('num_batsmen', 4)
    num_bowlers = input_data.get('num_bowlers', 3)
    num_all_rounders = input_data.get('num_all_rounders', 3)
    num_wicketkeepers = input_data.get('num_wicketkeepers', 1)

    match_data['date'] = pd.to_datetime(match_data['date'])
    match_data['year'] = match_data['date'].dt.year
    filtered_matches = match_data[(match_data['year'] == int(year)) & 
                                  ((match_data['team1'] == team1) | (match_data['team2'] == team1) |
                                   (match_data['team1'] == team2) | (match_data['team2'] == team2))]

    if filtered_matches.empty:
        return jsonify({"message": "No matches found for the selected teams and year."})

    merged_data = pd.merge(ball_by_ball_data, filtered_matches, on='id')

    merged_data['is_wicket'] = merged_data['is_wicket'].fillna(0)
    merged_data['total_runs'] = merged_data['batsman_runs'] + merged_data['extra_runs']

    batsman_stats = merged_data.groupby(['id', 'batsman','year']).agg({
        'total_runs': 'sum',
        'is_wicket': 'sum'
    }).reset_index()

    bowler_stats = merged_data[merged_data['is_wicket'] == 1].groupby(['id', 'bowler', 'year']).agg({
        'is_wicket': 'count'
    }).reset_index()

    all_rounder_stats = merged_data.groupby(['id', 'batsman', 'year']).agg({
        'total_runs': 'sum',
        'is_wicket': 'sum'
    }).reset_index()

    batsman_stats['role'] = 'batsman'
    batsman_stats['performance_score'] = batsman_stats['total_runs']
    
    bowler_stats['role'] = 'bowler'
    bowler_stats['performance_score'] = bowler_stats['is_wicket']

    wicketkeeper_stats = merged_data[merged_data['dismissal_kind'].isin(['caught', 'stumped'])].groupby(
        ['id', 'fielder','year']).agg({'dismissal_kind': 'count'}).reset_index()
    wicketkeeper_stats['role'] = 'wicketkeeper'
    wicketkeeper_stats['performance_score'] = wicketkeeper_stats['dismissal_kind']

    all_rounder_stats['role'] = 'all-rounder'
    all_rounder_stats['performance_score'] = all_rounder_stats['total_runs'] + all_rounder_stats['is_wicket']

    batsman_stats.rename(columns={'batsman': 'player_name'}, inplace=True)
    bowler_stats.rename(columns={'bowler': 'player_name'}, inplace=True)
    wicketkeeper_stats.rename(columns={'fielder': 'player_name'}, inplace=True)
    all_rounder_stats.rename(columns={'batsman': 'player_name'}, inplace=True)

    batsman_stats = batsman_stats[['player_name', 'performance_score', 'role','year']]
    bowler_stats = bowler_stats[['player_name', 'performance_score', 'role','year']]
    wicketkeeper_stats = wicketkeeper_stats[['player_name', 'performance_score', 'role','year']]
    all_rounder_stats = all_rounder_stats[['player_name', 'performance_score', 'role','year']]

    player_stats = pd.concat([batsman_stats, bowler_stats, wicketkeeper_stats, all_rounder_stats])

    X = player_stats[['performance_score']]
    y = player_stats['performance_score']

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    scaler = StandardScaler()
    X_train = scaler.fit_transform(X_train)
    X_test = scaler.transform(X_test)

    model = Sequential()
    model.add(Dense(128, input_dim=X_train.shape[1], activation='relu'))
    model.add(Dense(64, activation='relu'))
    model.add(Dense(32, activation='relu'))
    model.add(Dense(1)) 

    model.compile(optimizer='adam', loss='mean_squared_error')

    model.fit(X_train, y_train, epochs=50, batch_size=32, validation_split=0.1)

    loss = model.evaluate(X_test, y_test)
    print(f"Test Loss: {loss}")

    num_batsmen = 4
    num_bowlers = 3
    num_all_rounders = 3
    num_wicketkeepers = 1

    player_stats['predicted_performance'] = model.predict(scaler.transform(player_stats[['performance_score']]))

    player_stats_sorted = player_stats.sort_values(by='predicted_performance', ascending=False)

    selected_players = []
    player_ids_selected = set()

    for _, player in player_stats_sorted.iterrows():
        player_name = player['player_name']
        if player_name in player_ids_selected:
            continue  

        if player['year'] == int(year):
            
            if player['role'] == 'batsman' and len([p for p in selected_players if p['role'] == 'batsman']) < num_batsmen:
                selected_players.append(player)
                player_ids_selected.add(player_name)
    
            elif player['role'] == 'bowler' and len([p for p in selected_players if p['role'] == 'bowler']) < num_bowlers:
                selected_players.append(player)
                player_ids_selected.add(player_name)
    
            elif player['role'] == 'all-rounder' and len([p for p in selected_players if p['role'] == 'all-rounder']) < num_all_rounders:
                selected_players.append(player)
                player_ids_selected.add(player_name)
    
            elif player['role'] == 'wicketkeeper' and len([p for p in selected_players if p['role'] == 'wicketkeeper']) < num_wicketkeepers:
                selected_players.append(player)
                player_ids_selected.add(player_name)
    
            if len(selected_players) == 11:
                break

    fantasy_team = pd.DataFrame(selected_players)

    if len(fantasy_team) != 11:
        return jsonify({"message": "Warning: The selected fantasy team does not have 11 unique players."})
    else:
        return jsonify(fantasy_team[['player_name', 'role', 'predicted_performance']].to_dict(orient='records'))


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
