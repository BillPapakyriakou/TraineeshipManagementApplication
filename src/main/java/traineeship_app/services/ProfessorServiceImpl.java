package traineeship_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.Evaluation;
import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.EvaluationMapper;
import traineeship_app.mappers.ProfessorMapper;
import traineeship_app.mappers.TraineeshipPositionsMapper;
import traineeship_app.mappers.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorServiceImpl implements ProfessorService {


    private final ProfessorMapper professorMapper;
    private final UserMapper userMapper;
    private final TraineeshipPositionsMapper positionsMapper;
    private final EvaluationMapper evaluationMapper;
    private final PasswordEncoder passwordEncoder;

    public final TraineeshipPositionsMapper traineeshipPositionsMapper;

    @Autowired
    public ProfessorServiceImpl(ProfessorMapper professorMapper, UserMapper userMapper, TraineeshipPositionsMapper positionsMapper, EvaluationMapper evaluationMapper, PasswordEncoder passwordEncoder, TraineeshipPositionsMapper traineeshipPositionsMapper){
        this.professorMapper = professorMapper;
        this.userMapper = userMapper;
        this.positionsMapper = positionsMapper;
        this.evaluationMapper = evaluationMapper;
        this.passwordEncoder = passwordEncoder;
        this.traineeshipPositionsMapper = traineeshipPositionsMapper;
    }


    @Override
    public Professor retrieveProfile(String username) {
        return professorMapper.findByUsername(username);
    }

    @Override
    public void SaveProfile(Professor professor) {

        if (userMapper.existsByUsername(professor.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        Professor professorCopy = new Professor();
        professorCopy.setUsername(professor.getUsername());
        professorCopy.setPassword(passwordEncoder.encode(professor.getPassword()));
        professorCopy.setRole(professor.getRole());
        professorCopy.setProfessorName(professor.getProfessorName());

        professorMapper.save(professorCopy);

    }

    @Override
    public void UpdateProfile(Professor professor) {

        if (professor.getUsername() == null) {
            throw new RuntimeException("No username provided in form.");
        }

        Professor existingProfessor = professorMapper.findByUsername(professor.getUsername());

        if (existingProfessor == null) {
            throw new RuntimeException("Professor not found.");
        }

        existingProfessor.setProfessorName(professor.getProfessorName());
        existingProfessor.setInterests(professor.getInterests());

        professorMapper.save(existingProfessor);
    }

    @Override
    public List<TraineeshipPosition> retrieveAssignedPositions(String username) {

         List<TraineeshipPosition> positions = traineeshipPositionsMapper.findBySupervisorUsername(username);

        return new ArrayList<>(positions);

    }

    @Override
    public void saveEvaluation(Evaluation evaluation) {
        evaluation.setEvaluationType(Evaluation.EvaluationType.PROFESSOR_EVALUATION);

        TraineeshipPosition fullPosition = positionsMapper
                .findById(evaluation.getTraineeshipPosition().getId())
                .orElseThrow(() -> new RuntimeException("Position not found"));

        evaluation.setTraineeshipPosition(fullPosition);
        evaluation.setProfessor(fullPosition.getSupervisor());

        evaluationMapper.save(evaluation);
    }


    @Override
    public void updateEvaluation(Evaluation evaluation) {
        if (evaluation.getTraineeshipPosition() == null || evaluation.getTraineeshipPosition().getId() == 0) {
            throw new RuntimeException("Traineeship position must be specified");
        }

        Optional<Evaluation> existingEvalOpt = evaluationMapper
                .findByEvaluationTypeAndTraineeshipPosition(
                        Evaluation.EvaluationType.PROFESSOR_EVALUATION,
                        evaluation.getTraineeshipPosition()
                );

        if (existingEvalOpt.isEmpty()) {
            throw new RuntimeException("No existing evaluation found to update");
        }

        Evaluation existingEval = existingEvalOpt.get();

        existingEval.setMotivation(evaluation.getMotivation());
        existingEval.setEfficiency(evaluation.getEfficiency());
        existingEval.setEffectiveness(evaluation.getEffectiveness());

        // This updates the professor-specific fields - only for PROFESSOR_EVALUATION
        existingEval.setCompanyFacilities(evaluation.getCompanyFacilities());
        existingEval.setCompanyGuidance(evaluation.getCompanyGuidance());

        evaluationMapper.save(existingEval);
    }

    @Override
    public Optional<Evaluation> findByEvaluationTypeAndPosition(Evaluation.EvaluationType type, TraineeshipPosition position) {

        return evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(
                Evaluation.EvaluationType.PROFESSOR_EVALUATION,
                position
        );
    }

    @Override
    public Optional<Evaluation> findProfessorEvaluationByPosition(TraineeshipPosition position) {

        return evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(
                Evaluation.EvaluationType.PROFESSOR_EVALUATION,
                position
        );
    }


    @Override
    public Professor findByUsername(String professorUsername) {
        return professorMapper.findByUsername(professorUsername);
    }

}
