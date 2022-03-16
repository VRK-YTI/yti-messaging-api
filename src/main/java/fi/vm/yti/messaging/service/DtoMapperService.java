package fi.vm.yti.messaging.service;

import java.util.List;
import java.util.Set;

import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.dto.UserDTO;
import fi.vm.yti.messaging.entity.Resource;
import fi.vm.yti.messaging.entity.User;

public interface DtoMapperService {

    ResourceDTO mapResource(final Resource resource);

    ResourceDTO mapResource(final List<Resource> resources);

    Set<ResourceDTO> mapResources(final Set<Resource> resources);

    UserDTO mapUser(final User user);

    Set<UserDTO> mapUsers(final Set<User> users);
}
