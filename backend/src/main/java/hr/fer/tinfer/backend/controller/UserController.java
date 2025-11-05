package hr.fer.tinfer.backend.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/:email")
    public String getUserByEmail(@PathVariable String email) {
        return "User email: " + email;
    }

    @PostMapping("/add")
    public String addUser() {
        return "User added";
    }


}
