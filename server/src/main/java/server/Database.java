package server;

import tools.SavedGame;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

/**
 * The class for using the saved games database
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class Database {
    public void initDatabase() throws IOException, SQLException {
        try (Connection conn = getConnection()) {
            String[] files = new String[]{"000_create_migration_table.sql", "001_create_saved_games.sql", "002_create_saved_game_players.sql", "003_add_foreign_key_to_saved_games.sql"};
            for (String fileName : files) {
                if (!fileName.equals("000_create_migration_table.sql")) {
                    try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM migrations WHERE filename = '" + fileName + "'")) {
                        if (statement.execute()) {
                            ResultSet rs = statement.getResultSet();
                            if (rs.next())
                                continue;
                        }
                    }
                }
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream inputStream = classLoader.getResourceAsStream("sql/" + fileName);

                // the stream holding the file content
                if (inputStream == null) {
                    throw new IllegalArgumentException("file not found! " + "sql/" + fileName);
                }
                try (PreparedStatement statement = conn.prepareStatement(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8))) {
                    statement.execute();
                    try (PreparedStatement statement2 = conn.prepareStatement("INSERT INTO migrations (filename) VALUES ('" + fileName + "')")) {
                        statement2.execute();
                    }
                }
            }
        }
    }

    public SavedGame[] getSavedGames(UUID playerId) {
//        PreparedStatement ps = conn.prepareStatement("");
        return null;
    }

    public Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:saves.db";
        Properties properties = new Properties();
        properties.setProperty("PRAGMA foreign_keys", "ON");
        return DriverManager.getConnection(url, properties);
    }
}
