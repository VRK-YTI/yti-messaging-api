package fi.vm.yti.messaging.dao.impl;

import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import fi.vm.yti.messaging.dao.ResourceDao;
import fi.vm.yti.messaging.entity.Resource;
import fi.vm.yti.messaging.jpa.ResourceRepository;
import fi.vm.yti.messaging.service.ContainerNameService;
import static fi.vm.yti.messaging.util.ApplicationUtils.getApplicationByType;

@Component
public class ResourceDaoImpl implements ResourceDao {

    private final ResourceRepository resourceRepository;
    private final ContainerNameService containerNameService;

    public ResourceDaoImpl(final ResourceRepository resourceRepository,
                           @Lazy final ContainerNameService containerNameService) {
        this.resourceRepository = resourceRepository;
        this.containerNameService = containerNameService;
    }

    public Resource getOrCreateResource(final String uri,
                                        final String type) {
        final Resource existingResource = resourceRepository.findByUri(uri);
        if (existingResource != null) {
            return existingResource;
        } else {
            containerNameService.addPrefLabelToUriWithType(uri, type);
            final Resource resource = createResource(uri, type);
            resourceRepository.save(resource);
            return resource;
        }
    }

    public Set<Resource> findByApplication(final String applicationIdentifier) {
        return resourceRepository.findByApplication(applicationIdentifier);
    }

    public Set<String> findUrisByApplication(final String applicationIdentifier) {
        return resourceRepository.findUrisByApplication(applicationIdentifier);
    }

    public Set<String> findUrisByApplicationAndUserId(final String applicationIdentifier,
                                                      final UUID userId) {
        return resourceRepository.findUrisByApplicationAndUserId(applicationIdentifier, userId);
    }

    private Resource createResource(final String uri,
                                    final String type) {
        final Resource resource = new Resource();
        resource.setUri(uri);
        resource.setType(type);
        resource.setApplication(getApplicationByType(type));
        return resource;
    }
}
