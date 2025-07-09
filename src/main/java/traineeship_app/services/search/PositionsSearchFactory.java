package traineeship_app.services.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionsSearchFactory {

    private final SearchBasedOnLocation searchBasedOnLocation;
    private final SearchBasedOnInterests searchBasedOnInterests;
    private final CompositeSearch compositeSearch;

    // Constructor to initialize the search strategies
    @Autowired
    public PositionsSearchFactory(SearchBasedOnLocation searchBasedOnLocation,
                                  SearchBasedOnInterests searchBasedOnInterests,
                                  CompositeSearch compositeSearch) {
        this.searchBasedOnLocation = searchBasedOnLocation;
        this.searchBasedOnInterests = searchBasedOnInterests;
        this.compositeSearch = compositeSearch;
    }

    public PositionsSearchStrategy create(String strategy) {
        switch (strategy.toLowerCase()) {
            case "interests":
                return searchBasedOnInterests; // Return the search strategy based on interests
            case "location":
                return searchBasedOnLocation; // Return the search strategy based on location
            case "both":
                return compositeSearch; // Return the composite search strategy (location + interests)
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategy);
        }
    }
}
