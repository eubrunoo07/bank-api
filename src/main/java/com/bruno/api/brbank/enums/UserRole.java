package com.bruno.api.brbank.enums;

public enum UserRole {
    MERCHANT("merchant"),
    COMMON_USER("common_user"),
    ADMIN("admin");

    private String role;
    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }

}
