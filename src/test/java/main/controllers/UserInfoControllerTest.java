package main.controllers;

import com.github.javafaker.Faker;
import main.Main;
import main.views.ResponseMsg;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class UserInfoControllerTest {
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
                        .content(new JSONObject(Map.of("email", email, "login", login,
                                "score", 0, "password", password)).toString()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.CREATED.getMsg()));
    }

    @Test
    void getInfoOk() throws Exception {
        createUserOk();
        mockMvc.perform(
                get("/api/user/info")
                    .sessionAttr("userLogin", login))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void getInfoUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/user/info"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.NOT_LOGGED_IN.getMsg()));
    }

    @Test
    void getInfoNotExistUser() throws Exception {
        mockMvc.perform(
                get("/api/user/info")
                        .sessionAttr("userLogin", faker.name().username()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.INVALID_LOGIN.getMsg()));
    }
}
