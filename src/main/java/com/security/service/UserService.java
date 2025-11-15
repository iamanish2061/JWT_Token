package com.security.service;

import com.security.entity.UserPrincipal;
import com.security.entity.Users;
import com.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(12);

    //user
    public Users register(Users user){
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public Map<String, String> login(Users user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        // authenticate is the unimplemented method of AuthenticationManager interface whose one of the implementations is done is ProviderManager class
        // provider manager.authenticate() is called -> then it iterates through the available provider
        // i am using DAO so its dynamically added to the list of providerManager
//          now authentication is done by dao class's object
//        in that method :the flow is
//        1. load the user by username from database (method present in UserDetailsService) and i have implemented that on MyUserDetailsService
//        2. verifying password by encoding entered one to db's password
//        3. returning an authenticated token with authorities


        if (!authentication.isAuthenticated()) throw new RuntimeException("Invalid credentials");

        Users dbUser = userRepo.findByUsername(user.getUsername());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken((UserPrincipal) authentication.getPrincipal(), dbUser.getRole().toString());
        String refreshToken = jwtService.generateRefreshToken((UserPrincipal) authentication.getPrincipal());

        // Save refresh token in DB
        dbUser.setRefreshToken(refreshToken);
        userRepo.save(dbUser);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

//    public Map<String, String> refresh(String refreshToken) {
//        Users user = userRepo.findByRefreshToken(refreshToken);
//        if (user == null) throw new RuntimeException("Invalid refresh token");
//
//        String newAccessToken = jwtService.generateAccessToken(new UserPrincipal(user), user.getRole());
//
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("accessToken", newAccessToken);
//        tokens.put("refreshToken", refreshToken); // keep same refresh token
//        return tokens;
//    }


    //admin
    public List<Users> findAllUsers(){
        return userRepo.findAll();
    }

    public Optional<Users> findUserById(Long id) {
        return userRepo.findById(id);
    }
}
