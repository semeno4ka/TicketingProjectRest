package com.cydeo.service;

import com.cydeo.dto.UserDTO;

import javax.ws.rs.core.Response;

public interface KeycloakService {
    //How to create user from keycloak from java app?
    //response provides user created in keycloak, provided by javax dependency
    Response userCreate(UserDTO user);
    void delete(String username);
}
