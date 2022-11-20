package com.yrol.blog.controller;

import com.yrol.blog.dto.JwtAuthResponse;
import com.yrol.blog.dto.LoginDto;
import com.yrol.blog.dto.SignUpDto;
import com.yrol.blog.entity.Role;
import com.yrol.blog.entity.User;
import com.yrol.blog.repository.RoleRepository;
import com.yrol.blog.repository.UserRepository;
import com.yrol.blog.security.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

/**
 * Rest controller for Login and Sign up
 * Swagger Annotations - @Api, @ApiOperation
 * */

@Api(value = "Auth controller exposes Register and Login APIs")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Signin when using basic http login (username and password) without JWT
     * Returns signin successful string message
     * */
//    @PostMapping("/signin")
//    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        return new ResponseEntity<>("User signed in successfully", HttpStatus.OK);
//    }


    /**
     * Signin when using JWT
     * Returns JWT token
     * */

    @ApiOperation(value = "REST API for user Login")
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@RequestBody @Valid LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // get token from token provider
        String token = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @ApiOperation(value = "REST API for user Register")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignUpDto signUpDto) {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(signUpDto.getEmail());
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        // Assigning admin role - ideally we should give ROLE_USER and later an admin user can give admin privileges to this user.
        Role role = roleRepository.findByName("ROLE_ADMIN").get();

        user.setRoles(Collections.singleton(role));

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }
}
