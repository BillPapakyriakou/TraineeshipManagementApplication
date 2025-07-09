package traineeship_app.services.assign;

import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;

public interface SupervisorAssignmentStrategy {

    List<Professor> assign(TraineeshipPosition position);
}
