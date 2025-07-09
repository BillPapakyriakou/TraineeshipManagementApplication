package traineeship_app.domainmodel;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "traineeship_position")
public class TraineeshipPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "fromDate")
    private LocalDate fromDate;

    @Column(name = "toDate")
    private LocalDate toDate;

    @Column(name = "topics")
    private String topics;

    @Column(name = "skills")
    private String skills;

    @Column(name = "isAssigned", nullable = false)
    private boolean isAssigned;

    @Column(name = "isCompleted", nullable = false)
    private boolean isCompleted;  // New field

    @Column(name = "studentLogbook")
    private String studentLogbook;

    @Column(name = "passFailGrade", nullable = false)
    private boolean passFailGrade;

    //@OneToOne
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;  // Many-to-one with student so that it doesnt give us an error
                            // when a student that has completed a traineeship is assigned a new one

    @ManyToOne
    @JoinColumn(name = "supervisor_id", referencedColumnName = "id")
    private Professor supervisor;  // Many-to-one with professor

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;  // Many-to-one with company

    @OneToMany(mappedBy = "traineeshipPosition")
    private List<Evaluation> evaluations;  // One-to-many with Evaluation


    // Default constructor (JPA requirement for entities)
    public TraineeshipPosition(){}

    // TraineeshipPosition constructor

    public TraineeshipPosition(int id, String title, String description, LocalDate fromDate, LocalDate toDate, String topics,
                               String skills, boolean isAssigned, boolean isCompleted, String studentLogbook, boolean passFailGrade, Student student,
                               Professor supervisor, Company company, List<Evaluation> evaluations) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.topics = topics;
        this.skills = skills;
        this.isAssigned = isAssigned;
        this.isCompleted = isCompleted;
        this.studentLogbook = studentLogbook;
        this.passFailGrade = passFailGrade;
        this.student = student;
        this.supervisor = supervisor;
        this.company = company;
        this.evaluations = evaluations;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public String getStudentLogbook() {
        return studentLogbook;
    }

    public void setStudentLogbook(String studentLogbook) {
        this.studentLogbook = studentLogbook;
    }

    public boolean isPassFailGrade() {
        return passFailGrade;
    }

    public void setPassFailGrade(boolean passFailGrade) {
        this.passFailGrade = passFailGrade;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Professor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Professor supervisor) {
        this.supervisor = supervisor;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
