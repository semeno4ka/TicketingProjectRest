package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.ResponseWrapper;
import com.cydeo.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@Tag(name = " ProjectController", description = "Project API")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping()
    @RolesAllowed("Manager")
    @Operation(summary = "Retrieve projects")
    public ResponseEntity<ResponseWrapper> getProjects(){
        return ResponseEntity.ok(new ResponseWrapper("List of projects retrieved", projectService.listAllProjects(), HttpStatus.OK));
    }
    @GetMapping("/{projectCode}")
    @RolesAllowed("Manager")
    @Operation(summary = "Retrieve project by project code")
    public ResponseEntity<ResponseWrapper> getProjectByCode(@PathVariable("projectCode") String projectCode){
        return ResponseEntity.ok(new ResponseWrapper("Project successfully retrieved", projectService.getByProjectCode(projectCode), HttpStatus.OK));
    }
    @PostMapping
    @RolesAllowed({"Manager", "Admin"})
    @Operation(summary = "Create project")
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO project){
        projectService.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("Project created", HttpStatus.CREATED));
    }
    @PutMapping
    @RolesAllowed("Manager")
    @Operation(summary = "Update project")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO projectDTO){
        projectService.update(projectDTO);
        return ResponseEntity.ok(new ResponseWrapper("Project updated", HttpStatus.OK));
    }
    @DeleteMapping("/{projectCode}")
    @RolesAllowed("Manager")
    @Operation(summary = "Delete project")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("projectCode") String projectCode){
        projectService.delete(projectCode);
        return ResponseEntity.ok(new ResponseWrapper("Project deleted", HttpStatus.OK));
    }
    @GetMapping("/manager/project-status")
    @RolesAllowed("Manager")
    @Operation(summary = "Retrieve projects by manager")
    public ResponseEntity<ResponseWrapper> getProjectByManager(){
      List<ProjectDTO> projectDTOList= projectService.listAllProjectDetails();
       return ResponseEntity.ok(new ResponseWrapper("List of projects retrieved", projectDTOList, HttpStatus.OK));
    }
    @PutMapping("/manager/complete/{projectCode}")
    @RolesAllowed("Manager")
    @Operation(summary = "Retrieve completed projects by manager")
    public ResponseEntity<ResponseWrapper> managerCompleteProject(@PathVariable("projectCode") String projectCode){
        projectService.complete(projectCode);
        return ResponseEntity.ok(new ResponseWrapper("Project is completed",  HttpStatus.OK));

    }
}
