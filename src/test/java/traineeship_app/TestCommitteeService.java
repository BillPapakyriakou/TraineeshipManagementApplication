package traineeship_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import traineeship_app.domainmodel.*;
import traineeship_app.mappers.StudentMapper;
import traineeship_app.mappers.TraineeshipPositionsMapper;
import traineeship_app.mappers.ProfessorMapper;
import traineeship_app.mappers.*;
import traineeship_app.services.CommitteeServiceImpl;
import traineeship_app.services.assign.SupervisorAssignmentFactory;
import traineeship_app.services.assign.SupervisorAssignmentStrategy;
import traineeship_app.services.search.PositionsSearchFactory;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TestCommitteeService {

    @InjectMocks
    private CommitteeServiceImpl committeeService;

    @Mock
    private StudentMapper studentMapper;

    @Mock(lenient = true)
    private TraineeshipPositionsMapper positionMapper;


    @Mock
    private ProfessorMapper professorMapper;

    @Mock
    private SupervisorAssignmentFactory assignmentFactory;


    @Mock
    private CommitteeMapper committeeMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PositionsSearchFactory positionsSearchFactory;



    @BeforeEach
    void setup() {
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

    }

    @Test
    void testSaveCommitteeProfile() {
        Committee committee = new Committee();
        committee.setUsername("committee123");
        committee.setCommitteeMemberName("John Doe");
        committee.setPassword("rawPassword");

        when(userMapper.existsByUsername(anyString())).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        committeeService.saveProfile(committee);


        ArgumentCaptor<Committee> captor = ArgumentCaptor.forClass(Committee.class);
        verify(committeeMapper).save(captor.capture());

        Committee saved = captor.getValue();
        assertEquals("committee123", saved.getUsername());
        assertEquals("John Doe", saved.getCommitteeMemberName());
        assertEquals("encodedPassword", saved.getPassword());
    }

    @Test
    void testGetAllStudentsLookingForTraineeship() {
        Student s1 = new Student();
        s1.setUsername("student1");
        Student s2 = new Student();
        s2.setUsername("student2");
        List<Student> students = List.of(s1, s2);

        when(studentMapper.findByLookingForTraineeshipTrue()).thenReturn(students);
        when(studentMapper.findByLookingForTraineeshipTrue()).thenReturn(List.of(new Student(), new Student()));


        List<Student> result = committeeService.getAllStudentsLookingForTraineeship();

        assertEquals(2, result.size());
        assertFalse(result.isEmpty(), "Expected non-empty result list");

        verify(studentMapper, times(1)).findByLookingForTraineeshipTrue();
    }


    @Test
    void testAssignPosition() {
        String username = "student123";
        int positionId = 42;

        Student student = new Student();
        student.setUsername(username);

        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);
        position.setTitle("Java Intern");

        when(studentMapper.findByUsername(username)).thenReturn(student);
        when(positionMapper.findById(Long.valueOf(positionId))).thenReturn(Optional.of(position));

        when(studentMapper.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(positionMapper.save(any(TraineeshipPosition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        committeeService.assignPosition(positionId, username);

        assertEquals(position, student.getAssignedTraineeship());
        assertTrue(position.isAssigned());

        verify(studentMapper).save(student);
        verify(positionMapper).save(position);
    }


    @Test
    void testGetStudentById() {
        long id = 1L;
        Student student = new Student();
        student.setId(1L);
        when(studentMapper.findById(1L)).thenReturn(Optional.of(student));

        Student result = committeeService.getStudentById(id);
        assertNotNull(result, "Student should not be null");
        assertEquals(id, result.getId());
    }

    @Test
    void testGetCandidateProfessorsReturnsNonEmptyList() {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setTitle("Backend Intern");

        Professor p1 = new Professor();
        p1.setProfessorName("Dr. Smith");
        Professor p2 = new Professor();
        p2.setProfessorName("Dr. Brown");

        List<Professor> professors = List.of(p1, p2);

        var assignmentStrategyMock = mock(SupervisorAssignmentStrategy.class);
        when(assignmentStrategyMock.assign(any(TraineeshipPosition.class))).thenReturn(professors);
        when(assignmentFactory.create("load")).thenReturn(assignmentStrategyMock);

        List<Professor> result = committeeService.getCandidateProfessors("load", position);

        assertEquals(2, result.size());
        assertEquals("Dr. Smith", result.get(0).getProfessorName());
        assertEquals("Dr. Brown", result.get(1).getProfessorName());
    }


    // need to test the other methods too
}

