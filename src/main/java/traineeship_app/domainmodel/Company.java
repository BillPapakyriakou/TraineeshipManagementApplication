package traineeship_app.domainmodel;

import jakarta.persistence.*;
import org.springframework.ui.Model;


import java.util.List;

@Entity
@Table(name = "company")
public class Company extends User{

    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    @Column(name = "company_location")
    private String companyLocation;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TraineeshipPosition> positions;


    // Default constructor (JPA requires it for entities)
    public Company() {}

    // Company constructor
    public Company(String companyName, String companyLocation, List<TraineeshipPosition> positions) {
        this.companyName = companyName;
        this.companyLocation = companyLocation;
        this.positions = positions;
    }

    // Getters & Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public List<TraineeshipPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<TraineeshipPosition> positions) {
        this.positions = positions;
    }
}
