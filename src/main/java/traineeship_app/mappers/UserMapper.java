package traineeship_app.mappers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import traineeship_app.domainmodel.User;

import java.util.Optional;


//  UserMapper is now a Jpa repository, which comes with
//  built-in methods like save(), findById(), findAll(), etc

@Repository
public interface UserMapper extends JpaRepository<User, Long> {
    // id (Long) acts as the primary key here

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);


}