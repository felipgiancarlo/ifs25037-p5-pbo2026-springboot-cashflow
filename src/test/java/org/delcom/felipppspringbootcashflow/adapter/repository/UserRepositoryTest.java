package org.delcom.felipppspringbootcashflow.adapter.repository;

import org.delcom.felipppspringbootcashflow.domain.entity.Role;
import org.delcom.felipppspringbootcashflow.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest {

    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepository();
    }

    @Test
    void saveFindUpdateAndDelete() {
        User user = repository.saveNew("andi", "hash", "Andi", Role.USER);
        assertEquals(1, user.getId());
        assertTrue(repository.findByUsername("ANDI").isPresent());

        user.changeFullName("Andi Baru");
        repository.update(user);
        assertEquals("Andi Baru", repository.findById(1).orElseThrow().getFullName());

        assertTrue(repository.deleteById(1));
        assertTrue(repository.findByUsername("andi").isEmpty());
    }
}
