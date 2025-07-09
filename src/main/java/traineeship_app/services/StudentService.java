package traineeship_app.services;

import traineeship_app.domainmodel.Student;
import traineeship_app.domainmodel.TraineeshipPosition;

public interface StudentService {

    void SaveProfile(Student student);

    void UpdateProfile(Student student);

    Student retrieveProfile(String studentUsername);

    void saveLogBook(TraineeshipPosition position, String username);

}
