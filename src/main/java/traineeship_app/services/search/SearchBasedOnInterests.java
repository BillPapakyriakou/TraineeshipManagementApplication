package traineeship_app.services.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.Student;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.StudentMapper;
import traineeship_app.mappers.TraineeshipPositionsMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchBasedOnInterests implements PositionsSearchStrategy {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TraineeshipPositionsMapper traineeshipPositionsMapper;

    @Override
    public List<TraineeshipPosition> search(String applicantUsername) {
        Student student = studentMapper.findByUsername(applicantUsername);
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }

        List<String> studentSkills = parseTags(student.getSkills());
        List<String> studentInterests = parseTags(student.getInterests());

        return traineeshipPositionsMapper.findByIsAssignedFalse().stream()
                .filter(position -> {
                    //boolean skillsMatch = parseTags(position.getSkills()).stream().anyMatch(studentSkills::contains);
                    boolean interestsMatch = parseTags(position.getTopics()).stream().anyMatch(studentInterests::contains);

                    return interestsMatch;
                    //return skillsMatch && interestsMatch;
                })
                .collect(Collectors.toList());
    }

    private List<String> parseTags(String input) {
        return input != null && !input.isBlank()
                ? Arrays.stream(input.split("\\s*,\\s*")).map(String::trim).collect(Collectors.toList())
                : List.of();
    }
}



