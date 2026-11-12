package org.delcom.felipppspringbootcashflow.domain.repository;

import org.delcom.felipppspringbootcashflow.domain.entity.Role;
import org.delcom.felipppspringbootcashflow.domain.entity.User;

import java.util.Optional;

/**
 * Port penyimpanan pengguna.
 */
public interface IUserRepository extends IRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    User saveNew(String username, String passwordHash, String fullName, Role role);
}
