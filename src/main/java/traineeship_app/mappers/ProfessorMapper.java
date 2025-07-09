package traineeship_app.mappers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;
import java.util.Optional;


//  ProfessorMapper is now a Jpa repository, which comes with
//  built-in methods like save(), findById(), findAll(), etc

@Repository
public interface ProfessorMapper extends JpaRepository<Professor, Long>{

    Professor findByUsername(String username);


    @Query("SELECT p.username FROM Professor p LEFT JOIN p.supervisedPositions sp GROUP BY p.username ORDER BY COUNT(sp) ASC")
    String findLeastBusyProfessor();

    @Query("SELECT COUNT(p) FROM TraineeshipPosition p WHERE p.supervisor.id = :professorId")
    int countSupervisedPositions(@Param("professorId") Long professorId);

    @Query("SELECT p, COUNT(t) AS supervisionCount " +
            "FROM Professor p LEFT JOIN TraineeshipPosition t ON p.id = t.supervisor.id " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(t) ASC")
    List<Object[]> findAllProfessorsSortedByLoad();


    Optional<Professor> findById(Long professorId);
}
