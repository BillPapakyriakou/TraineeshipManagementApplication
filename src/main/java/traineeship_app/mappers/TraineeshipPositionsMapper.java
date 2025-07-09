package traineeship_app.mappers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.TraineeshipPosition;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;


//  TraineeshipPositionMapper is now a Jpa repository, which comes with
//  built-in methods like save(), findById(), findAll(), etc

@Repository
public interface TraineeshipPositionsMapper extends JpaRepository<TraineeshipPosition, Integer> {
    // id (int) acts as the primary key


    List<TraineeshipPosition> findByIsAssignedTrueAndSupervisorIsNull();


    @Query("SELECT p FROM TraineeshipPosition p WHERE p.company.username = :username AND p.student IS NOT NULL AND p.isCompleted = false")
    List<TraineeshipPosition> findAssignedByCompanyUsername(@Param("username") String username);


    List<TraineeshipPosition> findBySupervisorUsername(String username);


    List<TraineeshipPosition> findByIsAssignedTrue();

    List<TraineeshipPosition> findByIsAssignedFalse();


    List<TraineeshipPosition> findBySupervisorId(Long supervisorId);


    Optional<TraineeshipPosition> findById(Long positionId);

    Optional<TraineeshipPosition> findByStudentUsernameAndIsCompletedFalse(String username);

    List<TraineeshipPosition> findByCompanyUsernameAndIsCompletedFalseAndIsAssignedFalse(String username);

}
