package com.cydeo.controller;

import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.UserDTO;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")//naming convention, enough for CRUD operations
@Tag(name = "UserController", description = "User API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @RolesAllowed({"Manager", "Admin"})
    @Operation(summary = "Retrieve users")
    public ResponseEntity<ResponseWrapper> getUsers(){
        List<UserDTO> userDTOList=userService.listAllUsers();
        return ResponseEntity.ok(new ResponseWrapper("List of Users retrieved",userDTOList, HttpStatus.OK));
    }
    @GetMapping("/{username}")
    @RolesAllowed("Admin")
    @Operation(summary = "Retrieve user by username")
    public ResponseEntity<ResponseWrapper> getUserByUserName(@PathVariable("username") String username){
        return ResponseEntity.ok(new ResponseWrapper("User retrieved successfully", userService.findByUserName(username),HttpStatus.OK));
    }
    @PostMapping()// works with base
    @RolesAllowed("Admin")
    @Operation(summary = "Create user")
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO user){
        userService.save(user);// we do not normally pass object in the body when create and delete
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("User created",HttpStatus.CREATED));
    }
    @PutMapping()
    @RolesAllowed("Admin")
    @Operation(summary = "Update user")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO user){
        userService.update(user);
        return ResponseEntity.ok(new ResponseWrapper("User Updated",HttpStatus.ACCEPTED));
    }
    @DeleteMapping("/{username}")
    @RolesAllowed("Admin")
    @Operation(summary = "Delete user")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username") String username) throws TicketingProjectException {
        userService.delete(username);
        return ResponseEntity.ok().body(new ResponseWrapper("User deleted",HttpStatus.ACCEPTED));
    }

}
