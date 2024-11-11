package com.ninjaone.dundie_awards.medium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class IndexControllerMediumTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("GET / - Success")
    void testGetIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())                 // Check that HTTP status is 200 OK
                .andExpect(view().name("index"))            // Verify the view name returned is "index"
                .andExpect(model().attributeExists("employees")) // Check if model has an attribute populated by IndexService
                .andExpect(model().attributeExists("queueMessages")) // Check if model has an attribute populated by IndexService
                .andExpect(model().attributeExists("totalDundieAwards")) // Check if model has an attribute populated by IndexService
                .andExpect(model().attributeExists("activities")); // Check if model has an attribute populated by IndexService
    }
}
