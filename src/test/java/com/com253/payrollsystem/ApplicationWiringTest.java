package com.com253.payrollsystem;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Boots the full context against in-memory SQLite — guards the feature-package
 * wiring (every bean still resolves) and the auth slice end-to-end.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApplicationWiringTest {

    @Autowired MockMvc mvc;

    @Test
    void seededAdminCanLogIn() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void protectedEndpointRejectsAnonymous() throws Exception {
        mvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void kioskEndpointIsPublic() throws Exception {
        // Unknown id -> handled by the service (not a 401), proving the route is permitted.
        mvc.perform(post("/api/kiosk/clock")
                        .contentType("application/json")
                        .content("{\"employeeId\":\"0000-0000-00\"}"))
                .andExpect(status().is(Matchers.not(401)));
    }
}
