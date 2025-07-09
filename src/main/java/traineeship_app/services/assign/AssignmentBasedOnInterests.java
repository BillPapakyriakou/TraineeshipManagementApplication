package traineeship_app.services.assign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.ProfessorMapper;
import traineeship_app.services.assign.SupervisorAssignmentStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentBasedOnInterests implements SupervisorAssignmentStrategy {

    @Autowired
    private ProfessorMapper professorMapper;


    @Override
    public List<Professor> assign(TraineeshipPosition position) {
        List<String> positionTopics = parseTags(position.getTopics());

        return professorMapper.findAll().stream()
                .filter(professor -> {
                    List<String> professorInterests = parseTags(professor.getInterests());
                    return professorInterests.stream().anyMatch(positionTopics::contains);
                })
                .collect(Collectors.toList());
    }

    private List<String> parseTags(String input) {
        return input != null && !input.isBlank()
                ? Arrays.stream(input.split("\\s*,\\s*")).map(String::trim).collect(Collectors.toList())
                : List.of();
    }
}
