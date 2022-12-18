package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.awt.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc// since we are testing all layers
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    static UserDTO manager;//we create this fields because we will use @BeforeAll methods which are static and require static member
    static ProjectDTO project;
    //hardcoded token use
    static String token;

    @BeforeAll
    static void setUp(){//to create some sample data to use for testing
        token="Bearer "+ "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJZVmw4V2tqQzBmcjFRdXBUWW9mdEZ1RXU5dFNjdzFLQlF2U3JPZTA0R0hVIn0.eyJleHAiOjE2NzE0MjMwMzYsImlhdCI6MTY3MTQwNTAzNiwianRpIjoiYmZmZTA0MmMtZGYwNy00MGZhLWFjOTEtOGZmYmIzMzAwODY1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL2N5ZGVvLWRldiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2MTUxOTdlOS01ZWVmLTRjZWItODQ2NC0xNzk1NDgwZGFiNGUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ0aWNrZXRpbmctYXBwIiwic2Vzc2lvbl9zdGF0ZSI6ImI5MTYxOWM0LTNiOTctNGQ3Yi1hNDdlLTZkMzlkNGRiZmI5NSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgxIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1jeWRlby1kZXYiXX0sInJlc291cmNlX2FjY2VzcyI6eyJ0aWNrZXRpbmctYXBwIjp7InJvbGVzIjpbIk1hbmFnZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJzaWQiOiJiOTE2MTljNC0zYjk3LTRkN2ItYTQ3ZS02ZDM5ZDRkYmZiOTUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicHJlZmVycmVkX3VzZXJuYW1lIjoib3p6eSJ9.gorrjYYPpQVlRf6Z55bg6mj1CsZznDLrrbFV400Fk_dhib9ncXpfjFSSFKoiDfbGAQWSNX76f3OYBZvtNVTp7c1icNe05W8vknPMEujz_RrD95IukC_MkEoMU0YD_BUm0ICZmxHDGTM3ZqeP88fGvGoqerfwjht7N0qh2agZhVsr9zB_ZWoUj4Cydjhn7AaVNt10Zuc-HBykBWw1-ds5FiE_TrdpG48iYxx-iq6NxEuQYR7ERJdLoznFg8K2QArmhxNZT3P4MHO2rVgABIGsJDga2zvi57gRvlnWv3oK9x4zKUFMByMgoBZghXderZJ2MiXZ6X_aJ69YUi3zbjvw1w";
      manager =new UserDTO(2L, "","","ozzy","abc1","", true,"", new RoleDTO(2L,"Manager"), Gender.MALE);

      project = new ProjectDTO("ApII Project","PR001", manager, LocalDate.now(),LocalDate.now().plusDays(5),"Some details", Status.OPEN);
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
                .andExpect(jsonPath("$.data[0].projectCode").exists())// to get inside json and check required field match->jayway -jsonpath (git repo)
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

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/project/" + project.getProjectCode())//since it ProjectCode is string parameter we can concatenate it
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
}