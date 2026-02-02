package com.saksham_kumar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    private static ExpenseManager manager = new ExpenseManager();

    private static UserManager user = new UserManager();

    private static JwtUtil token_manager = new JwtUtil();

    private final static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) throws IOException{
        
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        class hndlr implements HttpHandler {
            
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                switch (exchange.getRequestURI().getPath()) {
                    case "/request" -> {
                        switch (exchange.getRequestMethod()) {
                            case "POST" -> {
                                InputStream in = exchange.getRequestBody();
                                Users obj = mapper.readValue(in, Users.class);
                                
                                String response = user.addUser(obj.getuserName(), obj.getPassword());
                                if(response.equalsIgnoreCase("User Registered!")) {
                                    exchange.sendResponseHeaders(201, response.length());
                                }else {
                                    exchange.sendResponseHeaders(409, response.length());
                                }
        
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }  
                            }
                            default -> exchange.sendResponseHeaders(404, 0);
                        }
                    }case "/login" -> {
                        switch (exchange.getRequestMethod()) {
                            case "POST" -> {
                                InputStream in = exchange.getRequestBody();
                                Users obj = mapper.readValue(in, Users.class);
                                
                                boolean isTrue = user.loginUser(obj.getuserName(), obj.getPassword());
                                String response;
                                if(isTrue) {
                                    String token = token_manager.getJWT(obj.getuserName());
                                    response = String.format("{\"token\": \"%s\"}", token);
                                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                                    exchange.sendResponseHeaders(201, response.length());
                                }else {
                                    response = "Invalid Credentials";
                                    exchange.sendResponseHeaders(404, response.length());
                                }
        
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }  
                            }
                            default -> exchange.sendResponseHeaders(404, 0);
                        }
                    }
                    case "/expenses" -> {

                        String header = exchange.getRequestHeaders().getFirst("Authorization");

                        if(header == null || !header.startsWith("Bearer ")) {
                            exchange.sendResponseHeaders(401, 0);
                            exchange.getResponseBody().close();
                            break;
                        }
                        
                        String userName = token_manager.userNameVerify(header.substring(7));
                        
                        if(userName == null) {
                            exchange.sendResponseHeaders(401, 0);
                            exchange.getResponseBody().close();
                            break;
                        } 

                        switch (exchange.getRequestMethod()) {
                            case "GET" -> {
                                URI url = exchange.getRequestURI();
                                String data;
        
                                if(url.getQuery() == null) {
                                    data = manager.getExpenses(userName);
                                }else {
                                    String[] indx = url.getQuery().split("=");
                                    if (indx.length < 2) {
                                        String resp = "Invalid Query Format";
                                        exchange.sendResponseHeaders(400, resp.length());
                                        try (OutputStream os = exchange.getResponseBody()) {
                                            os.write(resp.getBytes()); 
                                        }
                                        break;
                                    }
                                    String type = indx[0];
                                    String value = indx[1];
                                    data = manager.getFilteredExpenses(type, value, userName);
                                }
        
                                exchange.sendResponseHeaders(200, data.length());
                                
                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(data.getBytes());
                                }                              
                            }
                            case "POST" -> {
                                InputStream in = exchange.getRequestBody();
                                Expense obj = mapper.readValue(in, Expense.class);
                                
                                String response = manager.addExpense(obj.getDescription(), obj.getAmount(), obj.getCategory(), userName);
        
                                exchange.sendResponseHeaders(201, response.length());

                                try (OutputStream os = exchange.getResponseBody()) {
                                    os.write(response.getBytes());
                                }  
                            }case "DELETE" -> {
                                URI url = exchange.getRequestURI();
                                if(!(url.getQuery() == null)) {
                                    String[] indx = url.getQuery().split("=");
                                    int id = Integer.parseInt(indx[1]);
                                    
                                    String response = manager.deleteExpense(id, userName);  
                                    exchange.sendResponseHeaders(200, response.length());
        
                                    try (OutputStream os = exchange.getResponseBody()) {
                                        os.write(response.getBytes());
                                    }  
                                }else {
                                    exchange.sendResponseHeaders(404, -1);
                                }
                            }case "PUT" -> {
                                URI url = exchange.getRequestURI();
                                if(!(url.getQuery() == null)) {
                                    String[] indx = url.getQuery().split("=");
                                    int id = Integer.parseInt(indx[1]);
        
                                    InputStream in = exchange.getRequestBody();
        
                                    Map<String, Object> data = mapper.readValue(in, new TypeReference<Map<String, Object>>(){});
                                    Object typeObj = data.get("type");
                                    Object value = data.get("value");
                                    
                                    String response = "Invalid Data!";
                                    
                                    if(typeObj != null && value != null) {
                                        String type = typeObj.toString();
                                        if(type.equalsIgnoreCase("amount")) {
                                            response = manager.update_expense(id, Double.parseDouble(value.toString()), userName);
                                        }else if (type.equalsIgnoreCase("description") || type.equalsIgnoreCase("date") ||type.equalsIgnoreCase("category")) {
                                            response = manager.update_expense(id, type, value.toString(), userName);
                                        }
                                        exchange.sendResponseHeaders(200, response.length());
                                    }else {
                                        response = "Missing type or value";
                                        exchange.sendResponseHeaders(400, response.length());
                                    }
                                    
                                    
                                    try (OutputStream os = exchange.getResponseBody()) {
                                        os.write(response.getBytes());
                                    }  
                                }else {
                                    exchange.sendResponseHeaders(404, -1);
                                }
                            }
                            default -> exchange.sendResponseHeaders(404, -1);
                        }
                    }
                    default -> exchange.sendResponseHeaders(404, -1);
                }               

            }
            
        }
        HttpHandler handler = new hndlr();
        server.createContext("/", handler);
        System.out.println("Server started at port:8000.");
        server.start();

    }
}
