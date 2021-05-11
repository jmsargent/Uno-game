ALTER TABLE saved_games
    ADD COLUMN current_player string REFERENCES saved_game_players (player_id) ON DELETE CASCADE;