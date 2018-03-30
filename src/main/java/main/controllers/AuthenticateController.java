package main.controllers;

import main.models.User;
import main.services.UserServiceDAO;
import main.views.ResponseMsg;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class AuthenticateController {
    private UserServiceDAO userService;

    public AuthenticateController(UserServiceDAO userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/api/user/signup", method = RequestMethod.POST)
    public ResponseEntity signUp(@RequestBody User userData, HttpSession session) {
        try {
            userService.singUp(userData);

        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMsg.CONFLICT);
        }

        session.setAttribute("userLogin", userData.getLogin());
        return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);
    }
}
