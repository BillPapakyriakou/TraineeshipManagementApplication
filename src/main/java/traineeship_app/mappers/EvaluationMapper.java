package traineeship_app.mappers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import traineeship_app.domainmodel.Evaluation;
import traineeship_app.domainmodel.TraineeshipPosition;

import java.util.Optional;

@Repository
public interface EvaluationMapper extends JpaRepository<Evaluation, Integer> {

    Optional<Evaluation> findByEvaluationTypeAndTraineeshipPosition(
            Evaluation.EvaluationType evaluationType,
            TraineeshipPosition traineeshipPosition
    );
}