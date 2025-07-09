package traineeship_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.Company;
import traineeship_app.domainmodel.Evaluation;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.*;


import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyMapper companyMapper;

    private final EvaluationMapper evaluationMapper;

    private final TraineeshipPositionsMapper traineeshipPositionMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public CompanyServiceImpl(CompanyMapper companyMapper, EvaluationMapper evaluationMapper, UserMapper userMapper, PasswordEncoder passwordEncoder, TraineeshipPositionsMapper traineeshipPositionMapper){
        this.companyMapper = companyMapper;
        this.evaluationMapper = evaluationMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.traineeshipPositionMapper = traineeshipPositionMapper;
    }


    @Override
    public Company retrieveProfile(String username) {
        return companyMapper.findByUsername(username);
    }


    @Override
    public void SaveProfile(Company company) {

        if (userMapper.existsByUsername(company.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        Company companyCopy = new Company();
        companyCopy.setUsername(company.getUsername());
        companyCopy.setPassword(passwordEncoder.encode(company.getPassword()));
        companyCopy.setRole(company.getRole());
        companyCopy.setCompanyName(company.getCompanyName());
        companyCopy.setCompanyLocation(company.getCompanyLocation());

        companyMapper.save(companyCopy);
    }


    @Override
    public void UpdateProfile(Company company) {

        if (company.getUsername() == null) {
            throw new RuntimeException("No username provided in form.");
        }

        Company existingCompany = companyMapper.findByUsername(company.getUsername());

        if (existingCompany == null) {
            throw new RuntimeException("Company not found.");
        }

        existingCompany.setCompanyName(company.getCompanyName());
        existingCompany.setCompanyLocation(company.getCompanyLocation());

        companyMapper.save(existingCompany);
    }


    @Override
    public List<TraineeshipPosition> retrieveAvailablePositions(String username) {
        // Get list of traineeship positions
        List<TraineeshipPosition> positions = traineeshipPositionMapper.findByCompanyUsernameAndIsCompletedFalseAndIsAssignedFalse(username);

        // Return ArrayList of positions
        return new ArrayList<>(positions);
    }

    @Override
    public void addPosition(String username, TraineeshipPosition position) {

        Company company = companyMapper.findByUsername(username);

        if (company != null) {
            TraineeshipPosition positionCopy = new TraineeshipPosition();
            positionCopy.setFromDate(position.getFromDate());
            positionCopy.setToDate(position.getToDate());
            positionCopy.setDescription(position.getDescription());
            positionCopy.setSkills(position.getSkills());
            positionCopy.setTopics(position.getTopics());
            positionCopy.setCompany(company);

            traineeshipPositionMapper.save(positionCopy);  // Save the new position

        } else {
            throw new IllegalArgumentException("Company not found with username: " + username);
        }
    }

    @Override
    public List<TraineeshipPosition> retrieveAssignedPositions(String username) {

        return traineeshipPositionMapper.findAssignedByCompanyUsername(username);

    }

    public TraineeshipPosition getPositionById(int positionId) {
        return traineeshipPositionMapper.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Traineeship position not found"));
    }


    @Override
    public void deletePosition(int positionId) {
        // Check if the position exists
        if (traineeshipPositionMapper.existsById(positionId)) {
            traineeshipPositionMapper.deleteById(positionId);
        } else {
            throw new IllegalArgumentException("Position with ID " + positionId + " not found.");
        }
    }


    @Override
    public void saveEvaluation(Evaluation evaluation) {

        evaluation.setEvaluationType(Evaluation.EvaluationType.COMPANY_EVALUATION);

        TraineeshipPosition fullPosition = traineeshipPositionMapper
                .findById(evaluation.getTraineeshipPosition().getId())
                .orElseThrow(() -> new RuntimeException("Position not found"));

        evaluation.setTraineeshipPosition(fullPosition);
        evaluation.setCompany(fullPosition.getCompany());

        evaluationMapper.save(evaluation);

    }

    @Override
    public void updateEvaluation(Evaluation evaluation) {
        if (evaluation.getTraineeshipPosition() == null || evaluation.getTraineeshipPosition().getId() == 0) {
            throw new RuntimeException("Traineeship position must be specified");
        }

        Optional<Evaluation> existingEvalOpt = evaluationMapper
                .findByEvaluationTypeAndTraineeshipPosition(
                        evaluation.getEvaluationType(),
                        evaluation.getTraineeshipPosition()
                );

        if (existingEvalOpt.isEmpty()) {
            throw new RuntimeException("No existing evaluation found to update");
        }

        Evaluation existingEval = existingEvalOpt.get();

        existingEval.setMotivation(evaluation.getMotivation());
        existingEval.setEfficiency(evaluation.getEfficiency());
        existingEval.setEffectiveness(evaluation.getEffectiveness());


        evaluationMapper.save(existingEval);
    }

    @Override
    public Optional<Evaluation> findByEvaluationTypeAndPosition(Evaluation.EvaluationType type, TraineeshipPosition position) {
        return evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(type, position);
    }


    @Override
    public Optional<Evaluation> findCompanyEvaluationByPosition(TraineeshipPosition position) {
        return evaluationMapper.findByEvaluationTypeAndTraineeshipPosition(
                Evaluation.EvaluationType.COMPANY_EVALUATION,
                position
        );
    }


}
