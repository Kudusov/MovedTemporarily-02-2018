package main.controllers;

import main.services.UserServiceDAO;
import main.views.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/* Добавить метода для ScoreBoard */
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://moved-temporarily-front.herokuapp.com", "https://moved-temp-front.herokuapp.com"}, allowCredentials = "true")
public class UserInfoController {
    private UserServiceDAO userService;

    public UserInfoController(UserServiceDAO userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/api/user/info", method = RequestMethod.GET)
    public ResponseEntity currentUserInfo(HttpSession session) {
        final String login = (String) session.getAttribute("userLogin");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMsg.NOT_LOGGED_IN);
        }

        final UserInfoForm result = new UserInfoForm();
        final UserServiceDAO.ErrorCodes error = userService.getUserInfo(login, result);

        switch (error) {
            case OK:
                return ResponseEntity.status(HttpStatus.OK).body(result);

            case INVALID_LOGIN:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMsg.INVALID_LOGIN);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(path = "/api/user/score", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity scoreBoard(@RequestBody(required = false) ScoreRequest scoreReq) {
        final List<ScoreView> res = new ArrayList<>();
        final UserServiceDAO.ErrorCodes error;

        if (scoreReq == null || (scoreReq.getCount() == null && scoreReq.getPosition() == null)) {
            error = userService.getAllScoreBoard(res);
        } else {
            error = userService.getPartScoreBoard(scoreReq, res);
        }

        switch (error) {
            case OK:
                return ResponseEntity.status(HttpStatus.OK).body(res);
             default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMsg.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(path = "/api/about", method = RequestMethod.GET)
    public ResponseEntity aboutText() {
        return ResponseEntity.status(HttpStatus.OK).body(new AboutForm(null));
    }
}
