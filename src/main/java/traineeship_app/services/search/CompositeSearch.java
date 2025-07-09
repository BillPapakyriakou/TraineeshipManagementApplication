package traineeship_app.services.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.TraineeshipPosition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompositeSearch implements PositionsSearchStrategy {

    @Autowired
    private SearchBasedOnLocation searchBasedOnLocation;

    @Autowired
    private SearchBasedOnInterests searchBasedOnInterests;

    @Override
    public List<TraineeshipPosition> search(String applicantUsername) {
        // Get the positions based on location
        List<TraineeshipPosition> locationResults = searchBasedOnLocation.search(applicantUsername);

        // Get the positions based on interests
        List<TraineeshipPosition> interestResults = searchBasedOnInterests.search(applicantUsername);

        // Combine the results and remove duplicates using a Set (to ensure uniqueness)
        Set<TraineeshipPosition> combinedResults = new HashSet<>();
        combinedResults.addAll(locationResults);
        combinedResults.addAll(interestResults);

        // Return the combined list of positions
        return combinedResults.stream().collect(Collectors.toList());
    }

}
