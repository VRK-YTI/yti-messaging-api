package fi.vm.yti.messaging.dao;

import java.util.Set;

import fi.vm.yti.messaging.entity.Resource;

public interface ResourceDao {

    Resource findByUri(final String uri);

    Set<Resource> findByApplication(final String application);

    Set<String> findUrisByApplication(final String application);

    Resource getOrCreateResource(final String uri,
                                 final String type);
}
