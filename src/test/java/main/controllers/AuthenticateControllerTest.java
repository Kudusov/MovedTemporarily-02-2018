package main.controllers;

import main.Main;
import com.github.javafaker.Faker;
import main.services.UserServiceDAO;
import main.views.LoginForm;
import main.views.ResponseMsg;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class AuthenticateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static Faker faker;
    private static String email;
    private static String login;
    private static String password;


    @BeforeAll
    public static void setUpValues() {
        faker = new Faker(new Locale("en-US"));
        email = faker.internet().emailAddress();
        login = faker.name().username();
        password = faker.internet().password();
    }

    public void createUserOk() throws Exception {
        mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "login", login, "score", 0, "password", password)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.CREATED.getMsg()));
    }

//    @Test
//    public void someMockTest() throws Exception {
//        UserServiceDAO m = mock(UserServiceDAO.class);
//        LoginForm l = new LoginForm("qwertyqwerty", "qwerty@mail.ru", "somepass");
//        LoginForm l2 = new LoginForm("qwertyqwerty", "qwerty@mail.ru", "somepass");
//
//        when(m.getUserByIdOrEmailDB(l)).thenReturn(l2);
//        when(m.login(l)).thenCallRealMethod();
//        final UserServiceDAO.ErrorCodes errorCode = m.login(l);
//        System.out.println(l.getLogin() + l.getPassword());
//        assertEquals(errorCode, UserServiceDAO.ErrorCodes.OK);
//    }

    @Test
    public void signUpOk() throws Exception {
        mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "login", login, "score", 0, "password", password)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated());

    }

    @Test
    public void createUserConflict() throws Exception {
        createUserOk();
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "login", login, "score", 0, "password", password)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isConflict());
    }

    @Test
    public void createUserMailConflict() throws Exception {
        createUserOk();
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "login",
                                faker.name().username(), "score", 0, "password", faker.internet().password())).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.CONFLICT.getMsg()));
    }

    @Test
    public void createUserLoginConflict() throws Exception {
        createUserOk();
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", faker.internet().emailAddress(), "login", login,
                                "score", 0, "password", faker.internet().password())).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.CONFLICT.getMsg()));
    }

    @Test
    public void createNullMail() throws  Exception {
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("login", faker.name().username(),
                                "score", 0, "password", faker.internet().password())).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createNullLogin() throws  Exception {
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", faker.internet().emailAddress(),
                                "score", 0, "password", faker.internet().password())).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullPassword() throws  Exception {
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", faker.internet().emailAddress(), "login", faker.internet().password(),
                                "score", 0)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }


    /*----------------------------Login tests-------------------------------------------*/
    @Test
    public void loginUserOk() throws Exception {
        createUserOk();
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "login", login, "password", password)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()));
    }

    @Test
    public void loginCheckCookies() throws Exception {
        createUserOk();
        final MvcResult result = this.mockMvc.perform(
               post("/api/user/login")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "login", login, "password", password)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()))
                .andReturn();
        final MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        assertEquals(login, session.getAttribute("userLogin"));
    }

    @Test
    public void loginByUserMail() throws Exception {
        createUserOk();
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "password", password)).toString()))

                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()));
    }


    @Test
    public void loginByUserLogin() throws Exception {
        createUserOk();
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("login", login, "password", password)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()));
    }


    @Test
    public void loginNullPassword() throws Exception {
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "login", login)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.BAD_REQUEST.getMsg()));
    }

    @Test
    public void loginNotExistingUser() throws Exception {
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", faker.internet().emailAddress(), "login", faker.name().username(), "password", faker.internet().password())).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.INVALID_LOGIN.getMsg()));
    }

    @Test
    public void loginIncorrectPassword() throws Exception {
        createUserOk();
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content(new JSONObject(Map.of("email", email, "login", login, "password", faker.internet().password())).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.INCORRECT_PASSWORD.getMsg()));
    }

    /* ---------------------------------- Test logout ----------------------------------------*/

    @Test
    public void logoutOk() throws Exception {
        this.mockMvc.perform(
                delete("/api/user/logout")
                        .contentType("application/json")
                        .sessionAttr("userLogin", login))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()));

    }

    @Test
    public void logoutUnauthorized() throws  Exception {
        this.mockMvc.perform(
                delete("/api/user/logout")
                        .contentType("application/json"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.NOT_LOGGED_IN.getMsg()));

    }

}
