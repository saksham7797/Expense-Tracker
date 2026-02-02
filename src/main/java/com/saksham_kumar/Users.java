package com.saksham_kumar;

public class Users {
    private String userName;
    private String password;

    public Users() {}

    public Users(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getuserName() {
        return userName;
    }
    
    public String getPassword() {
        return password;
    }
}
