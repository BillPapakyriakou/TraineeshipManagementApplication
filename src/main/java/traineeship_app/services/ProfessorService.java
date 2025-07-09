package traineeship_app.services;

import traineeship_app.domainmodel.Evaluation;
import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {

    Professor retrieveProfile(String username);

    void SaveProfile(Professor professor);

    void UpdateProfile(Professor professor);

    List<TraineeshipPosition> retrieveAssignedPositions(String username);

    void saveEvaluation(Evaluation evaluation);

    void updateEvaluation(Evaluation evaluation);

    Optional<Evaluation> findByEvaluationTypeAndPosition(Evaluation.EvaluationType type, TraineeshipPosition position);

    //Optional<Evaluation> findCompanyEvaluationByPosition(TraineeshipPosition position);

    Optional<Evaluation> findProfessorEvaluationByPosition(TraineeshipPosition position);

    Professor findByUsername(String professorUsername);
}
