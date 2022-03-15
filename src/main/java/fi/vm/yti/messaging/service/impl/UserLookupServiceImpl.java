package fi.vm.yti.messaging.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.messaging.configuration.GroupManagementProperties;
import fi.vm.yti.messaging.dto.GroupManagementUserDTO;
import fi.vm.yti.messaging.service.UserLookupService;
import static org.springframework.http.HttpMethod.GET;

@Service
public class UserLookupServiceImpl implements UserLookupService {

    private static final String GROUPMANAGEMENT_API_PRIVATE_CONTEXT_PATH = "private-api";
    private static final String GROUPMANAGEMENT_API_USERS = "users";

    private static final Logger LOG = LoggerFactory.getLogger(UserLookupServiceImpl.class);

    private final Map<UUID, GroupManagementUserDTO> users;
    private final GroupManagementProperties groupManagementProperties;
    private final RestTemplate restTemplate;

    @Inject
    public UserLookupServiceImpl(final GroupManagementProperties groupManagementProperties,
                                 final RestTemplate restTemplate) {
        this.groupManagementProperties = groupManagementProperties;
        this.restTemplate = restTemplate;
        users = new HashMap<>();
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void fetchUsers() {
        updateUsers();
    }

    public void updateUsers() {
        final String url = groupManagementProperties.getUrl() + "/" + GROUPMANAGEMENT_API_PRIVATE_CONTEXT_PATH + "/" + GROUPMANAGEMENT_API_USERS;
        LOG.debug("Updating users from GroupManagement URL: " + url);
        final Set<GroupManagementUserDTO> fetchedUsers = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<Set<GroupManagementUserDTO>>() {
        }).getBody();
        if (fetchedUsers != null) {
            LOG.info(String.format("Successfully synced %d users from GroupManagement service!", fetchedUsers.size()));
        }
        Objects.requireNonNull(fetchedUsers).forEach(user -> users.put(user.getId(), user));
    }

    public GroupManagementUserDTO getUserById(final UUID id) {
        return users.get(id);
    }

    public String getUserEmailById(final UUID id) {
        GroupManagementUserDTO user = users.get(id);
        return user != null ? user.getEmail() : null;
    }
}
