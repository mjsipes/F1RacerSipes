
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value = "/GamingPageWebSocket", configurator = GamingPageWebSocket.CustomConfigurator.class)
public class GamingPageWebSocket {
	
	private static Map<Session, PlayerThread> playerMap = new HashMap<Session, PlayerThread>();	
	
    public static class CustomConfigurator extends ServerEndpointConfig.Configurator {
        @Override
      //the following 4 lines of code were provided by chatpt
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
            HttpSession httpSession = (HttpSession) request.getHttpSession();
            config.getUserProperties().put("gameServerName", httpSession.getAttribute("gameServerName"));
            config.getUserProperties().put("username", httpSession.getAttribute("username"));
        }
    }

    @OnOpen
    public void open(Session session, EndpointConfig config) {
    	System.out.println("GamingPageWebSocket: opening new connection, retrieving cookies, putting new player into player map");
        String gameServerName = (String) config.getUserProperties().get("gameServerName");
        String username = (String) config.getUserProperties().get("username");
        System.out.println(gameServerName);
        playerMap.put(session, new PlayerThread(this, session, username, gameServerName, f1racerServlet.gameServers.get(gameServerName)));
    }
	
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
	    System.out.println("GamingPageWebSocket: to " + playerMap.get(session).userName + ", routing message: "  + message);
	    playerMap.get(session).handleMessage(message);
	}
	
	@OnClose
	public void close(Session session) {
		System.out.println("GamingPageWebSocket: 1 Disconnecting from " + playerMap.get(session).userName + " - writing playerstats to database and removing player from game server vector");
		//playerMap.get(session).updateGameStats();
		playerMap.get(session).gameServer.removePlayer(playerMap.get(session));
		playerMap.remove(session);
	}
	
	@OnError
	public void error(Throwable error) {
		System.out.println("GamingPageWebSocket: Error!" + error);
		error.printStackTrace();
	}
	
}
