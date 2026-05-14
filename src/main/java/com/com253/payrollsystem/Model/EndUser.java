package com.com253.payrollsystem.Model;

public class EndUser {
    
    public enum Role {
        ADMIN,
        EMPLOYEE
    }
    
    private final String username;
    private final String passwordHash;
    private final Role role;
    private final String linkedEmployeeId;
    
    public EndUser(String username, String passwordHash, Role role, String linkedEmployeeId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.linkedEmployeeId = linkedEmployeeId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public Role getRole() {
        return role;
    }
    
    public String getLinkedEmployeeId() {
        return linkedEmployeeId;
    }
}