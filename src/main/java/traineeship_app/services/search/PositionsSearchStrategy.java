package traineeship_app.services.search;

import traineeship_app.domainmodel.TraineeshipPosition;
import java.util.List;

public interface PositionsSearchStrategy {

    List<TraineeshipPosition> search(String applicantUsername);

}
