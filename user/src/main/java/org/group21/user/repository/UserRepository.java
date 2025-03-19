package org.group21.user.repository;

import org.group21.user.model.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmailIgnoreCase(String email);
    void deleteByEmailIgnoreCase(String email);
}
