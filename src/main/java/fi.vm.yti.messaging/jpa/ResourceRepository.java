package fi.vm.yti.messaging.jpa;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fi.vm.yti.messaging.entity.Resource;

@Repository
@Transactional
public interface ResourceRepository extends CrudRepository<Resource, String> {

    Resource findByUri(final String uri);

    Set<Resource> findByApplication(final String application);

    @Query("SELECT uri FROM Resource WHERE application = :applicationIdentifier")
    Set<String> findUrisByApplication(@Param(value = "applicationIdentifier") final String applicationIdentifier);
}
