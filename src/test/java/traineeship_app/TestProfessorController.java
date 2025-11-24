package traineeship_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import traineeship_app.domainmodel.*;
import traineeship_app.services.*;


@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureMockMvc
class TestProfessorController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CommitteeService committeeService;

    private Professor sampleProfessor;
    private TraineeshipPosition samplePosition;

    @BeforeEach
    void setup() {
        sampleProfessor = new Professor();
        sampleProfessor.setUsername("prof123");
        sampleProfessor.setProfessorName("Prof. John Doe");
        sampleProfessor.setInterests("AI, Data Science");

        samplePosition = new TraineeshipPosition();
        samplePosition.setId(1);
        samplePosition.setSupervisor(sampleProfessor);
    }

    @Test
    @WithMockUser(username = "prof123", roles = {"PROFESSOR"})
    void testProfessorHome() throws Exception {
        mockMvc.perform(get("/professors/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("professors/home"))
                .andExpect(model().attributeExists("username", "role"));
    }

    // need to test the other methods too
}

