package traineeship_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import traineeship_app.domainmodel.Student;
import traineeship_app.controllers.StudentController;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.StudentMapper;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureMockMvc
class TestStudentController {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentController studentController;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private Student sampleStudent;

    @Mock
    private TraineeshipPosition samplePosition;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        sampleStudent = new Student();
        sampleStudent.setUsername("alice123");
        sampleStudent.setStudentName("Alice Example");
        sampleStudent.setAM("123456");
        sampleStudent.setAvgGrade(8.5);
        sampleStudent.setPreferredLocation("Athens");
        sampleStudent.setInterests("Java, Spring");
        sampleStudent.setSkills("Java, SQL");
        sampleStudent.setLookingForTraineeship(true);
        sampleStudent.setAssignedTraineeship(null);

        samplePosition = new TraineeshipPosition();
        samplePosition.setId(1);
        samplePosition.setStudentLogbook("Initial logbook entry");
    }


    @Test
    void testControllerIsNotNull() {
        Assertions.assertNotNull(studentController);
    }

    @Test
    void testMockMvcIsNotNull() {
        Assertions.assertNotNull(mockMvc);
    }
    // need to test the other methods too
}
