package main.controllers;


import com.github.javafaker.Faker;
import main.Main;
import main.views.ResponseMsg;
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

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class UserUpdateControllerTest {
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
    public void changeEmailOk() throws Exception {
        createUserOk();
        mockMvc.perform(
                put("/api/user/changeEmail")
                        .contentType("application/json")
                        .content("{\"userMail\":\"" + faker.internet().emailAddress() + "\"}")
                        .sessionAttr("userLogin", login))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()));

    }


    @Test
    public void changeEmailUnauthorized() throws Exception {
        mockMvc.perform(
                put("/api/user/changeEmail")
                        .contentType("application/json")
                        .content("{\"userMail\":\"" + faker.internet().emailAddress() + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.NOT_LOGGED_IN.getMsg()));
    }

    /*
    @Test
    public void changeEmailConflict() throws Exception {
        createUserOk();
        final String existEmail = email;
        setUpValues();
        createUserOk();
        mockMvc.perform(
                put("/api/user/changeEmail")
                        .contentType("application/json")
                        .content("{\"userMail\":\"" + existEmail + "\"}")
                        .sessionAttr("userLogin", login))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.CONFLICT.getMsg()));
    }
    */
    @Test
    public void changetEmailNullEmail() throws Exception {
        mockMvc.perform(
                put("/api/user/changeEmail")
                        .contentType("application/json")
                        .content("{\"userMail\":" + null + '}')
                        .sessionAttr("userLogin", login))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.BAD_REQUEST.getMsg()));
    }

    /* ----------------------------------- changePassword --------------------------- */

    @Test
    public void changePasswordOk() throws Exception {
        createUserOk();
        final String newPassword = faker.internet().password();
        mockMvc.perform(
                put("/api/user/changePass")
                        .contentType("application/json")
                        .content("{\"oldPassword\":\"" + password + "\"," +
                                "\"newPassword\":\"" + newPassword + "\"}")
                        .sessionAttr("userLogin", login))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());

        this.mockMvc.perform(
                post("/api/user/login")
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\"," +
                                "\"login\":\"" + login + "\"," +
                                "\"password\":\"" + newPassword + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.OK.getMsg()));
    }

    @Test
    public void changePasswordIncorrectOldPassword() throws Exception {
        createUserOk();
        mockMvc.perform(
                put("/api/user/changePass")
                        .contentType("application/json")
                        .content("{\"oldPassword\":\"" + faker.internet().password() + "\"," +
                                "\"newPassword\":\"" + faker.internet().password() + "\"}")
                        .sessionAttr("userLogin", login))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.INCORRECT_PASSWORD.getMsg()));
    }

    @Test
    public void changePasswordNullPassword() throws Exception {
        mockMvc.perform(
                put("/api/user/changePass")
                        .contentType("application/json")
                        .content("{\"oldPassword\":" + null + ',' +
                                "\"newPassword\":" + null + '}')
                        .sessionAttr("userLogin", login))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.INCORRECT_PASSWORD.getMsg()));
    }

    @Test
    public void changePasswordUnauthorized() throws Exception {
        mockMvc.perform(
                put("/api/user/changePass")
                        .contentType("application/json")
                        .content("{\"oldPassword\":\"" + faker.internet().password() + "\"," +
                                "\"newPassword\":\"" + faker.internet().password() + "\"}"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.NOT_LOGGED_IN.getMsg()));
    }

    @Test
    public void changePasswordNotExistUser() throws Exception {
        mockMvc.perform(
                put("/api/user/changePass")
                        .contentType("application/json")
                        .content("{\"oldPassword\":\"" + faker.internet().password() + "\"," +
                                "\"newPassword\":\"" + faker.internet().password() + "\"}")
                        .sessionAttr("userLogin", faker.name().username()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ResponseMsg.INVALID_LOGIN.getMsg()));
    }

}
