package main.controllers;


import main.services.UserServiceDAO;
import main.views.MailForm;
import main.views.PassForm;
import main.views.ResponseMsg;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class UserUpdateController {
    private UserServiceDAO userService;

    public UserUpdateController(UserServiceDAO userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/api/user/changeEmail", method = RequestMethod.PUT)
    public ResponseEntity changeEmail(@RequestBody MailForm mailData, HttpSession session) {
        final String login = (String) session.getAttribute("userLogin");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.NOT_LOGGED_IN);
        }

        final UserServiceDAO.ErrorCodes error = userService.changeEmail(login, mailData);

        switch (error) {
            case OK:
                return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);

            case EMAIL_OCCUPIED:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMsg.CONFLICT);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(path = "/api/user/changePass", method = RequestMethod.PUT)
    public ResponseEntity changePass(@RequestBody PassForm passData, HttpSession session) {
        final String login = (String) session.getAttribute("userLogin");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.NOT_LOGGED_IN);
        }

        final UserServiceDAO.ErrorCodes error = userService.changePass(login, passData);

        switch (error) {
            case OK:
                return ResponseEntity.status(HttpStatus.OK).body(ResponseMsg.OK);

            case INCORRECT_PASSWORD:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMsg.INCORRECT_PASSWORD);

            case INVALID_LOGIN:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMsg.INVALID_LOGIN);

             default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);

        }

    }
}
