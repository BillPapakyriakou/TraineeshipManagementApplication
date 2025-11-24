package traineeship_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import traineeship_app.services.CompanyService;
import traineeship_app.mappers.EvaluationMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureMockMvc
class TestCompanyController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Test
    @WithMockUser(username = "companyUser", roles = {"COMPANY"})
    void testCompanyHome() throws Exception {
        mockMvc.perform(get("/companies/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("companies/home"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("role"));
    }



    @Test
    @WithMockUser(username = "companyUser", roles = {"COMPANY"})
    void testAnnounceTraineeshipForm() throws Exception {
        mockMvc.perform(get("/companies/traineeship-management/announce-traineeship")
                        .param("username", "companyUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("companies/announce-traineeship"))
                .andExpect(model().attributeExists("traineeship"))
                .andExpect(model().attribute("username", "companyUser"));
    }

    // need to test the other methods too
}

