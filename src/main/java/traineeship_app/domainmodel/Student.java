package traineeship_app.domainmodel;


import jakarta.persistence.*;

@Entity
@Table(name = "student")
public class Student extends User{

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "AM", unique = true, nullable = false)
    private String AM;

    @Column(name = "avg_grade")
    private double avgGrade;

    @Column(name = "preferred_location")
    private String preferredLocation;

    @Column(name = "interests")
    private String interests;

    @Column(name = "skills")
    private String skills;

    @Column(name = "looking_for_traineeship", nullable = false)
    private boolean lookingForTraineeship;

    @OneToOne(cascade = CascadeType.ALL)   // student can be assigned to ONE traineeship position
    @JoinColumn(name = "assigned_traineeship_id", referencedColumnName = "id")
    private TraineeshipPosition assignedTraineeship;


    // Default constructor (JPA requirement for entities)
    public Student() {}


    // Student constructor
    public Student(String studentName, String AM, double avgGrade, String preferredLocation,
                   String interests, String skills, boolean lookingForTraineeship, TraineeshipPosition assignedTraineeship) {
        this.studentName = studentName;
        this.AM = AM;
        this.avgGrade = avgGrade;
        this.preferredLocation = preferredLocation;
        this.interests = interests;
        this.skills = skills;
        this.lookingForTraineeship = lookingForTraineeship;
        this.assignedTraineeship = assignedTraineeship;
    }

    // Getters & Setters

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAM() {
        return AM;
    }

    public void setAM(String AM) {
        this.AM = AM;
    }

    public double getAvgGrade() {
        return avgGrade;
    }

    public void setAvgGrade(double avgGrade) {
        this.avgGrade = avgGrade;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public boolean isLookingForTraineeship() {
        return lookingForTraineeship;
    }

    public void setLookingForTraineeship(boolean lookingForTraineeship) {
        this.lookingForTraineeship = lookingForTraineeship;
    }

    public TraineeshipPosition getAssignedTraineeship() {
        return assignedTraineeship;
    }

    public void setAssignedTraineeship(TraineeshipPosition assignedTraineeship) {
        this.assignedTraineeship = assignedTraineeship;
    }
}
