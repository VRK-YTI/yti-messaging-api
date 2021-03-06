package fi.vm.yti.messaging.jpa;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.messaging.entity.Resource;

@Repository
@Transactional
public interface ResourceRepository extends CrudRepository<Resource, String> {

    Resource findByUri(final String uri);

    Set<Resource> findByApplication(final String application);

    @Query("SELECT uri FROM Resource WHERE application = :applicationIdentifier")
    Set<String> findUrisByApplication(@Param(value = "applicationIdentifier") final String applicationIdentifier);

    @Query(value = "SELECT uri FROM resource AS r WHERE application = :applicationIdentifier AND r.uri IN (SELECT resource_uri FROM user_resource WHERE user_id = :userId)", nativeQuery = true)
    Set<String> findUrisByApplicationAndUserId(@Param(value = "applicationIdentifier") final String applicationIdentifier,
                                               @Param(value = "userId") final UUID userId);
}
