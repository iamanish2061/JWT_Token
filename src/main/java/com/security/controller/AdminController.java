package com.security.controller;

import com.security.entity.Users;
import com.security.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Users users){

        return ResponseEntity.ok().build();
    }

    @GetMapping("get-users")
    public ResponseEntity<List<Users>> getAllUsers(){

        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("get-user-details")
    public ResponseEntity<Users> getUserDetails(@RequestParam Long id){
        Optional<Users> optUser = userService.findUserById(id);
        Users user = optUser.orElse(null);
        return ResponseEntity.ok(user);
    }
}
