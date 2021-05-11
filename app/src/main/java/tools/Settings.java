package tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.ToJSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author Martin Claesson
 * @version 2021-03-07
 */
public class Settings implements ToJSON {
    @JsonIgnore
    Path currentRelativePath = Paths.get("");
    private final String defaultConfigDir = currentRelativePath.toAbsolutePath().toString()+"\\";
    @JsonIgnore
    private ObjectMapper mapper = new ObjectMapper();
    private String serverHost;
    private int serverPort;
    private UUID uuid;
    private String playerName;

    public Settings() throws IOException {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("default_config.json");
            if (inputStream != null) {
                load(inputStream);
            } else {
                throw new FileNotFoundException("Unable to locate the default config inside the jar file");
            }
            this.uuid = UUID.randomUUID();
            this.playerName = "Player";
                save();
        }
    }

    @JsonCreator
    public Settings(@JsonProperty("serverHost") String serverHost, @JsonProperty("serverPort") int serverPort, @JsonProperty("uuid") String uuid, @JsonProperty("playerName") String playerName) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        if (uuid == "") {
            this.uuid = UUID.randomUUID();
        } else {
            this.uuid = UUID.fromString(uuid);
        }
        this.playerName = playerName;
    }

    public void save() throws IOException {
        File confDir = new File(defaultConfigDir);
        if (!confDir.exists() && !confDir.mkdir()) {
            throw new IOException("Unable to create the config directory");
        }
        File file = new File(defaultConfigDir + "config.json");
        mapper.writeValue(file, this);
        System.out.println("Wrote config to " + defaultConfigDir + "config.json");
    }

    public void load() throws IOException {
        System.out.println("loading " + defaultConfigDir + "config.json");
        load(defaultConfigDir + "config.json");
    }

    public void load(InputStream is) throws IOException {
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        copy(mapper.readValue(json, Settings.class));
    }

    public void load(String path) throws IOException {
        File file = new File(path);
        if (file.exists() && file.canRead()) {
            copy(mapper.readValue(file, Settings.class));
        } else {
            throw new IOException("Config file does not exist");
        }
    }

    private void copy(Settings s) {
        serverHost = s.serverHost;
        serverPort = s.serverPort;
        uuid = s.uuid;
        playerName = s.playerName;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
