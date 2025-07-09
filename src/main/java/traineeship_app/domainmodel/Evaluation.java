package traineeship_app.domainmodel;


import jakarta.persistence.*;

@Entity
@Table(name = "evaluation")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")  // Column for the primary key
    private int id;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor; // Only for PROFESSOR_EVALUATION

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company; // Only for COMPANY_EVALUATION

    @Enumerated(EnumType.STRING)  // Store enum as a string in the database
    @Column(name = "evaluation_type", length = 20)
    private EvaluationType evaluationType;

    @Column(name = "motivation")
    int motivation;

    @Column(name = "efficiency")
    int efficiency;

    @Column(name = "effectiveness")
    int effectiveness;

    @Column(name = "company_facilities")
    private Integer companyFacilities; // Only for PROFESSOR_EVALUATION

    @Column(name = "company_guidance")
    private Integer companyGuidance; // Only for PROFESSOR_EVALUATION

    @ManyToOne
    @JoinColumn(name = "traineeship_position_id")
    private TraineeshipPosition traineeshipPosition;



    // We decided to set up EvaluationType as an enum inside the class
    public enum EvaluationType {
        PROFESSOR_EVALUATION,

        COMPANY_EVALUATION
    }

    public Evaluation() {}


    // Evaluation constructor
    public Evaluation(int id, Professor professor, Company company, EvaluationType evaluationType, int motivation, int efficiency, int effectiveness, Integer companyFacilities, Integer companyGuidance, TraineeshipPosition traineeshipPosition) {
        this.id = id;
        this.professor = professor;
        this.company = company;
        this.evaluationType = evaluationType;
        this.motivation = motivation;
        this.efficiency = efficiency;
        this.effectiveness = effectiveness;
        this.companyFacilities = companyFacilities;
        this.companyGuidance = companyGuidance;
        this.traineeshipPosition = traineeshipPosition;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
    }

    public int getMotivation() {
        return motivation;
    }

    public void setMotivation(int motivation) {
        this.motivation = motivation;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public int getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(int effectiveness) {
        this.effectiveness = effectiveness;
    }


    public Integer getCompanyFacilities() {
        return companyFacilities;
    }

    public void setCompanyFacilities(Integer companyFacilities) {
        this.companyFacilities = companyFacilities;
    }

    public Integer getCompanyGuidance() {
        return companyGuidance;
    }

    public void setCompanyGuidance(Integer companyGuidance) {
        this.companyGuidance = companyGuidance;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public TraineeshipPosition getTraineeshipPosition() {
        return traineeshipPosition;
    }

    public void setTraineeshipPosition(TraineeshipPosition traineeshipPosition) {
        this.traineeshipPosition = traineeshipPosition;
    }

}
