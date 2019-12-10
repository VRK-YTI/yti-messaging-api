package fi.vm.yti.messaging.service.impl;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.messaging.dao.ResourceDao;
import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.entity.Resource;
import fi.vm.yti.messaging.service.DtoMapperService;
import fi.vm.yti.messaging.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceDao resourceDao;
    private final DtoMapperService dtoMapperService;

    @Inject
    public ResourceServiceImpl(final ResourceDao resourceDao,
                               final DtoMapperService dtoMapperService) {
        this.resourceDao = resourceDao;
        this.dtoMapperService = dtoMapperService;
    }

    @Transactional
    public ResourceDTO getOrCreateResource(final String uri,
                                           final String type) {
        final Resource resource = resourceDao.getOrCreateResource(uri, type);
        return dtoMapperService.mapResource(resource);
    }

    @Transactional
    public Set<ResourceDTO> getResourcesForApplication(final String application) {
        return dtoMapperService.mapResources(resourceDao.findByApplication(application));
    }

    @Transactional
    public Set<String> getResourceUrisForApplication(final String application) {
        return resourceDao.findUrisByApplication(application);
    }

    @Transactional
    public Set<String> getResourceUrisForApplicationAndUserId(final String application,
                                                              final UUID userId) {
        return resourceDao.findUrisByApplicationAndUserId(application, userId);
    }
}
