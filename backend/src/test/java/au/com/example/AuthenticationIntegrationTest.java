package au.com.example;

import au.com.example.controller.AuthenticationController;
import au.com.example.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLoginWithValidCredentials() throws Exception {
        String credentials = Base64.getEncoder().encodeToString("user@example.com:password".getBytes());

        mockMvc.perform(post("/api/authentication/login")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtTokenProvider.TOKEN_HEADER))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        String credentials = Base64.getEncoder().encodeToString("user@example.com:wrongpassword".getBytes());

        mockMvc.perform(post("/api/authentication/login")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAccessProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAccessProtectedEndpointWithToken() throws Exception {
        // First login to get a token
        String credentials = Base64.getEncoder().encodeToString("user@example.com:password".getBytes());

        String token = mockMvc.perform(post("/api/authentication/login")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader(JwtTokenProvider.TOKEN_HEADER);

        // Use the token to access protected endpoint
        mockMvc.perform(get("/api/user")
                        .header(JwtTokenProvider.TOKEN_HEADER, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }
}
