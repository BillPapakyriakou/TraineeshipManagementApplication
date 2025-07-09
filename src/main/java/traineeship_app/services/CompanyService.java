package traineeship_app.services;

import traineeship_app.domainmodel.Company;
import traineeship_app.domainmodel.Evaluation;
import traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    Company retrieveProfile(String username);

    void SaveProfile(Company company);

    void UpdateProfile(Company company);

    List<TraineeshipPosition> retrieveAvailablePositions(String username);

    void addPosition(String username, TraineeshipPosition position);

    List<TraineeshipPosition> retrieveAssignedPositions(String username);

    //void evaluateAssignedPosition(int positionId);

    void saveEvaluation(Evaluation evaluation);

    void deletePosition(int positionId);

    TraineeshipPosition getPositionById(int positionId);

    Optional<Evaluation> findCompanyEvaluationByPosition(TraineeshipPosition position);

    void updateEvaluation(Evaluation evaluation);

    Optional<Evaluation> findByEvaluationTypeAndPosition(Evaluation.EvaluationType type, TraineeshipPosition position);
}
