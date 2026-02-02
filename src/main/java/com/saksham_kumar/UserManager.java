package com.saksham_kumar;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserManager {

    private List<Users> user = new ArrayList<>();

    private final File f = new File("Users.json");

    private final ObjectMapper mapper = new ObjectMapper();

    public UserManager() {
        loadFile();
    }

    private void loadFile() {
        if (!f.exists()) return;

        if (f.length() == 0) return;
        
        try {
            user = mapper.readValue(f, new TypeReference<List<Users>>(){});
        } catch (IOException e) {
            System.out.println("Error while loading File: " + e.getMessage());
        }
    }

    private void savetoFile() {
        try     {
            mapper.writeValue(f, user);
        } catch (IOException e) {
            System.out.println("Some error occured while saving file.");
        }
    }
    
    public String addUser(String userName, String password) {
        boolean present = user.stream().anyMatch(obj -> obj.getuserName().equals(userName));
        if(present) {
            savetoFile();
            return "Username already exists!";
        }
        String encrypted_password = BCrypt.hashpw(password, BCrypt.gensalt());

        user.add(new Users(userName, encrypted_password));
        savetoFile();
        return "User Registered!";
    }

    public boolean loginUser(String userName, String password) {
        Users foundUser = user.stream().filter(u -> u.getuserName().equals(userName)).findFirst().orElse(null);

        if (foundUser == null) {
            return false;
        }

        return BCrypt.checkpw(password, foundUser.getPassword());
    }
}
