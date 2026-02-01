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

    private final static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) throws IOException{
        
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        class hndlr implements HttpHandler {
            
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                switch (exchange.getRequestMethod()) {
                    case "GET" -> {
                        URI url = exchange.getRequestURI();
                        String data;

                        if(url.getQuery() == null) {
                            data = manager.getExpenses();
                        }else {
                            String[] indx = url.getQuery().split("=");
                            String type = indx[0];
                            String value = indx[1];
                            data = manager.getFilteredExpenses(type, value);
                        }

                        exchange.sendResponseHeaders(200, data.length());
                        
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(data.getBytes());
                        }                              
                    }
                    case "POST" -> {
                        InputStream in = exchange.getRequestBody();
                        Expense obj = mapper.readValue(in, Expense.class);
                        
                        String response = manager.addExpense(obj.getDescription(), obj.getAmount(), obj.getCategory());

                        exchange.sendResponseHeaders(201, response.length());

                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }  
                    }case "DELETE" -> {
                        URI url = exchange.getRequestURI();
                        if(!(url.getQuery() == null)) {
                            String[] indx = url.getQuery().split("=");
                            int id = Integer.parseInt(indx[1]);

                            String response = manager.deleteExpense(id);  
                            exchange.sendResponseHeaders(200, response.length());

                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }  
                        }else {
                            exchange.sendResponseHeaders(404, 0);
                        }
                    }case "PUT" -> {
                        URI url = exchange.getRequestURI();
                        if(!(url.getQuery() == null)) {
                            String[] indx = url.getQuery().split("=");
                            int id = Integer.parseInt(indx[1]);

                            InputStream in = exchange.getRequestBody();

                            Map<String, Object> data = mapper.readValue(in, new TypeReference<Map<String, Object>>(){});
                            String type = data.get("type").toString();
                            Object value = data.get("value");

                            String response = "Invalid Data!";

                            if(type != null && value != null) {
                                if(type.equalsIgnoreCase("amount")) {
                                    response = manager.update_expense(id, Double.parseDouble(value.toString()));
                                }else if (type.equalsIgnoreCase("description") || type.equalsIgnoreCase("date") ||type.equalsIgnoreCase("category")) {
                                    response = manager.update_expense(id, type, value.toString());
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
                            exchange.sendResponseHeaders(404, 0);
                        }
                    }
                    default -> exchange.sendResponseHeaders(404, 0);
                }
            }
            
        }
        HttpHandler handler = new hndlr();
        server.createContext("/expenses", handler);
        
        server.start();

    }
}
