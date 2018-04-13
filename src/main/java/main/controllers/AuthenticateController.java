package main.controllers;

import main.models.User;
import main.services.UserServiceDAO;
import main.views.LoginForm;
import main.views.ResponseMsg;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = {"https://moved-temporarily-front.herokuapp.com"}, allowCredentials = "true")
public class AuthenticateController {
    private UserServiceDAO userService;

    public AuthenticateController(UserServiceDAO userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/api/user/signup", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signUp(@RequestBody User userData, HttpSession session) {
        try {
            if (userData.getLogin() == null || userData.getEmail() == null || userData.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMsg.BAD_REQUEST);
            }
            userService.signUp(userData);

        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMsg.CONFLICT);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);
        }

        session.setAttribute("userLogin", userData.getLogin());
         session.setMaxInactiveInterval(4 * 6 * 10 * 10 * 6 * 6);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMsg.CREATED);
    }

    @RequestMapping(path = "/api/user/login", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody LoginForm loginData, HttpSession session) {
        if (!loginData.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMsg.BAD_REQUEST);
        }

        final UserServiceDAO.ErrorCodes error =  userService.login(loginData);

        switch (error) {
            case INVALID_AUTH_DATA:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMsg.INVALID_LOGIN);
            case INCORRECT_PASSWORD:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMsg.INCORRECT_PASSWORD);
            case OK:
                session.setAttribute("userLogin", loginData.getLogin());
                // Magic number обход(
                 session.setMaxInactiveInterval(4 * 6 * 10 * 10 * 6 * 6);
                return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/api/user/logout", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity logOut(HttpSession httpSession) {
        final String login = (String) httpSession.getAttribute("userLogin");

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.NOT_LOGGED_IN);
        }

        httpSession.invalidate();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);
    }

}
