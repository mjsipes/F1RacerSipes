import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;


public class gameServer extends Thread {

    String name;
    String difficulty;
    String customTopic;
    String prompt;
    
    volatile boolean start = false;
    volatile boolean stop = false;
    PlayerThread winner = null;
    
    Set<PlayerThread> players = new HashSet<>();

    public gameServer(String name, String difficulty, String customTopic) {
        this.name = name;
        this.difficulty = difficulty;
        this.customTopic = customTopic;
        this.prompt = "loading prompt...";
        System.out.println("gameServer: initializing...");
        this.start(); // Start the thread
    }

    public void addPlayer(PlayerThread player) {
    	System.out.println("gameServer: adding player " + player.userName);
        players.add(player);
    }

    public void removePlayer(PlayerThread player) {
    	System.out.println("gameServer: removing player "+ player.userName);
        players.remove(player);
        if (players.isEmpty()) {
        	f1racerServlet.gameServers.remove(name);
        	}
    }

    public void run() {
        System.out.println("gameServer:" + name + " - running");
        System.out.println("gameServer:" + name + "generating prompt");
        this.prompt = GeneratePrompt.generatePrompt(Integer.parseInt(difficulty), customTopic);
        sendAll("prompt", prompt);
        System.out.println("gameServer:" + name + " - prompt generated: " + prompt);
        // Game logic
        System.out.println("gameServer:" + name + " - starting Game");
        start = true;
        sendAll("gameStart", true);
        while (start && !stop) {
            // Game loop
        }
        sendAll("gameStop", stop);
        System.out.println("gameServer:" + name + " - game server terminated");
    }

    
    public void sendPositionUpdate() {
    	System.out.println("gameServer: sending position update ");
    	//the following 5 lines of code were provided by chatpt
        List<Map<String, String>> statuses = new ArrayList<>();
        for (PlayerThread player : players) {
            Map<String, String> playerStatus = new HashMap<>();
            playerStatus.put("serverName", player.userName);
            playerStatus.put("gameStatus", String.valueOf(player.percentComplete));
            statuses.add(playerStatus);
        }
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(statuses);
        sendAll("carPosition", jsonResponse);
    }
    
    public void sendAll(String type, Object value) {
    	System.out.println("gameServer: sending to all - " + type + " - " + value);
        for (PlayerThread player : players) {
            try {
                player.send(type, value);
            } catch (IOException e) {
                e.printStackTrace();
            }       
        }
    }
}