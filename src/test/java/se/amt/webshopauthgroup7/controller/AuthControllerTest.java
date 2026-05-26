package se.amt.webshopauthgroup7.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.amt.webshopauthgroup7.repository.AppUserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    String registerBody;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();

        registerBody = """
                {
                  "username": "seaotter@example.com",
                  "password": "stenarochmusslor"
                }
                """;
    }

    @Test
    void registerUser() throws Exception {
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON) //skickas som json
                                .content(registerBody)) //skickar user+pwd till endpoint
                .andExpect(status().isCreated());//förväntar return status 201
    }

    @Test
    void getJwks() throws Exception {
        mockMvc.perform(get("/auth/jwks"))
                .andExpect(status().isOk()); //testar att auth/jwks fungerar
    }

    @Test
    void loginTest() throws Exception {
        //registrerar
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerBody))
                .andExpect(status().isCreated());

        //loggar in
        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerBody))
                .andExpect(status().isOk());
    }

    @Test
    void registerDupeUsers() throws Exception {
        //registrerar en user
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerBody))
                .andExpect(status().isCreated());

        //testar för att se om samma user ger felmeddelande
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void wrongPasswordTest() throws Exception {
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerBody))
                .andExpect(status().isCreated());

        String wrongPwd = """
                { "username": "seaotter@example.com",
                "password": "härvardetfellösen"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(wrongPwd))
                .andExpect(status().isForbidden());
    }
}
