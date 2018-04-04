package main.controllers;

import main.Main;
import com.github.javafaker.Faker;
import main.views.ResponseMsg;
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
import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public static void setUpFaker() {
        faker = new Faker(new Locale("en-US"));
    }

    @BeforeAll
    public static void setUpValues() {
        email = faker.internet().emailAddress();
        login = faker.name().username();
        password = faker.internet().password();
    }

    public void createUserOk() throws Exception {
        mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"score\":\"" + 0 + "\"," +
                                "\"password\":\"" + password + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.CREATED.getMsg()));
    }

    @Test
    public void signUpOk() throws Exception {
        final MvcResult result = mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"score\":\"" + null + "\"," +
                                "\"password\":\"" + password + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated()).andReturn();
        final MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        System.out.println("name = " + login + "userlogin = " + session.getAttribute("userLogin"));
        System.out.println(result.getRequest().getAttribute ("userLogin"));
    }

    @Test
    public void createUserConflict() throws Exception {
        createUserOk();
        final MvcResult result =  this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"password\":\"" + password + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isConflict()).andReturn();

        final MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        System.out.println(result.getResponse().getContentAsString());
        System.out.println("name = " + login + "userlogin = " + session.getAttribute("userLogin"));
        System.out.println(result.getResponse().getCookie("userLogin"));
    }

    @Test
    public void createUserMailConflict() throws Exception {
        createUserOk();
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + faker.name().username() + "\"," +
                                "\"password\":\"" + faker.internet().password() + "\"}"))
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
                        .content("{\"email\":\"" + faker.internet().emailAddress() + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"password\":\"" + faker.internet().password() + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.CONFLICT.getMsg()));
    }

    @Test
    public void createNullMail() throws  Exception {
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content("{\"email\":" + null + ',' +
                                "\"login\":\"" + faker.name().username() + "\"," +
                                "\"password\":\"" + faker.internet().password() + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createNullLogin() throws  Exception {
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"" + faker.internet().emailAddress() + "\"," +
                                "\"login\":" + null + ',' +
                                "\"password\":\"" + faker.internet().password() + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNullPassword() throws  Exception {
        this.mockMvc.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"" + faker.internet().emailAddress() + "\"," +
                                "\"login\":\"" + faker.name().username() + "\"," +
                                "\"password\":" + null + '}'))
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
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"password\":\"" + password + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()));
    }

    @Test
    public void loginCheckCookies() throws Exception {
        createUserOk();
        final MvcResult result = this.mockMvc.perform(
               post("/api/user/login")
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"password\":\"" + password + "\"}"))
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
                        .content("{\"email\":" + null + ',' +
                                "\"login\":\"" + login + "\"," +
                                "\"password\":\"" + password + "\"}"))
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
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":" + null + ',' +
                                "\"password\":\"" + password + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()));
    }

    @Test
    public void loginIncorrectUser() throws Exception {
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content("{\"email\":" + null + ',' +
                                "\"login\":" + null + ',' +
                                "\"password\":\"" + password + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.BAD_REQUEST.getMsg()));
    }

    @Test
    public void loginNullPasswors() throws Exception {
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"password\":" + null + '}'))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.BAD_REQUEST.getMsg()));
    }

    @Test
    public void loginNotExistingUser() throws Exception {
        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content("{\"email\":\"" + faker.internet().emailAddress() + "\"," +
                                "\"login\":\"" + faker.name().username() + "\"," +
                                "\"password\":\"" + faker.internet().password() + "\"}"))
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
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"password\":\"" + faker.internet().password() + "\"}"))
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
