package fi.vm.yti.messaging.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.messaging.entity.User;

public interface UserDao {

    User save(final User user);

    User findById(final UUID userId);

    Set<User> findAll();

    User getUser(final UUID user);

    User getOrCreateUser(final UUID user);

    User setSubscriptionType(final UUID user,
                             final String subscriptionType);
}
