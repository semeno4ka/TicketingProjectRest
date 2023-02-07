package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.TestResponseDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Gender;
import com.cydeo.enums.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc// since we are testing all layers
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;
    static UserDTO manager;
    //we create static fields because we will use
    // @BeforeAll methods which are static and require static member
    static ProjectDTO project;
    //hardcoded token use
    static String token;

    @BeforeAll //to create some sample data to use for testing
    static void setUp(){
        token="Bearer "+ getToken();// separate method created in the class
      manager =new UserDTO(2L, "","","ozzy","abc1","",
              true,"", new RoleDTO(2L,"Manager"), Gender.MALE);

      project = new ProjectDTO("ApII Project","PR001", manager,
              LocalDate.now(),LocalDate.now().plusDays(5),"Some details", Status.OPEN);
    }

    @Test
    void givenNoToken_getProjects() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/project"))
                .andExpect(status().is4xxClientError()); // check if cannot retrieve data because no toke-edge case
    }

    @Test
    void givenToken_getProject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/project")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].projectCode").exists())
                // to get inside json and check required field match->Jway -jsonpath (git repo)
                //data comes from our response wrapper field which has a list of Projects-> give me the 1st element projectCode
                .andExpect(jsonPath("$.data[0].assignedManager.userName").exists())
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isNotEmpty())
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isString())
                .andExpect(jsonPath("$.data[0].assignedManager.userName").value("ozzy"));
    }

   @Test
    void givenToken_createProject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/project")
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(toJsonString(project)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Project created"));
   }

   @Test
   void givenToken_updateProject() throws Exception {

        project.setProjectName("API Project-2");

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/project")//will fail because of 'create' in app property
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(toJsonString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project updated"));
   }

   @Test
   void givenToken_deleteProject() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/project/" + project.getProjectCode())
                 //since it ProjectCode is string parameter we can concatenate it
                .header("Authorization",token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project deleted"));
    }



   //Ready Code to convert dto to json and return it as string format instead of manual input as we did before
    private String toJsonString(final Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);//to place dates without times
        objectMapper.registerModule(new JavaTimeModule());//change default 2022,12,18 -> 2022/12/18
        return objectMapper.writeValueAsString(obj);
    }

    //Ready Code to skip manual token pass
    private static String getToken() {

        RestTemplate restTemplate = new RestTemplate();//real api request send to keycloak

        HttpHeaders headers = new HttpHeaders();//get token
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();//assign properties

        map.add("grant_type", "password");
        map.add("client_id", "ticketing-app");
        map.add("client_secret", "9kyDRsp1GJD6ZnY4hedkAJr5ziY6Snoe");
        map.add("username", "ozzy");
        map.add("password", "abc1");
        map.add("scope", "openid");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<TestResponseDTO> response =
                restTemplate.exchange("http://localhost:8080/auth/realms/cydeo-dev/protocol/openid-connect/token",
                        HttpMethod.POST,
                        entity,
                        TestResponseDTO.class);

        if (response.getBody() != null) {
            return response.getBody().getAccess_token();
        }

        return "";

    }
}