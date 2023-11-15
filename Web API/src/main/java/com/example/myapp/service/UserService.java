package com.example.myapp.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myapp.constant.RolesConstants;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.exception.EmailAlreadyUsedException;
import com.example.myapp.exception.UsernameAlreadyUsedException;
import com.example.myapp.repository.RoleRepository;
import com.example.myapp.repository.UserRepository;
import com.example.myapp.util.RandomUtil;
import com.example.myapp.util.SecurityUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

//    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(String filter, Pageable pageable) {
        if (filter != null && !filter.isEmpty()) {
            return userRepository.findAllWithFilter(filter, pageable).map(UserDTO::new);
        } else {
            return userRepository.findAll(pageable).map(UserDTO::new);
        }
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserWithRolesByLogin(String login) {
        return userRepository.findOneWithRolesByLogin(login).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithRoles() {
        return SecurityUtil.getCurrentUserLogin().flatMap(userRepository::findOneWithRolesByLogin);
    }

    @Transactional(readOnly = true)
    public List<String> getRoles() {
        return roleRepository.findAll().stream().map(Role::getCode).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFullname(userDTO.getFullname());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setActivated(true);
        user.setPhoneNumber(userDTO.getPhoneNumber());
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setImageUrl(userDTO.getImageUrl());

        if (userDTO.getRoles() != null) {
            List<Role> roles = userDTO.getRoles().stream().map(roleRepository::findOneByCode)
                    .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            user.setRoles(roles);
        }

        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return new UserDTO(user);
    }

    public void deleteUser(String email) {
        userRepository.findOneByEmailIgnoreCase(email).ifPresent(user -> {
            userRepository.delete(user);
            log.debug("Deleted User: {}", user);
        });
    }

    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository.findById(userDTO.getId())).filter(Optional::isPresent).map(Optional::get)
                .map(user -> {
                    user.setLogin(userDTO.getLogin().toLowerCase());
                    user.setFullname(userDTO.getFullname());
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail().toLowerCase());
                    }
                    user.setPhoneNumber(userDTO.getPhoneNumber());
                    user.setImageUrl(userDTO.getImageUrl());
                    user.setActivated(userDTO.isActivated());
                    List<Role> managedRoles = user.getRoles();
                    managedRoles.clear();
                    userDTO.getRoles().stream().map(roleRepository::findOneByCode).filter(Optional::isPresent)
                            .map(Optional::get).forEach(managedRoles::add);

                    userRepository.save(user);
                    log.debug("Changed Information for User: {}", user);
                    return user;
                }).map(UserDTO::new);
    }

    public void updateUser(String fullname, String email, String phoneNumber, String imageUrl) {
        SecurityUtil.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(user -> {
            user.setFullname(fullname);
            if (email != null) {
                user.setEmail(email.toLowerCase());
            }
            user.setPhoneNumber(phoneNumber);
            user.setImageUrl(imageUrl);
            userRepository.save(user);
            log.debug("Changed Information for User: {}", user);
        });
    }

    public User registerUser(UserDTO userDTO, String password) {
        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });

        userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });

        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());

        newUser.setPassword(encryptedPassword);
        newUser.setEmail(userDTO.getEmail().toLowerCase());
        newUser.setFullname(userDTO.getFullname());

        newUser.setActivated(false);

        newUser.setActivationKey(RandomUtil.generateActivationKey());

        List<Role> roles = new ArrayList<>();
        roleRepository.findOneByCode(RolesConstants.USER).ifPresent(roles::add);
        newUser.setRoles(roles);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtil.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(user -> {
            String currentEncryptedPassword = user.getPassword();
            if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                throw new com.example.myapp.resource.errors.InvalidPasswordException();
            }
            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);
            log.debug("Changed password for User: {}", user);
        });
    }

    public Optional<User> requestPasswordReset(String email) {
        return userRepository.findOneByEmailIgnoreCase(email).filter(User::isActivated).map(user -> {
            user.setResetKey(RandomUtil.generateResetKey());
            user.setResetDate(Instant.now());
            return user;
        });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS))).map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    return user;
                });
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key).map(user -> {
            // activate given user for the registration key.
            user.setActivated(true);
            user.setActivationKey(null);
            log.debug("Activated user: {}", user);
            return user;
        });
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
                .forEach(user -> {
                    log.debug("Deleting not activated user {}", user.getEmail());
                    userRepository.delete(user);
                });
    }


    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

}
