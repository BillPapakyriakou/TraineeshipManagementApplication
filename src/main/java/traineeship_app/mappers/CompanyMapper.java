package traineeship_app.mappers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import traineeship_app.domainmodel.Company;
import traineeship_app.domainmodel.TraineeshipPosition;

import java.util.List;


//  CompanyMapper is now a Jpa repository, which comes with
//  built-in methods like save(), findById(), findAll(), etc

@Repository
public interface CompanyMapper extends JpaRepository<Company, Long> {

    Company findByUsername(String username);

}
