package traineeship_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.Student;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.StudentMapper;
import traineeship_app.mappers.TraineeshipPositionsMapper;
import traineeship_app.mappers.UserMapper;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {


    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final TraineeshipPositionsMapper traineeshipPositionsMapper;

    @Autowired
    public StudentServiceImpl(StudentMapper studentMapper, UserMapper userMapper, PasswordEncoder passwordEncoder, TraineeshipPositionsMapper traineeshipPositionsMapper) {
        this.studentMapper = studentMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.traineeshipPositionsMapper = traineeshipPositionsMapper;
    }


    @Override
    public void SaveProfile(Student student) {
        if (userMapper.existsByUsername(student.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }

        if (studentMapper.existsStudentByAM(student.getAM())) {
            throw new DataIntegrityViolationException("AM already exists");
        }

        // Creates copy of student
        Student studentCopy = new Student();
        studentCopy.setUsername(student.getUsername());
        studentCopy.setPassword(passwordEncoder.encode(student.getPassword()));
        studentCopy.setRole(student.getRole());
        studentCopy.setStudentName(student.getStudentName());
        studentCopy.setAM(student.getAM());
        studentCopy.setAvgGrade(student.getAvgGrade());
        studentCopy.setPreferredLocation(student.getPreferredLocation());
        studentCopy.setLookingForTraineeship(student.isLookingForTraineeship());
        studentCopy.setSkills(student.getSkills());
        studentCopy.setInterests(student.getInterests());

        // Saves student to db
        studentMapper.save(studentCopy);
    }




    @Override
    public void UpdateProfile(Student student) {
        if (student.getUsername() == null) {
            throw new RuntimeException("No username provided in form.");
        }
        Student existingStudent = studentMapper.findByUsername(student.getUsername());

        if (existingStudent == null) {
            throw new RuntimeException("Student not found.");
        }

        existingStudent.setStudentName(student.getStudentName());
        existingStudent.setAM(student.getAM());
        existingStudent.setAvgGrade(student.getAvgGrade());
        existingStudent.setPreferredLocation(student.getPreferredLocation());
        existingStudent.setLookingForTraineeship(student.isLookingForTraineeship());
        existingStudent.setSkills(student.getSkills());
        existingStudent.setInterests(student.getInterests());

        studentMapper.save(existingStudent);
    }



    @Override
    public Student retrieveProfile(String studentUsername) {
        return studentMapper.findByUsername(studentUsername);
    }


    @Override
    public void saveLogBook(TraineeshipPosition position, String username) {

        Student student = position.getStudent();

        // Find the traineeship position assigned to the student
        TraineeshipPosition studentPosition = traineeshipPositionsMapper
                .findByStudentUsernameAndIsCompletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("No assigned traineeship position found for student: "));

        // Update logbook for found position
        studentPosition.setStudentLogbook(position.getStudentLogbook());

        // Save the updated traineeship position back to our db
        traineeshipPositionsMapper.save(studentPosition);
    }


}

