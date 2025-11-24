package traineeship_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import traineeship_app.domainmodel.Student;
import traineeship_app.mappers.StudentMapper;
import java.util.List;


@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class TestStudentMapper {

    @Autowired
    private StudentMapper studentMapper;

    private Student baseStudent;

    @BeforeEach
    void setup() {
        baseStudent = new Student();
        baseStudent.setUsername("defaultUser");        // from User superclass
        baseStudent.setPassword("defaultPassword");    // from User superclass, needed for DB constraints
        baseStudent.setStudentName("Default Student");
        baseStudent.setAM("AM000");
        baseStudent.setAvgGrade(0.0);
        baseStudent.setPreferredLocation("Default Location");
        baseStudent.setInterests("Default Interests");
        baseStudent.setSkills("Default Skills");
        baseStudent.setLookingForTraineeship(true);
        baseStudent.setAssignedTraineeship(null);
    }

    @Test
    void testSaveAndFindByUsername() {
        Student student = new Student();
        student.setUsername("alice123");
        student.setPassword("alicePassword");
        student.setStudentName("Alice Wonderland");
        student.setAM("AM001");
        student.setAvgGrade(9.5);
        student.setPreferredLocation("Wonderland");
        student.setInterests("Adventures");
        student.setSkills("Curiosity");
        student.setLookingForTraineeship(true);
        student.setAssignedTraineeship(null);

        Student savedStudent = studentMapper.save(student);
        Assertions.assertNotNull(savedStudent.getId());

        Student foundStudent = studentMapper.findByUsername("alice123");
        Assertions.assertNotNull(foundStudent);
        Assertions.assertEquals("Alice Wonderland", foundStudent.getStudentName());
    }

    @Test
    void testExistsStudentByAM() {
        Student student = new Student();
        student.setUsername("bob456");
        student.setPassword("bobPassword");
        student.setStudentName("Bob Builder");
        student.setAM("AM002");
        student.setAvgGrade(8.2);
        student.setPreferredLocation("Builder City");
        student.setInterests("Building");
        student.setSkills("Construction");
        student.setLookingForTraineeship(false);
        student.setAssignedTraineeship(null);

        studentMapper.save(student);

        boolean exists = studentMapper.existsStudentByAM("AM002");
        Assertions.assertTrue(exists);

        boolean notExists = studentMapper.existsStudentByAM("NON_EXISTING_AM");
        Assertions.assertFalse(notExists);
    }

    @Test
    void testFindByLookingForTraineeshipTrue() {
        Student s1 = new Student();
        s1.setUsername("charlie");
        s1.setPassword("charliePassword");
        s1.setStudentName("Charlie Chocolate");
        s1.setAM("AM003");
        s1.setAvgGrade(7.8);
        s1.setPreferredLocation("Chocolate Factory");
        s1.setInterests("Chocolate");
        s1.setSkills("Candy Making");
        s1.setLookingForTraineeship(true);
        s1.setAssignedTraineeship(null);

        Student s2 = new Student();
        s2.setUsername("diana");
        s2.setPassword("dianaPassword");
        s2.setStudentName("Diana Prince");
        s2.setAM("AM004");
        s2.setAvgGrade(9.0);
        s2.setPreferredLocation("Themyscira");
        s2.setInterests("Justice");
        s2.setSkills("Combat");
        s2.setLookingForTraineeship(false);
        s2.setAssignedTraineeship(null);

        studentMapper.save(s1);
        studentMapper.save(s2);

        List<Student> lookingStudents = studentMapper.findByLookingForTraineeshipTrue();
        Assertions.assertFalse(lookingStudents.isEmpty());
        Assertions.assertTrue(lookingStudents.stream().allMatch(Student::isLookingForTraineeship));
    }
}
