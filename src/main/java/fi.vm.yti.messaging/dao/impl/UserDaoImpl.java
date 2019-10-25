package fi.vm.yti.messaging.dao.impl;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import fi.vm.yti.messaging.dao.UserDao;
import fi.vm.yti.messaging.entity.User;
import fi.vm.yti.messaging.exception.NotFoundException;
import fi.vm.yti.messaging.jpa.UserRepository;
import fi.vm.yti.messaging.service.impl.SubscriptionType;

@Component
public class UserDaoImpl implements UserDao {

    private UserRepository userRepository;

    @Inject
    public UserDaoImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(final User user) {
        return userRepository.save(user);
    }

    public User findById(final UUID userId) {
        return userRepository.findById(userId);
    }

    public Set<User> findAll() {
        return userRepository.findAll();
    }

    public User getUser(final UUID userId) {
        return userRepository.findById(userId);
    }

    public User getOrCreateUser(final UUID userId) {
        final User existingUser = userRepository.findById(userId);
        if (existingUser != null) {
            return existingUser;
        } else {
            final User user = createUser(userId);
            userRepository.save(user);
            return user;
        }
    }

    public User setSubscriptionType(final UUID userId,
                                    final String subscriptionType) {
        final User existingUser = userRepository.findById(userId);
        if (existingUser != null) {
            existingUser.setSubscriptionType(subscriptionType);
            userRepository.save(existingUser);
            return existingUser;
        }
        throw new NotFoundException();
    }

    private User createUser(final UUID id) {
        final User user = new User();
        user.setId(id);
        user.setSubscriptionType(SubscriptionType.DAILY.toString());
        return user;
    }
}
