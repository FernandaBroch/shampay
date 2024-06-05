package com.br.shampay.controller;

import com.br.shampay.entities.Transaction;
import com.br.shampay.entities.User;
import com.br.shampay.services.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping
    @ApiResponse(responseCode = "201" )
    public ResponseEntity<Void> create(@RequestBody User user ) throws IOException, InvalidFormatException {
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
