package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatServer extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    static String flutter , java;
    

    public ChatServer (int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        // Quan el servidor s'inicia
        String host = getAddress().getAddress().getHostAddress();
        int port = getAddress().getPort();
        System.out.println("WebSockets server running at: ws://" + host + ":" + port);
        System.out.println("Type 'exit' to stop and exit server.");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
        
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Quan un client es connecta
        String clientId = getConnectionId(conn);

        // Saludem personalment al nou client
        JSONObject objWlc = new JSONObject("{}");
        objWlc.put("type", "private");
        objWlc.put("from", "server");
        objWlc.put("value", "Welcome to the chat server");
        conn.send(objWlc.toString()); 

        // Li enviem el seu identificador
        JSONObject objId = new JSONObject("{}");
        objId.put("type", "id");
        objId.put("from", "server");
        objId.put("value", clientId);
        conn.send(objId.toString()); 

        // Enviem la direcció URI del nou client a tothom 
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "connected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        JSONObject tb = new JSONObject("{}");
        tb.put("type", "nombre");
        broadcast(tb.toString());

        // Mostrem per pantalla (servidor) la nova connexió
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println("New client (" + clientId + "): " + host);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Quan un client es desconnecta
        String clientId = getConnectionId(conn);

        // Informem a tothom que el client s'ha desconnectat
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "disconnected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println("Client disconnected '" + clientId + "'");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Quan arriba un missatge
        String clientId = getConnectionId(conn);
        System.out.println(message);
        
        try {
            JSONObject objRequest = new JSONObject(message);
            String type = objRequest.getString("type");
            if (type.equalsIgnoreCase("nombre")) {
                String plat = objRequest.getString("plat");
                if(plat.equalsIgnoreCase("flutter")){
                    flutter = objRequest.getString("value");
                }
                if(plat.equalsIgnoreCase("java")){
                    java  = objRequest.getString("value");
                }
                if (java != "" && flutter != "" ){
                    JSONObject objCln = new JSONObject("{}");
                    objCln.put("type", "named");
                    objCln.put("flutter", flutter );
                    objCln.put("java", java);
                    broadcast(objCln.toString());
                }
            }
            if (type.equalsIgnoreCase("broadcast")) {
                String from = objRequest.getString("from");
                if (from.equalsIgnoreCase("flutter")){
                    System.out.println("aaa");              
                    int value1 = objRequest.getInt("value1");   
                    System.out.println(value1);
                    int value2 = objRequest.getInt("value2");   
                    System.out.println(value2);
                    sendjava(conn, value1,value2);
                }
                if (from.equalsIgnoreCase("java")){
                    System.out.println("bbb");
                    String value1 = objRequest.getString("value1");   
                    System.out.println(value1);
                    String value2 = objRequest.getString("value2");   
                    System.out.println(value2);
                    sendflutter(conn,value1,value2);
                }
            }
            if (type.equalsIgnoreCase("tablero")){
                sendtab(conn);
            }
            if (type.equalsIgnoreCase("list")) {
                // El client demana la llista de tots els clients
                System.out.println("Client '" + clientId + "'' requests list of clients");
                sendList(conn);

            } else if (type.equalsIgnoreCase("private")) {
                // El client envia un missatge privat a un altre client
                System.out.println("Client '" + clientId + "'' sends a private message");

                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "private");
                objResponse.put("from", clientId);
                objResponse.put("value", objRequest.getString("value"));

                String destination = objRequest.getString("destination");
                WebSocket desti = getClientById(destination);

                if (desti != null) {
                    desti.send(objResponse.toString()); 
                }
                
            } else if (type.equalsIgnoreCase("broadcast")) {
                // El client envia un missatge a tots els clients
                System.out.println("Client '" + clientId + "'' sends a broadcast message to everyone");

                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "broadcast");
                objResponse.put("from", objRequest.getString("from"));
                String plat = objRequest.getString("from");
                if(plat.equals("flutter")){
                    objResponse.put("value1", objRequest.getString("value1"));
                    objResponse.put("value2", objRequest.getString("value2"));
                }
                if(plat.equals("java")){
                    objResponse.put("value1", objRequest.getString("value1"));
                    objResponse.put("value2", objRequest.getString("value2"));
                }
                broadcast(objResponse.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Quan hi ha un error
        ex.printStackTrace();
    }
    public String lista_j(){
        List<String> images = new ArrayList<>();
        images.add("circle.png");
        images.add("triangle.png");
        images.add("circle.png");
        images.add("heart.png");

        images.add("star.png");
        images.add("triangle.png");
        images.add("star.png");
        images.add("heartP.png");

        images.add("circleR.png");
        images.add("triangleN.png");
        images.add("circleR.png");
        images.add("heart.png");

        images.add("starM.png");
        images.add("triangleN.png");
        images.add("starM.png");
        images.add("heartP.png");
        Collections.shuffle(images);

      
        String json = convertListToJSON(images);
        return json;
    }
    private static String convertListToJSON(List<String> list) {
        JSONArray jsonArray = new JSONArray(list);
        return jsonArray.toString();
    }
    public void runServerBucle () {
        boolean running = true;
        try {
            System.out.println("Starting server");
            start();
            while (running) {
                String line;
                line = in.readLine();
                if (line.equals("exit")) {
                    running = false;
                }
            } 
            System.out.println("Stopping server");
            stop(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }  
    }

    public void sendList (WebSocket conn) {
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "list");
        objResponse.put("from", "server");
        objResponse.put("list", getClients());
        conn.send(objResponse.toString()); 
    }
     public void sendjava (WebSocket conn, int value1,int value2) {
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "move");
        objResponse.put("from", "server");
        objResponse.put("to", "java");
        objResponse.put("value1", value1);
        objResponse.put("value2", value2);
        
        conn.send(objResponse.toString()); 
    }
      public void sendflutter(WebSocket conn, String value1,String value2) {
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "move");
        objResponse.put("to", "flutter");
        objResponse.put("value1", value1);
        objResponse.put("value2", value2);
        conn.send(objResponse.toString()); 
    }
    public void sendtab (WebSocket conn) {
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "tablero");
        conn.send(objResponse.toString()); 
    }

    public String getConnectionId (WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }

    public String[] getClients () {
        int length = getConnections().size();
        String[] clients = new String[length];
        int cnt = 0;

        for (WebSocket ws : getConnections()) {
            clients[cnt] = getConnectionId(ws);               
            cnt++;
        }
        return clients;
    }

    public WebSocket getClientById (String clientId) {
        for (WebSocket ws : getConnections()) {
            String wsId = getConnectionId(ws);
            if (clientId.compareTo(wsId) == 0) {
                return ws;
            }               
        }
        
        return null;
    }
}