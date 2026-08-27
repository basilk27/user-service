package com.mbsystems.userservice.repository;

import com.mbsystems.userservice.TestcontainersConfiguration;
import com.mbsystems.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User newUser;
    private User user;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        newUser = new User(
                null,
                "John",
                "Doe",
                "john.doe@example.com",
                "123 Main St",
                true,
                150.0
        );

        user = new User(
                null,
                "Alice",
                "Smith",
                "alice.smith@example.com",
                "456 High St",
                false,
                0.0
        );

        user1 = new User(null, "User1", "One", "duplicate@example.com",
                         "Addr 1", false, 0.0);
        user2 = new User(null, "User2", "Two", "duplicate@example.com",
                         "Addr 2", false, 0.0);


    }

    @Test
    @DisplayName("Should persist user and generate database ID")
    void shouldSaveAndFindUserById() {
        //given

        //when
        User savedUser = userRepository.save(newUser);

        //then
        assertThat(savedUser.id()).isNotNull();
        assertThat(savedUser.name()).isEqualTo("John");

        Optional<User> foundUser = userRepository.findById(savedUser.id());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().email()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should find user by email and check existence")
    void shouldFindByEmailAndExistsByEmail() {
        //given

        //when
        userRepository.save(user);

        //then
        Optional<User> found = userRepository.findByEmail("alice.smith@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().name()).isEqualTo("Alice");

        assertThat(userRepository.existsByEmail("alice.smith@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should reject duplicate email due to unique constraint")
    void shouldFailOnDuplicateEmail() {
        //given

        //when
        userRepository.save(user1);

        //then
        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("Should update existing user details")
    void shouldUpdateUser() {
        //given
        User savedUser = userRepository.save(newUser);
        User userToUpdate = new User(
                savedUser.id(),
                "Johnny",
                "Doe",
                savedUser.email(),
                "789 New Address St",
                false,
                250.0
        );

        //when
        User updatedUser = userRepository.save(userToUpdate);

        //then
        assertThat(updatedUser.id()).isEqualTo(savedUser.id());
        assertThat(updatedUser.name()).isEqualTo("Johnny");
        assertThat(updatedUser.address()).isEqualTo("789 New Address St");
        assertThat(updatedUser.alerting()).isFalse();
        assertThat(updatedUser.energyAlertingThreshold()).isEqualTo(250.0);

        Optional<User> found = userRepository.findById(savedUser.id());
        assertThat(found).isPresent();
        assertThat(found.get().name()).isEqualTo("Johnny");
        assertThat(found.get().address()).isEqualTo("789 New Address St");
        assertThat(found.get().alerting()).isFalse();
        assertThat(found.get().energyAlertingThreshold()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("Should delete user by ID")
    void shouldDeleteUserById() {
        //given
        User savedUser = userRepository.save(newUser);
        Long userId = savedUser.id();

        //when
        userRepository.deleteById(userId);

        //then
        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty();
        assertThat(userRepository.existsById(userId)).isFalse();
    }
}