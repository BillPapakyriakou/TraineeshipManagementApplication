package traineeship_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import traineeship_app.domainmodel.*;
import traineeship_app.services.ProfessorService;
import traineeship_app.services.CommitteeService;
import traineeship_app.services.CompanyService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureMockMvc
class TestCommitteeController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommitteeService committeeService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ProfessorService professorService;

    private Committee committee;

    @BeforeEach
    void setUp() {
        committee = new Committee();
        committee.setCommitteeMemberName("John Doe");
        committee.setUsername("committeeUser");
    }

    @Test
    @WithMockUser(username = "committeeUser", roles = {"COMMITTEE"})
    void testCommiteeHome() throws Exception {
        mockMvc.perform(get("/committees/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("committees/home"))
                .andExpect(model().attribute("username", "committeeUser"))
                .andExpect(model().attribute("role", "ROLE_COMMITTEE"));
    }


    @Test
    void testRegisterCommittee_WithErrors() throws Exception {

        mockMvc.perform(post("/committees/register")
                        .param("committeeMemberName", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register?role=COMMITTEE_MEMBER"));
    }

    // need to test the other methods too
}

