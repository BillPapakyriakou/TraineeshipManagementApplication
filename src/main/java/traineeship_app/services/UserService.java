package traineeship_app.services;

import traineeship_app.domainmodel.User;

import java.util.Optional;

public interface UserService {

    void saveUser(User user);  // saves or updates a user

    boolean isUserPresent(User user);  // checks if the user is present

    Optional<User> findById(String username);  // finds user by username

    //long getUserIdByUsername(String username);
}
