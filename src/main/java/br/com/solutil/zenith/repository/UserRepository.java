package br.com.solutil.zenith.repository;

import br.com.solutil.zenith.model.User;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository   extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    User save(User user);
    Optional<User> findById(Long id);
    void delete(User user);
}
