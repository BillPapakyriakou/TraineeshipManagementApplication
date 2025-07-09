package traineeship_app.domainmodel;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "professor")
public class Professor extends User{


    @Column(name = "professor_name", nullable = false)
    private String professorName;

    @Column(name = "interests")
    private String interests;

    @OneToMany(mappedBy = "supervisor")
    private List<TraineeshipPosition> supervisedPositions;  // One-to-many relationship with TraineeshipPosition
                                                        // One position has one supervisor

    public Professor() {}

    // Professor constructor
    public Professor(String professorName, String interests, List<TraineeshipPosition> supervisedPositions) {

        this.professorName = professorName;
        this.interests = interests;
        this.supervisedPositions = supervisedPositions;

    }


    // Getters & Setters
    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public List<TraineeshipPosition> getSupervisedPositions() {
        return supervisedPositions;
    }

    public void setSupervisedPositions(List<TraineeshipPosition> supervisedPositions) {
        this.supervisedPositions = supervisedPositions;
    }
}
