package traineeship_app.services;



import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import traineeship_app.domainmodel.User;
import traineeship_app.mappers.UserMapper;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final PasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userDAO;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserMapper userDAO) {
        this.bCryptPasswordEncoder = passwordEncoder;
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userDAO.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
        );
    }

    @Transactional
    @Override
    public void saveUser(User user) {
        // Check if user already exists
        if (userDAO.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists with username: " + user.getUsername());
        }

        // Encrypt the password before saving the user
        String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        userDAO.save(user);
    }

    @Override
    public boolean isUserPresent(User user) {
        Optional<User> storedUser = userDAO.findByUsername(user.getUsername());
        return storedUser != null;
    }

    @Transactional
    @Override
    public Optional<User> findById(String username) {
        return userDAO.findByUsername(username);
    }


}
