CREATE TABLE IF NOT EXISTS saved_game_players
(
    player_id   string,
    player_hand json NOT NULL,
    game_id     string,
    FOREIGN KEY (game_id) REFERENCES saved_games (id) ON DELETE CASCADE,
    PRIMARY KEY (player_id, game_id)
);