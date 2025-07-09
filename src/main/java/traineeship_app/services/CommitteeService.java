package traineeship_app.services;

import traineeship_app.domainmodel.Committee;
import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.Student;
import traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;
import java.util.Map;

public interface CommitteeService {

    List<TraineeshipPosition> retrievePositionsForApplicant(String applicantUsername, String strategy);

    List<Student> getAllStudentsLookingForTraineeship();

    List<Student> retrieveTraineeshipApplications();

    void assignPosition(int positionId, String studentUsername);

    List<TraineeshipPosition> listUnsupervisedPositions();

    void assignSupervisor(int positionId, String strategy);

    List<TraineeshipPosition> listAssignedTraineeships();

    void completeAssignedTraineeships(int positionId);

    void SaveProfile(Committee committee);

    List<TraineeshipPosition> getCandidatePositions(String strategy, String applicantUsername);

    Student getStudentById(long studentId);

    List<Professor> getCandidateProfessors(String strategy, TraineeshipPosition position);


    TraineeshipPosition getPositionById(Long positionId);

    Map<Long, Integer> getWorkloadForProfessors(List<Professor> candidateProfessors);

    void assignSupervisorToPosition(int positionId, Long professorId);

    List<TraineeshipPosition> getPositionsBySupervisor(Professor professor);

    TraineeshipPosition savePosition(TraineeshipPosition position);

    Student saveStudent(Student student);
}
