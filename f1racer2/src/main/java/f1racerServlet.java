
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import gsonClasses.*;

@WebServlet("/f1racerServlet")
public class f1racerServlet extends HttpServlet {
	 private static final long serialVersionUID = 1L;
	 public static Map<String, gameServer> gameServers = new HashMap<>();
	 	      
//    public f1racerServlet() {
//    	System.out.println("f1racerServlet: entering");
//            try {
//				this.start();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    }
//    public void start() throws IOException {}
//    
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String action = request.getParameter("action");
    	if ("GuestLoginServlet".equals(action)) {
    		System.out.println("f1racerServlet: GuestLoginServlet - setting username to guest");
    		//the following 3 lines of code were provided by chatpt
            HttpSession session = request.getSession();
            session.setAttribute("username", "guest");
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            response.sendRedirect("PreGamingPage.html");
    	}
    	else if ("PreGamingPageServlet".equals(action)) {
    	    System.out.println("f1racerServlet: PreGamingPageServlet - getting player stats");
    	    String userName = null;
    	    HttpSession session = request.getSession(false);
    	    if (session != null && session.getAttribute("username") != null) {
    	        userName = (String) session.getAttribute("username");
    	    }
    	    PlayerStats stats = jdbc.getPlayerStats(userName);
    	    Gson gson = new Gson();
    	    String jsonResponse;
    	    if (stats != null) {
    	        jsonResponse = gson.toJson(stats);
    	    } else {
    	        jsonResponse = "{\"userName\": \"guest\"}";
    	    }
    	    response.setContentType("application/json");
    	    response.setCharacterEncoding("UTF-8");
    	    response.getWriter().write(jsonResponse);
    	    System.out.println("f1racerServlet: PreGamingPageServlet: json response: " + jsonResponse);
    	}
    	else if ("JoinGamePageServlet".equals(action)) {
    	    System.out.println("f1racerServlet: JoinGamePageServlet - getting list of active game servers");
    	  //the following 7 lines of code were provided by chatpt
    	    List<GameServerInfo> serverInfos = new ArrayList<>();
    	    for (Map.Entry<String, gameServer> entry : f1racerServlet.gameServers.entrySet()) {
    	        String key = entry.getKey();
    	        gameServer server = entry.getValue();
    	        boolean start = server.start;
    	        String gameStatus = start ? "in progress" : "waiting to start";
    	        serverInfos.add(new GameServerInfo(key, gameStatus));
    	    }
    	    Gson gson = new Gson();
    	    String jsonResponse = gson.toJson(serverInfos);
    	    response.setContentType("application/json");
    	    response.setCharacterEncoding("UTF-8");
    	    response.getWriter().write(jsonResponse);
    	    System.out.println("f1racerServlet: JoinGamePageServlet: json response: " + jsonResponse);
    	}

    	
    	
    	
    	
    	
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String action = request.getParameter("action");
    	if ("StartGamePageServlet".equals(action)) {
    		System.out.println("f1racerServlet: StartGamePageServlet - creating new game server thread ");
            String gameServerName = request.getParameter("gameServerName");
            String difficulty = request.getParameter("difficulty");
            String customTopic = request.getParameter("customTopic");
            createGameServer(gameServerName, difficulty, customTopic);
            HttpSession session = request.getSession();
        	session.setAttribute("gameServerName", gameServerName);
    	} else if ("LoginPageServlet".equals(action)) {
    		System.out.println("f1racerServlet: LoginPageServlet - logging in ");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            boolean loginSuccess = jdbc.login(username, password);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (loginSuccess) {
            	System.out.println("f1racerServlet - LoginPageServlet: login success");
        	    HttpSession session = request.getSession();
        	    session.setAttribute("username", username);
        	    session.setMaxInactiveInterval(30 * 60);
                response.getWriter().write("{\"status\": \"success\"}");
            } else {
        		System.out.println("f1racerServlet: LoginPageServlet - login failed ");
                response.getWriter().write("{\"status\": \"fail\"}");
            }
    	} else if ("RegisterPageServlet".equals(action)) {
    		System.out.println("f1racerServlet: RegisterPageServlet - registering ");
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            int signUpStatus = jdbc.signUp(username, password);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if (signUpStatus == 0) {
        		System.out.println("f1racerServlet: RegisterPageServlet - registration succesful ");
            	    HttpSession session = request.getSession();
            	    session.setAttribute("username", username);
            	    session.setMaxInactiveInterval(30 * 60);
                response.getWriter().write("{\"status\": \"success\"}");
            } else if (signUpStatus == 1) {
                response.getWriter().write("{\"status\": \"username_exists\"}");
            } else {
        		System.out.println("f1racerServlet: RegisterPageServlet - registration failed ");
                response.getWriter().write("{\"status\": \"fail\"}");
            }
    	} else if ("JoinGamePageServlet".equals(action)) {
    		System.out.println("f1racerServlet: JoinGamePageServlet - joining game server ");
            String gameServerName = request.getParameter("gameServerName");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8"); 
            if (f1racerServlet.gameServers.containsKey(gameServerName)){
            	HttpSession session = request.getSession();
            	session.setAttribute("gameServerName", gameServerName); // Storing or updating the game server name in the session
                response.getWriter().write("{\"status\": \"success\"}");
            }
            else {
            	response.getWriter().write("{\"status\": \"fail\"}");
            }
    	}
    	
    	
    	 	
    }



    public synchronized void createGameServer(String gameServerName, String difficulty, String customTopic) {
        if (!f1racerServlet.gameServers.containsKey(gameServerName)) {
            gameServer gameServer = new gameServer(gameServerName, difficulty, customTopic);
            f1racerServlet.gameServers.put(gameServerName, gameServer);
            new Thread(gameServer).start();
        }
    }


}

