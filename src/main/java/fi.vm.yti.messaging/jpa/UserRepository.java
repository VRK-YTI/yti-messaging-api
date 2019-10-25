package fi.vm.yti.messaging.jpa;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import fi.vm.yti.messaging.entity.User;

public interface UserRepository extends CrudRepository<User, String> {

    User findById(final UUID id);

    Set<User> findAll();
}
