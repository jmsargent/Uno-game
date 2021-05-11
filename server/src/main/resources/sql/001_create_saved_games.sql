CREATE TABLE IF NOT EXISTS saved_games
(
    id        string PRIMARY KEY,
    draw_pile json NOT NULL,
    play_pile json NOT NULL
);