package main.controllers;


import main.services.FileStorageService;
import main.views.*;
import main.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.IOException;


//@CrossOrigin(origins = {"https://moved-temporarily-front.herokuapp.com"}, allowCredentials = "true")
@RestController
public class UserController {

    private @NotNull UserService users;

    public UserController(@NotNull UserService userService) {
        this.users = userService;
    }
/*
    @RequestMapping(path = "/api/user/signup", method = RequestMethod.POST)
    public ResponseEntity signUp(@RequestBody User signUpData, HttpSession httpSession) {
        final UserService.ErrorCodes error = users.addUser(signUpData);
        switch (error) {
            case INVALID_REG_DATA:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMsg.BAD_REQUEST);

            case LOGIN_OCCUPIED:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMsg.CONFLICT);

            case OK:
                httpSession.setAttribute("userLogin", signUpData.getLogin());
                return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMsg.OK);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);
        }
    }
*/
/*
    @RequestMapping(path = "api/user/login", method = RequestMethod.POST)
    public ResponseEntity logIn(@RequestBody LoginForm loginData, HttpSession httpSession) {
        final UserService.ErrorCodes error = users.login(loginData);
        switch (error) {
            case INVALID_AUTH_DATA:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMsg.BAD_REQUEST);

            case INVALID_LOGIN:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.INVALID_LOGIN);

            case INCORRECT_PASSWORD:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMsg.INCORRECT_PASSWORD);

            case OK:
                if (loginData.getLogin() != null) {
                    httpSession.setAttribute("userLogin", loginData.getLogin());
                } else {
                    httpSession.setAttribute("userLogin", users.getLoginByEmail(loginData.getEmail()));
                }
                return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);
        }
    }
*/
/*
    @RequestMapping(path = "/api/user/logout", method = RequestMethod.GET)
    public ResponseEntity logOut(HttpSession httpSession) {
        final String login = (String) httpSession.getAttribute("userLogin");

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.NOT_LOGGED_IN);
        }

        httpSession.invalidate();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);
    }
*/
    @RequestMapping(path = "/api/user/changeEmail", method = RequestMethod.POST)
    public ResponseEntity changeMail(@RequestBody MailForm mailData, HttpSession httpSession) {
        final String login = (String) httpSession.getAttribute("userLogin");

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.NOT_LOGGED_IN);
        }

        final UserService.ErrorCodes error = users.changeMail(mailData, login);

        switch (error) {
            case OK:
                return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);

            case INVALID_LOGIN:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.INVALID_LOGIN);

            case EMAIL_OCCUPIED:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMsg.CONFLICT);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "api/user/changePass", method = RequestMethod.POST)
    public ResponseEntity changePass(@RequestBody PassForm passData, HttpSession httpSession) {
        final String login = (String) httpSession.getAttribute("userLogin");

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.NOT_LOGGED_IN);
        }

        final UserService.ErrorCodes error = users.changePass(passData, login);

        switch (error) {
            case INVALID_LOGIN:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.INVALID_LOGIN);

            case INCORRECT_PASSWORD:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMsg.INCORRECT_PASSWORD);

            case OK:
                return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "api/user/info", method = RequestMethod.GET)
    public ResponseEntity currentUserInfo(HttpSession httpSession) throws IOException {
        FileStorageService fs = new FileStorageService();
        final UserInfoForm  data = new UserInfoForm();
        final String login = (String) httpSession.getAttribute("userLogin");

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.NOT_LOGGED_IN);
        }

        final UserService.ErrorCodes error = users.getUserInfo(data, login);

        switch (error) {
            case INVALID_LOGIN:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.INVALID_LOGIN);

            case OK:
                return ResponseEntity.status(HttpStatus.OK).body(data);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @RequestMapping(path = "api/user/score", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity allScoreBoard() {
        return ResponseEntity.status(HttpStatus.OK).body(users.getScoreBoard());
    }

    @ResponseBody
    @RequestMapping(path = "api/user/score", method = RequestMethod.POST)
    public ResponseEntity partScoreBoard(@RequestBody ScoreRequest scoreReq) {
        if (scoreReq.getPosition() == null || scoreReq.getCount() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMsg.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(users.getScoreBoard(scoreReq.getPosition(), scoreReq.getCount()));
    }

}

