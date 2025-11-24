package traineeship_app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traineeship_app.domainmodel.Student;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.StudentMapper;
import traineeship_app.mappers.TraineeshipPositionsMapper;
import traineeship_app.mappers.UserMapper;
import traineeship_app.services.StudentServiceImpl;


@ExtendWith(MockitoExtension.class)
public class TestStudentService {

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TraineeshipPositionsMapper traineeshipPositionsMapper;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void testSaveProfile() {
        Student student = new Student();
        student.setUsername("testuser");

        when(userMapper.existsByUsername(any())).thenReturn(false);
        when(studentMapper.existsStudentByAM(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        studentService.SaveProfile(student);

        verify(studentMapper, times(1)).save(any(Student.class));
    }

    @Test
    void testUpdateProfile() {
        Student student = new Student();
        student.setUsername("testuser");

        when(studentMapper.findByUsername("testuser")).thenReturn(new Student());

        studentService.UpdateProfile(student);

        verify(studentMapper, times(1)).save(any(Student.class));
    }

    @Test
    void testRetrieveProfile() {
        Student student = new Student();
        student.setUsername("testuser");

        when(studentMapper.findByUsername("testuser")).thenReturn(student);

        Student result = studentService.retrieveProfile("testuser");

        verify(studentMapper, times(1)).findByUsername("testuser");
        assert(result != null && result.getUsername().equals("testuser"));
    }

    @Test
    void testSaveLogBook() {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setStudent(new Student());
        position.setStudentLogbook("log");

        when(traineeshipPositionsMapper.findByStudentUsernameAndIsCompletedFalse(any()))
                .thenReturn(java.util.Optional.of(position));

        studentService.saveLogBook(position, "testuser");

        verify(traineeshipPositionsMapper, times(1)).save(any(TraineeshipPosition.class));
    }
}
