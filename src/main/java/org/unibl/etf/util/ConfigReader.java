package org.unibl.etf.util;

import org.unibl.etf.exceptions.ConfigException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigReader {
    private static final int MIN_PLAYER_NUMBER = 2;
    private static final int MIN_GRID_SIZE = 7;
    private static final int MAX_GRID_SIZE = 10;
    public static final int MAX_PLAYERS = 4;
    public static final String CONFIG_PATH = "config.properties";
    public static int numOfPlayers;
    public static int numOfRows;
    public static int numOfColumns;
    public static ArrayList<String> playerNames = new ArrayList<>();

    private ConfigReader(){}

    public static void readConfiguration() throws ConfigException {
        Properties properties = new Properties();
        List<String> distinctPlayerList = null;
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH)) {
            properties.load(fileInputStream);

            numOfRows = Integer.parseInt(properties.getProperty("map_rows"));
            numOfColumns = Integer.parseInt(properties.getProperty("map_columns"));

            for (int i = 0; i < MAX_PLAYERS; i++)
                playerNames.add(properties.getProperty("player" + i));

            playerNames = (ArrayList<String>) playerNames.stream().filter(Objects::nonNull).collect(Collectors.toList());
            distinctPlayerList = playerNames.stream().distinct().collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();//todo logger
        }

        if(numOfColumns < MIN_GRID_SIZE || numOfColumns > MAX_GRID_SIZE || numOfRows < MIN_GRID_SIZE ||
                numOfRows > MAX_GRID_SIZE)
            throw new ConfigException("Row size or column size must be equal or greater to 7 and lower or equal to 10!");

        if(playerNames.size() < MIN_PLAYER_NUMBER)
            throw new ConfigException("Minimal player count is 2!");


        if(distinctPlayerList != null && distinctPlayerList.size() != playerNames.size())
            throw new ConfigException("All player names need to be unique!");

        numOfPlayers = playerNames.size();
    }
}
