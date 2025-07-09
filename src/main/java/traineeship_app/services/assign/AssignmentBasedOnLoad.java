package traineeship_app.services.assign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.ProfessorMapper;
import traineeship_app.mappers.TraineeshipPositionsMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentBasedOnLoad implements SupervisorAssignmentStrategy {

    @Autowired
    private TraineeshipPositionsMapper positionsMapper;
    @Autowired
    private ProfessorMapper professorMapper;



    @Override
    public List<Professor> assign(TraineeshipPosition position) {
        List<Object[]> results = professorMapper.findAllProfessorsSortedByLoad();

        return results.stream()
                .map(row -> (Professor) row[0])
                .collect(Collectors.toList());
    }

}

