package main.controllers;

import main.models.User;
import main.services.UserServiceDAO;
import main.views.ResponseMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)

public class AuthenticateControllerTest {

    @MockBean
    private UserServiceDAO userServiceDAO;

    @Autowired
    private TestRestTemplate restTemplate;

    private final User testUser = new User("e.mail@mail.ru", "test", "test", 1337);

    @Test
    public void signUpSuccess() {
        final HttpEntity<User> httpEntity = new HttpEntity<>(testUser);
        final ResponseEntity<ResponseMsg> responseEntity = restTemplate.exchange("/api/user/signup",
                HttpMethod.POST, httpEntity, ResponseMsg.class);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(ResponseMsg.CREATED, responseEntity.getBody());
    }

    @Test
    public void signUpUserExist() {
        final User testUser2 = new User("hopper@gmail.com", "ChiefHopper", "password", 1);
        doThrow(new DuplicateKeyException("")).when(userServiceDAO).signUp(eq(testUser2));
        final HttpEntity<User> httpEntity = new HttpEntity<>(testUser2);
        final ResponseEntity<ResponseMsg> responseEntity = restTemplate.exchange("/api/user/signup",
                HttpMethod.POST, httpEntity, ResponseMsg.class);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getBody());
        verify(userServiceDAO).signUp(eq(testUser2));
    }
}
