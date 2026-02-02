package com.saksham_kumar;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExpenseManager {
    
    private List<Expense> expenses = new ArrayList<>();
    
    private final File f = new File("Expenses.json");

    private final ObjectMapper mapper = new ObjectMapper();

    public ExpenseManager() {
        loadFile();
    }


    public String getExpenses(String userName) {
        try {
            List<Expense> lst = expenses.stream().filter(e -> e.getUserName().equals(userName)).collect(Collectors.toList());

            return mapper.writeValueAsString(lst);
        } catch (IOException e) {
            return "[]";
        }
    }

    private void loadFile() {
        if (!f.exists()) return;

        if (f.length() == 0) return;
        
        try {
            expenses = mapper.readValue(f, new TypeReference<List<Expense>>(){});
        } catch (IOException e) {
            System.out.println("Error while loading File: " + e.getMessage());
        }
    }

    private void savetoFile() {
        try     {
            mapper.writeValue(f, expenses);
        } catch (IOException e) {
            System.out.println("Some error occured while saving file.");
        }
    }

    public String addExpense(String desc, double amount, String category, String userName) {
        int id = generateID();
        String date = LocalDate.now().toString();
        expenses.add(new Expense(id, desc, amount, category, date, userName));
        savetoFile();
        return "Expense Added!";
    }

    public String deleteExpense(int id, String userName) {
        boolean removed = expenses.removeIf(e -> e.getId() == id && e.getUserName().equals(userName));
        if(removed) {
            savetoFile();
            return "Expense Deleted";
        }
        return "Expense not Found.";
    }

    public String update_expense(int id, String type, String data, String userName) {
        for (Expense obj : expenses) {
            if(obj.getId() == id && obj.getUserName().equals(userName)) {
                switch (type.toLowerCase()) {
                    case "description" -> obj.setDescription(data);
                    case "category" -> obj.setCategory(data);
                    case "date" -> obj.setDate(data);
                    default -> {return "Invalid Update Type";}
                }
            }
        }
        savetoFile();
        return "Expenses Updated!";
    }

    public String update_expense(int id, double amt, String userName) {
        boolean isUpdated = false;
        for (Expense obj : expenses) {
            if(obj.getId() == id && obj.getUserName().equals(userName)) {
                obj.setAmount(amt);
                isUpdated = true;
                break;
            }
        }
        if(isUpdated) {
            savetoFile();
            return "Amount Updated!";
        }
        return "Expense not found or Acess Denied";
    }

    public String getFilteredExpenses(String type, String value, String userName) {
        List<Expense> filtered = new ArrayList<>();
        LocalDate today = LocalDate.now();

        if ("category".equalsIgnoreCase(type)) {
            filtered = expenses.stream()
                    .filter(e -> e.getUserName().equals(userName))
                    .filter(e -> e.getCategory().equalsIgnoreCase(value))
                    .collect(Collectors.toList());
        } else if ("filter".equalsIgnoreCase(type)) {
            LocalDate limit = switch (value.toLowerCase()) {
                case "week" -> today.minusWeeks(1);
                case "month" -> today.minusMonths(1);
                case "3month" -> today.minusMonths(3);
                default -> today;
            };
            filtered = expenses.stream()
                    .filter(e -> e.getUserName().equals(userName))
                    .filter(e -> LocalDate.parse(e.getDate()).isAfter(limit) || LocalDate.parse(e.getDate()).isEqual(limit))
                    .collect(Collectors.toList());
        }

        try {
            return mapper.writeValueAsString(filtered);
        } catch (IOException e) {
            return "[]";
        }
    }

    private int generateID() {
        if(expenses.isEmpty()) {
            return 1;
        }

        int maxId = 0;
        for(Expense obj : expenses) {
            if(obj.getId() > maxId) {
                maxId = obj.getId();
            }
        }
        return maxId+1;
    }
}
