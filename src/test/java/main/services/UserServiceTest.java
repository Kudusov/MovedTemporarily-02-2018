package main.services;

import main.models.User;
import main.views.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserServiceDAO userServiceDAO;

    private final User testUser = new User("e.mail@mail.ru", "test", "test", 1337);

    @Test
    public void signUpSuccess() {
        userServiceDAO.signUp(testUser);
    }

    @Test(expected = DuplicateKeyException.class)
    public void signUpDuplicateEmail() {
        userServiceDAO.signUp(testUser);
        userServiceDAO.signUp(new User(testUser.getEmail(), "test2", "test", 0));
    }


    @Test(expected = DuplicateKeyException.class)
    public void signUpDuplicateLogin() {
        userServiceDAO.signUp(testUser);
        userServiceDAO.signUp(new User("mail@mail.ru", testUser.getLogin(), "test", 0));
    }

    @Test
    public void loginCorrect() {
        userServiceDAO.signUp(testUser);
        final LoginForm loginForm = new LoginForm(testUser.getLogin(), null, testUser.getPassword());
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.login(loginForm);

        assertEquals(testUser.getLogin(), loginForm.getLogin());
        assertEquals(null, loginForm.getEmail());
        assertEquals(testUser.getPassword(), loginForm.getPassword());
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);

        loginForm.setLogin(null);
        loginForm.setEmail(testUser.getEmail());
        errorCode = userServiceDAO.login(loginForm);

        assertEquals(null, loginForm.getLogin());
        assertEquals(testUser.getEmail(), loginForm.getEmail());
        assertEquals(testUser.getPassword(), loginForm.getPassword());
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

    @Test
    public void loginIncorrectValues() {
        userServiceDAO.signUp(testUser);
        final LoginForm loginForm = new LoginForm(null, "alexxx228@mail.com", testUser.getPassword());
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.login(loginForm);
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INVALID_AUTH_DATA);

        loginForm.setEmail(null);
        loginForm.setLogin("alexxx");
        errorCode = userServiceDAO.login(loginForm);
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INVALID_AUTH_DATA);

        loginForm.setEmail(testUser.getEmail());
        loginForm.setPassword("paramparampam");
        errorCode = userServiceDAO.login(loginForm);
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INCORRECT_PASSWORD);
    }

    @Test
    public void getUserInfoCorrect() {
        userServiceDAO.signUp(testUser);
        final UserInfoForm userInfoForm = new UserInfoForm();
        final UserServiceDAO.ErrorCodes errorCode = userServiceDAO.getUserInfo(testUser.getLogin(), userInfoForm);

        assertEquals(testUser.getLogin(), userInfoForm.getLogin());
        assertEquals(testUser.getEmail(), userInfoForm.getEmail());
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

    @Test
    public void getUserInfoInvalidLogin() {
        final UserInfoForm userInfoForm = new UserInfoForm();
        final UserServiceDAO.ErrorCodes errorCode = userServiceDAO.getUserInfo(testUser.getLogin(), userInfoForm);

        assertEquals(null, userInfoForm.getLogin());
        assertEquals(null, userInfoForm.getEmail());
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INVALID_LOGIN);
    }

    @Test
    public void changeEmailCorrect() {
        userServiceDAO.signUp(testUser);
        final String mail = "darkstalker98@mail.ru";
        final MailForm mailForm = new MailForm(mail);
        final UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changeEmail(testUser.getLogin(), mailForm);

        final UserInfoForm infoForm = new UserInfoForm();
        userServiceDAO.getUserInfo(testUser.getLogin(), infoForm);

        assertEquals(mail, infoForm.getEmail());
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

    @Test
    public void changeEmailIncorrect() {
        userServiceDAO.signUp(testUser);

        final MailForm mailForm = new MailForm(null);
        final UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changeEmail(testUser.getLogin(), mailForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INVALID_LOGIN);
    }

    @Test
    public void changeDuplicateEmail() {
        final String mail = "hopper@gmail.com";
        final User testUser2 = new User(mail, "test2", "test2", 228);
        userServiceDAO.signUp(testUser);
        userServiceDAO.signUp(testUser2);

        final MailForm mailForm = new MailForm(mail);
        final UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changeEmail(testUser.getLogin(), mailForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.EMAIL_OCCUPIED);
    }

    @Test
    public void changePassCorrect() {
        userServiceDAO.signUp(testUser);
        final String pass = "password";
        final PassForm passForm = new PassForm(testUser.getPassword(), pass);
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changePass(testUser.getLogin(), passForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);

        final LoginForm loginForm = new LoginForm(testUser.getLogin(), null, pass);
        errorCode = userServiceDAO.login(loginForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

    @Test
    public void changePassIncorrect() {
        userServiceDAO.signUp(testUser);

        PassForm passForm = new PassForm(testUser.getPassword(), null);
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changePass(testUser.getLogin(), passForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INCORRECT_PASSWORD);

        final String pass = testUser.getPassword();

        passForm = new PassForm("tirpirdir", pass);
        errorCode = userServiceDAO.changePass(testUser.getLogin(), passForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INCORRECT_PASSWORD);
    }

    @Test
    public void getAllScoreBoard() {
        final User testUser1 = new User("test1@mail.ru", "test1", "test1", 228);
        final User testUser2 = new User("test2@mail.ru", "test2", "test2", 229);

        userServiceDAO.signUp(testUser);
        userServiceDAO.signUp(testUser1);
        userServiceDAO.signUp(testUser2);

        final List<ScoreView> result = new ArrayList<>();
        result.add(new ScoreView(testUser.getLogin(), testUser.getScore()));
        result.add(new ScoreView(testUser2.getLogin(), testUser2.getScore()));
        result.add(new ScoreView(testUser1.getLogin(), testUser1.getScore()));

        final UserServiceDAO.ErrorCodes errorCode = userServiceDAO.getAllScoreBoard(result);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

    @Test
    public void getPartScoreBoard() {
        final User testUser1 = new User("test1@mail.ru", "test1", "test1", 228);
        final User testUser2 = new User("test2@mail.ru", "test2", "test2", 229);
        final User testUser3 = new User("test3@mail.ru", "test3", "test3", 590);

        userServiceDAO.signUp(testUser);
        userServiceDAO.signUp(testUser1);
        userServiceDAO.signUp(testUser2);
        userServiceDAO.signUp(testUser3);

        final List<ScoreView> result = new ArrayList<>();
        result.add(new ScoreView(testUser.getLogin(), testUser.getScore()));
        result.add(new ScoreView(testUser2.getLogin(), testUser3.getScore()));
        result.add(new ScoreView(testUser2.getLogin(), testUser2.getScore()));
        result.add(new ScoreView(testUser1.getLogin(), testUser1.getScore()));

        final Integer pos = 1;
        final Integer count = 2;

        final ScoreRequest scoreRequest = new ScoreRequest(pos, count);

        final UserServiceDAO.ErrorCodes errorCode = userServiceDAO.getPartScoreBoard(scoreRequest, result);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }
    
}
