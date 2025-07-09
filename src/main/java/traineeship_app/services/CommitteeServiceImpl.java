package traineeship_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.Committee;
import traineeship_app.domainmodel.Professor;
import traineeship_app.mappers.*;
import traineeship_app.domainmodel.Student;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.services.assign.SupervisorAssignmentFactory;
import traineeship_app.services.search.PositionsSearchFactory;
//import traineeship_app.services.search.PositionsSearchFactory;
//import traineeship_app.services.search.PositionsSearchStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommitteeServiceImpl implements CommitteeService {

    private final CommitteeMapper committeeMapper;

    private final TraineeshipPositionsMapper traineeshipPositionsMapper;

    private final StudentMapper studentMapper;

    private final ProfessorMapper professorMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final PositionsSearchFactory positionsSearchFactory;

    private final SupervisorAssignmentFactory assignmentFactory;

    @Autowired
    public CommitteeServiceImpl(CommitteeMapper committeeMapper, TraineeshipPositionsMapper traineeshipPositionsMapper, StudentMapper studentMapper, ProfessorMapper professorMapper, UserMapper userMapper, PasswordEncoder passwordEncoder, PositionsSearchFactory positionsSearchFactory, SupervisorAssignmentFactory assignmentFactory){
        this.committeeMapper = committeeMapper;
        this.traineeshipPositionsMapper = traineeshipPositionsMapper;
        this.studentMapper = studentMapper;
        this.professorMapper = professorMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.positionsSearchFactory = positionsSearchFactory;
        this.assignmentFactory = assignmentFactory;
    }


    @Override
    public void SaveProfile(Committee committee){
        if (userMapper.existsByUsername(committee.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        Committee committeeCopy = new Committee();
        committeeCopy.setUsername(committee.getUsername());
        committeeCopy.setPassword(passwordEncoder.encode(committee.getPassword()));
        committeeCopy.setRole(committee.getRole());
        committeeCopy.setCommitteeMemberName(committee.getCommitteeMemberName());

        committeeMapper.save(committeeCopy);

    }


    @Override
    public List<TraineeshipPosition> retrievePositionsForApplicant(String applicantUsername, String strategy) {
        return List.of();
    }

    @Override
    public List<Student> retrieveTraineeshipApplications() {
        return List.of();
    }

    @Override
    public List<TraineeshipPosition> listUnsupervisedPositions() {
        return traineeshipPositionsMapper.findByIsAssignedTrueAndSupervisorIsNull();
    }

    @Override
    public void assignSupervisor(int positionId, String strategy) {

    }

    @Override
    public List<TraineeshipPosition> listAssignedTraineeships() {
        return traineeshipPositionsMapper.findByIsAssignedTrue();
    }

    @Override
    public void completeAssignedTraineeships(int positionId) {

    }

    @Override
    public List<Student> getAllStudentsLookingForTraineeship() {
        return studentMapper.findByLookingForTraineeshipTrue();
    }


    public List<TraineeshipPosition> getCandidatePositions(String strategy, String applicantUsername) {
        var searchStrategy = positionsSearchFactory.create(strategy);
        return searchStrategy.search(applicantUsername)
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.isCompleted()))
                .collect(Collectors.toList());
    }

    public Student getStudentById(long studentId) {
        return studentMapper.findById(studentId).orElse(null);
    }

    @Override
    public void assignPosition(int positionId, String studentUsername) {
        TraineeshipPosition position = traineeshipPositionsMapper.findById(positionId).orElse(null);
        Student student = studentMapper.findByUsername(studentUsername);

        if (position == null) {
            throw new IllegalArgumentException("Traineeship position not found.");
        }
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }
        if (position.isAssigned()) {
            throw new IllegalStateException("Traineeship position is already assigned.");
        }

        // Assign the student to the position
        position.setStudent(student);
        position.setAssigned(true);
        student.setAssignedTraineeship(position);
        student.setLookingForTraineeship(false);

        traineeshipPositionsMapper.save(position);
    }

    @Override
    public List<Professor> getCandidateProfessors(String strategy, TraineeshipPosition position) {
        var assignmentStrategy = assignmentFactory.create(strategy);

        return switch (strategy.toLowerCase()) {
            case "load" -> assignmentStrategy.assign(position);
            case "interests" -> assignmentStrategy.assign(position);
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
    }

    @Override
    public TraineeshipPosition getPositionById(Long positionId) {
        return traineeshipPositionsMapper.findById(positionId).orElse(null);
    }

    public Map<Long, Integer> getWorkloadForProfessors(List<Professor> professors) {
        Map<Long, Integer> workloadMap = new HashMap<>();
        for (Professor prof : professors) {
            int workload = professorMapper.countSupervisedPositions(prof.getId());
            workloadMap.put(prof.getId(), workload);
        }
        return workloadMap;
    }

    @Override
    public void assignSupervisorToPosition(int positionId, Long professorId) {
        TraineeshipPosition position = traineeshipPositionsMapper.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid position ID"));

        Professor professor = professorMapper.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid professor ID"));

        position.setSupervisor(professor);
        traineeshipPositionsMapper.save(position);
    }

    @Override
    public List<TraineeshipPosition> getPositionsBySupervisor(Professor professor) {

        return traineeshipPositionsMapper.findBySupervisorId(professor.getId());
    }

    @Override
    public TraineeshipPosition savePosition(TraineeshipPosition position) {
        return traineeshipPositionsMapper.save(position);
    }

    @Override
    public Student saveStudent(Student student) {
        return studentMapper.save(student);
    }

}

