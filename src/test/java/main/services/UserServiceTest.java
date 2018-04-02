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
    public void signUpSuccess() throws Exception {
        userServiceDAO.signUp(testUser);
    }

    @Test(expected = DuplicateKeyException.class)
    public void signUpDuplicateEmail() throws Exception {
        userServiceDAO.signUp(testUser);
        userServiceDAO.signUp(new User(testUser.getEmail(), "test2", "test", 1337));
    }

    @Test(expected = DuplicateKeyException.class)
    public void signUpDuplicateLogin() throws Exception {
        userServiceDAO.signUp(testUser);
        userServiceDAO.signUp(new User("mail@mail.ru", testUser.getLogin(), "test", 1337));
    }

    @Test
    public void loginCorrect() {
        userServiceDAO.signUp(testUser);
        LoginForm loginForm = new LoginForm(testUser.getLogin(), null, testUser.getPassword());
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
        LoginForm loginForm = new LoginForm(null, "alexxx228@mail.com", testUser.getPassword());
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
        UserInfoForm userInfoForm = new UserInfoForm();
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.getUserInfo(testUser.getLogin(), userInfoForm);

        assertEquals(testUser.getLogin(), userInfoForm.getLogin());
        assertEquals(testUser.getEmail(), userInfoForm.getEmail());
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

    @Test
    public void getUserInfoInvalidLogin() {
        UserInfoForm userInfoForm = new UserInfoForm();
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.getUserInfo(testUser.getLogin(), userInfoForm);

        assertEquals(null, userInfoForm.getLogin());
        assertEquals(null, userInfoForm.getEmail());
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INVALID_LOGIN);
    }

    @Test
    public void changeEmailCorrect() {
        userServiceDAO.signUp(testUser);
        String mail = "darkstalker98@mail.ru";
        MailForm mailForm = new MailForm(mail);
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changeEmail(testUser.getLogin(), mailForm);

        UserInfoForm infoForm = new UserInfoForm();
        userServiceDAO.getUserInfo(testUser.getLogin(), infoForm);

        assertEquals(mail, infoForm.getEmail());
        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

    @Test
    public void changeEmailIncorrect() {
        userServiceDAO.signUp(testUser);
        String mail = null;
        MailForm mailForm = new MailForm(mail);
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changeEmail(testUser.getLogin(), mailForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INVALID_LOGIN);
    }

    @Test
    public void changeDuplicateEmail() {
        String mail = "hopper@gmail.com";
        final User testUser2 = new User(mail, "test2", "test2", 228);
        userServiceDAO.signUp(testUser);
        userServiceDAO.signUp(testUser2);

        MailForm mailForm = new MailForm(mail);
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changeEmail(testUser.getLogin(), mailForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.EMAIL_OCCUPIED);
    }

    @Test
    public void changePassCorrect() {
        userServiceDAO.signUp(testUser);
        String pass = "password";
        PassForm passForm = new PassForm(testUser.getPassword(), pass);
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changePass(testUser.getLogin(), passForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);

        LoginForm loginForm = new LoginForm(testUser.getLogin(), null, pass);
        errorCode = userServiceDAO.login(loginForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

    @Test
    public void changePassIncorrect() {
        userServiceDAO.signUp(testUser);
        String pass = null;
        PassForm passForm = new PassForm(testUser.getPassword(), pass);
        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.changePass(testUser.getLogin(), passForm);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.INCORRECT_PASSWORD);

        pass = testUser.getPassword();

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

        List<ScoreView> result = new ArrayList<>();
        result.add(new ScoreView(testUser.getLogin(), testUser.getScore()));
        result.add(new ScoreView(testUser2.getLogin(), testUser2.getScore()));
        result.add(new ScoreView(testUser1.getLogin(), testUser1.getScore()));

        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.getAllScoreBoard(result);

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

        List<ScoreView> result = new ArrayList<>();
        result.add(new ScoreView(testUser.getLogin(), testUser.getScore()));
        result.add(new ScoreView(testUser2.getLogin(), testUser3.getScore()));
        result.add(new ScoreView(testUser2.getLogin(), testUser2.getScore()));
        result.add(new ScoreView(testUser1.getLogin(), testUser1.getScore()));

        final Integer pos = 1;
        final Integer count = 2;

        ScoreRequest scoreRequest = new ScoreRequest(pos, count);

        UserServiceDAO.ErrorCodes errorCode = userServiceDAO.getPartScoreBoard(scoreRequest, result);

        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
    }

}
