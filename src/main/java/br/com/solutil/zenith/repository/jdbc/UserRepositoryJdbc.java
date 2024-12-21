package br.com.solutil.zenith.repository.jdbc;

import br.com.solutil.zenith.model.User;
import br.com.solutil.zenith.model.Role;
import br.com.solutil.zenith.security.oauth2.AuthProvider;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@Repository
public class UserRepositoryJdbc {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setImageUrl(rs.getString("image_url"));
        user.setProvider(AuthProvider.valueOf(rs.getString("provider")));
        user.setProviderId(rs.getString("provider_id"));
        user.setEmailVerified(rs.getBoolean("email_verified"));
        user.setMemberStatus(User.MemberStatus.valueOf(rs.getString("member_status")));
        user.setRoles(getUserRoles(user.getId()));
        return user;
    };

    private Set<Role> getUserRoles(Long userId) {
        final String sql = """
                SELECT r.* FROM roles r \
                INNER JOIN user_roles ur ON r.id = ur.role_id \
                WHERE ur.user_id = ?""";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> {
            Role role = new Role();
            role.setId(rs.getLong("id"));
            role.setName(Role.RoleType.valueOf(rs.getString("name")));
            return role;
        }, userId));
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            Logger.getLogger(UserRepositoryJdbc.class.getName()).log(java.util.logging.Level.SEVERE, null, e);  
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            Logger.getLogger(UserRepositoryJdbc.class.getName()).log(java.util.logging.Level.SEVERE, null, e);  
            return Optional.empty();
        }
    }

    public Boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count > 0;
    }

    public Boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count > 0;
    }

    @Transactional
    public Optional<User> save(User user) {

        try {
            if (user.getId() == null) {
                String sql = """
                        INSERT INTO users \
                        (username, password, name, \
                        email, image_url, provider, \
                        provider_id, email_verified, member_status) \
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""";

                jdbcTemplate.update(sql,
                        user.getUsername(),
                        user.getPassword(),
                        user.getName(),
                        user.getEmail(),
                        user.getImageUrl(),
                        user.getProvider().toString(),
                        user.getProviderId(),
                        user.getEmailVerified(),
                        user.getMemberStatus().toString());

                // Get the generated id
                String idSql = "SELECT id FROM users WHERE username = ?";
                Long id = jdbcTemplate.queryForObject(idSql, Long.class, user.getUsername());
                user.setId(id);
            } else {
                String sql = """
                        UPDATE users SET username = ?, password = ?, \
                        name = ?, email = ?, \
                        image_url = ?, provider = ?, \
                        provider_id = ?, email_verified = ?, \
                        member_status = ? WHERE id = ?""";

                jdbcTemplate.update(sql,
                        user.getUsername(),
                        user.getPassword(),
                        user.getName(),
                        user.getEmail(),
                        user.getImageUrl(),
                        user.getProvider().toString(),
                        user.getProviderId(),
                        user.getEmailVerified(),
                        user.getMemberStatus().toString(),
                        user.getId());
            }

            // Update user roles
            updateUserRoles(user);
            
        } catch (DataAccessException e) {
            Logger.getLogger(UserRepositoryJdbc.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
            return Optional.empty();
        }
        return Optional.ofNullable(user).map(Optional::of).orElse(Optional.empty()); // Optional.ofNullable(user;)
    }

    private void updateUserRoles(User user) {
        // First delete existing roles
        String deleteSql = "DELETE FROM user_roles WHERE user_id = ?";
        jdbcTemplate.update(deleteSql, user.getId());

        // Then insert new roles
        String insertSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        for (Role role : user.getRoles()) {
            jdbcTemplate.update(insertSql, user.getId(), role.getId());
        }
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void delete(User user) {
        // First delete from user_roles
        String deleteRolesSql = "DELETE FROM user_roles WHERE user_id = ?";
        jdbcTemplate.update(deleteRolesSql, user.getId());

        // Then delete the user
        String deleteUserSql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteUserSql, user.getId());
    }
}
