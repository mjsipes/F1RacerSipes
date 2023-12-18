import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.mysql.cj.Session;

import gsonClasses.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlayerThread extends Thread{
	
	GamingPageWebSocket gpws;
	private javax.websocket.Session session;
	
	String userName = null;
	String gameServerName;
	gameServer gameServer;
	volatile boolean isWinner = false;
	volatile double percentComplete = 0;


		
	public PlayerThread(GamingPageWebSocket gpws, javax.websocket.Session session, String userName, String gameServerName, gameServer gameServer)  {
		System.out.println("PlayerThread: constructor for - " + userName); 
		this.gpws = gpws;
		this.session = session;
		this.userName = userName;
		this.gameServerName = gameServerName;
		this.gameServer = gameServer;
		gameServer.addPlayer(this);
        try {
			send("userName", userName);
            send("gameServerName", gameServerName);
            send("prompt", gameServer.prompt);
            send("gameStart", gameServer.start);
            send("gameStop", gameServer.stop);
            send("winner", gameServer.winner);
            gameServer.sendPositionUpdate();
		} catch (IOException e) {
			e.printStackTrace();
		}        
        
		this.start();
	}
	
	public void run() {
	}
	
	public void handleMessage(String message) {
		System.out.println("PlayerThread: handleMessage: " + message);
	    JsonParser parser = new JsonParser();
	    JsonObject jsonObject = parser.parse(message).getAsJsonObject();
	    String type = jsonObject.get("type").getAsString();
	    
        if ("percentComplete".equals(type)) {
        	String value = jsonObject.get("value").getAsString();
            try {
                percentComplete = Double.parseDouble(value);
                gameServer.sendPositionUpdate();
                
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in message value");
            }
        }
        else if ("isWinner".equals(type)) {
        	String value = jsonObject.get("value").getAsString();
        	System.out.println("PlayerThread: " + userName + " is winner!");
        	isWinner = true;
        	gameServer.stop = true;
        	gameServer.winner = this;
        }
        else if ("gameStats".equals(type)) {
        	JsonObject valueObject = jsonObject.get("value").getAsJsonObject();
        	updateGameStats(valueObject);
        }
        
    }
	
	void updateGameStats(JsonObject value) {
		System.out.println("PlayerThread: updateGameStats ");
		if (userName == "guest")return;
		Gson gson = new Gson();
	    GameStats gameStats = gson.fromJson(value, GameStats.class);
		PlayerStats playerStats = jdbc.getPlayerStats(userName);
		PlayerStats updatedPlayerStats = new PlayerStats();
		updatedPlayerStats.userName = userName;
		updatedPlayerStats.gamesPlayed = ++playerStats.gamesPlayed;
		updatedPlayerStats.gamesWon = playerStats.gamesWon;
		if (isWinner) updatedPlayerStats.gamesWon = ++playerStats.gamesWon ;
		updatedPlayerStats.totalCharactersTyped = playerStats.totalCharactersTyped + gameStats.numCharactersTyped;
		updatedPlayerStats.totalWordsTyped = playerStats.totalWordsTyped + gameStats.numCharactersTyped/5;
		updatedPlayerStats.bestWPM = (int) Math.max(playerStats.bestWPM, gameStats.CPM/5.0);
		jdbc.updatePlayerStats(updatedPlayerStats);
	}
	
	public void send(String type, Object value) throws IOException {
	    // Create a map to hold the message components
	    Map<String, Object> messageMap = new HashMap<>();
	    messageMap.put("type", type);
	    messageMap.put("value", value);
	    // Use Gson to serialize the map to JSON
	    Gson gson = new Gson();
	    String jsonMessage = gson.toJson(messageMap);
	    // Send the JSON string
	    synchronized(session) {
	        session.getBasicRemote().sendText(jsonMessage);
	    }
	    System.out.print( "PlayerThread: send " + userName + " - " + jsonMessage);
	}

}

