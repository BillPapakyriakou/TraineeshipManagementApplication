package traineeship_app.services.assign;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class SupervisorAssignmentFactory {

    private final AssignmentBasedOnLoad assignmentBasedOnLoad;
    private final AssignmentBasedOnInterests assignmentBasedOnInterests;

    @Autowired
    public SupervisorAssignmentFactory(AssignmentBasedOnLoad assignmentBasedOnLoad,
                                       AssignmentBasedOnInterests assignmentBasedOnInterests) {
        this.assignmentBasedOnLoad = assignmentBasedOnLoad;
        this.assignmentBasedOnInterests = assignmentBasedOnInterests;
    }

    public SupervisorAssignmentStrategy create(String strategy) {
        return switch (strategy.toLowerCase()) {
            case "load" -> assignmentBasedOnLoad;
            case "interests" -> assignmentBasedOnInterests;
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
    }
}
