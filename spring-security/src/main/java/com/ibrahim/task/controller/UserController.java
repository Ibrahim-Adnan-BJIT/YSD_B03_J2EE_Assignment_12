package com.ibrahim.task.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibrahim.task.constants.AppConstants;
import com.ibrahim.task.exception.ResourceNotFoundException;
import com.ibrahim.task.model.UserDto;
import com.ibrahim.task.model.UserLoginReqModel;
import com.ibrahim.task.service.UserService;
import com.ibrahim.task.utils.JWTUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String hello(){
        return "Hello From Admin";
    }

    @GetMapping("/hello2")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String hello2(){
        return "Hello2 from User";
    }
    @PostMapping("/registration")
    public ResponseEntity<?> register (@RequestBody UserDto userDto) {
        try {
            UserDto createdUser = userService.createUser(userDto);
            String accessToken = JWTUtils.generateToken(createdUser.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("user", createdUser);
            response.put("accessToken", AppConstants.TOKEN_PREFIX + accessToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public void login(@RequestBody UserLoginReqModel userLoginReqModel, HttpServletResponse response) throws IOException {
        try {


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginReqModel.getEmail(), userLoginReqModel.getPassword()));


            if (authentication.isAuthenticated()) {
                UserDto userDto = userService.getUser(userLoginReqModel.getEmail());
                String accessToken = JWTUtils.generateToken(userDto.getEmail());

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("userId", userDto.getUserId());
                responseBody.put("email", userDto.getEmail());
                responseBody.put(AppConstants.HEADER_STRING, AppConstants.TOKEN_PREFIX + accessToken);

                response.setContentType("application/json");
                response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Authentication failed");
                errorResponse.put("message", "Invalid email or password");
                response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            }
        } catch (ResourceNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        }
    }

}
