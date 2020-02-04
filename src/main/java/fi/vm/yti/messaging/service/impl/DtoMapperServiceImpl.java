package fi.vm.yti.messaging.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.dto.UserDTO;
import fi.vm.yti.messaging.entity.Resource;
import fi.vm.yti.messaging.entity.User;
import fi.vm.yti.messaging.service.DtoMapperService;

@Service
public class DtoMapperServiceImpl implements DtoMapperService {

    public Set<ResourceDTO> mapResources(final Set<Resource> resources) {
        final Set<ResourceDTO> resourceDtos = new HashSet<>();
        if (resources != null && !resources.isEmpty()) {
            resources.forEach(resource -> resourceDtos.add(mapResource(resource)));
        }
        return resourceDtos;
    }

    public ResourceDTO mapResource(final Resource resource) {
        if (resource != null) {
            final ResourceDTO resourceDto = new ResourceDTO();
            resourceDto.setUri(resource.getUri());
            resourceDto.setApplication(resource.getApplication());
            resourceDto.setType(resource.getType());
            return resourceDto;
        }
        return null;
    }

    public Set<UserDTO> mapUsers(final Set<User> users) {
        final Set<UserDTO> userDtos = new HashSet<>();
        if (users != null && !users.isEmpty()) {
            users.forEach(user -> userDtos.add(mapUser(user)));
        }
        return userDtos;
    }

    public UserDTO mapUser(final User user) {
        if (user != null) {
            final UserDTO userDto = new UserDTO();
            userDto.setId(user.getId());
            userDto.setSubscriptionType(user.getSubscriptionType());
            userDto.setResources(mapResources(user.getResources()));
            return userDto;
        }
        return null;
    }
}
