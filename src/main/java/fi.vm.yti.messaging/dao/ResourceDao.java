package fi.vm.yti.messaging.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.messaging.entity.Resource;

public interface ResourceDao {

    Set<Resource> findByApplication(final String applicationIdenfier);

    Set<String> findUrisByApplication(final String applicationIdenfier);

    Set<String> findUrisByApplicationAndUserId(final String applicationIdenfier,
                                               final UUID userId);

    Resource getOrCreateResource(final String uri,
                                 final String type);
}
