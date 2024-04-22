package com.battlebyte.battlebyte.entity.dto;

import lombok.Data;

public interface UserDTO {
//    private String userName;
//    private String password;

    String getUserName();
    String getPassword();
    String getToken();
    String getRole();

    void setToken();
}
