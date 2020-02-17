package fi.vm.yti.messaging.service;

import java.util.UUID;

import fi.vm.yti.messaging.dto.GroupManagementUserDTO;

public interface UserLookupService {

    void updateUsers();

    GroupManagementUserDTO getUserById(final UUID id);

    String getUserEmailById(final UUID id);
}
