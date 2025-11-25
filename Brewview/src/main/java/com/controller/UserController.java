package com.controller;

import com.dto.CreateUserRequest;
import com.dto.ResponseUser;
import com.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public ResponseUser addNewUser(@RequestBody CreateUserRequest userInfo) {
        return userService.addUser(userInfo);
    }
}
