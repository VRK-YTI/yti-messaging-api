package fi.vm.yti.messaging.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fi.vm.yti.messaging.api.Meta;
import fi.vm.yti.messaging.configuration.MessagingServiceProperties;
import fi.vm.yti.messaging.dto.IntegrationResourceDTO;
import fi.vm.yti.messaging.dto.IntegrationResponseDTO;
import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.dto.UserDTO;
import fi.vm.yti.messaging.dto.UserNotificationDTO;
import fi.vm.yti.messaging.exception.NotFoundException;
import fi.vm.yti.messaging.exception.NotModifiedException;
import fi.vm.yti.messaging.service.EmailService;
import fi.vm.yti.messaging.service.IntegrationService;
import fi.vm.yti.messaging.service.NotificationService;
import fi.vm.yti.messaging.service.ResourceService;
import fi.vm.yti.messaging.service.UserService;
import static fi.vm.yti.messaging.api.ApiConstants.*;
import static fi.vm.yti.messaging.util.ApplicationUtils.*;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final String SUBSCRIPTION_TYPE_DAILY = "DAILY";

    private static final String LANGUAGE_FI = "fi";
    private static final String LANGUAGE_EN = "en";
    private static final String LANGUAGE_SV = "sv";
    private static final String LANGUAGE_UND = "und";

    private final UserService userService;
    private final ResourceService resourceService;
    private final EmailService emailService;
    private final IntegrationService integrationService;
    private final MessagingServiceProperties messagingServiceProperties;

    @Inject
    public NotificationServiceImpl(final UserService userService,
                                   final ResourceService resourceService,
                                   final EmailService emailService,
                                   final IntegrationService integrationService,
                                   final MessagingServiceProperties messagingServiceProperties) {
        this.userService = userService;
        this.resourceService = resourceService;
        this.emailService = emailService;
        this.integrationService = integrationService;
        this.messagingServiceProperties = messagingServiceProperties;
    }

    @Scheduled(cron = "0 0 7 * * *", zone = "Europe/Helsinki")
    @Transactional
    public void sendAllNotifications() {
        LOG.info("Sending scheduled notifications!");
        final Map<String, IntegrationResourceDTO> updatedResourcesMap = fetchAndMapUpdatedResources();
        final Map<UUID, UserNotificationDTO> userNotifications = mapUserNotifications(updatedResourcesMap);
        sendUserNotifications(userNotifications);
    }

    @Transactional
    public void sendUserNotifications(final UUID userId) {
        final UserDTO user = userService.findById(userId);
        if (user != null && SUBSCRIPTION_TYPE_DAILY.equalsIgnoreCase(user.getSubscriptionType())) {
            final Map<String, IntegrationResourceDTO> updatedResourcesMap = fetchAndMapUpdatedResourcesForUser(userId);
            final UserNotificationDTO userNotification = mapUserNotificationResource(user, updatedResourcesMap);
            if (userNotification != null) {
                sendSingleUserNotifications(user.getId(), userNotification);
            } else {
                throw new NotModifiedException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    private Map<String, IntegrationResourceDTO> fetchAndMapUpdatedResources() {
        return fetchAndMapUpdatedResourcesForUser(null);
    }

    private Map<String, IntegrationResourceDTO> fetchAndMapUpdatedResourcesForUser(final UUID userId) {
        final Map<String, IntegrationResourceDTO> updatedResourcesMap = new HashMap<>();
        final Set<IntegrationResourceDTO> allUpdates = getUpdatedContainersForAllApplications(userId);
        allUpdates.forEach(updatedResource -> updatedResourcesMap.put(updatedResource.getUri(), updatedResource));
        return updatedResourcesMap;
    }

    private Map<UUID, UserNotificationDTO> mapUserNotifications(final Map<String, IntegrationResourceDTO> updatedResourcesMap) {
        final Map<UUID, UserNotificationDTO> userNotifications = new HashMap<>();
        final Set<UserDTO> users = userService.findAll();
        for (final UserDTO user : users) {
            if (SUBSCRIPTION_TYPE_DAILY.equalsIgnoreCase(user.getSubscriptionType())) {
                final UserNotificationDTO userNotificationDto = mapUserNotificationResource(user, updatedResourcesMap);
                if (userNotificationDto != null) {
                    userNotifications.put(user.getId(), userNotificationDto);
                }
            }
        }
        return userNotifications;
    }

    private UserNotificationDTO mapUserNotificationResource(final UserDTO user,
                                                            final Map<String, IntegrationResourceDTO> updatedResourcesMap) {
        final Set<ResourceDTO> resources = user.getResources();
        if (resources != null && !resources.isEmpty()) {
            final Set<IntegrationResourceDTO> codeListUpdates = new HashSet<>();
            final Set<IntegrationResourceDTO> dataModelUpdates = new HashSet<>();
            final Set<IntegrationResourceDTO> terminologyUpdates = new HashSet<>();
            final Set<IntegrationResourceDTO> commentsUpdates = new HashSet<>();
            for (final ResourceDTO resource : resources) {
                final String resourceUri = resource.getUri();
                if (updatedResourcesMap.keySet().contains(resourceUri)) {
                    switch (resource.getApplication()) {
                        case APPLICATION_CODELIST:
                            codeListUpdates.add(updatedResourcesMap.get(resourceUri));
                            break;
                        case APPLICATION_DATAMODEL:
                            dataModelUpdates.add(updatedResourcesMap.get(resourceUri));
                            break;
                        case APPLICATION_TERMINOLOGY:
                            terminologyUpdates.add(updatedResourcesMap.get(resourceUri));
                            break;
                        case APPLICATION_COMMENTS:
                            commentsUpdates.add(updatedResourcesMap.get(resourceUri));
                            break;
                        default:
                            LOG.info("Unknown application type: " + resource.getApplication());
                    }
                }
            }
            UserNotificationDTO userNotificationDto = null;
            if (!codeListUpdates.isEmpty() || !dataModelUpdates.isEmpty() || !terminologyUpdates.isEmpty() || !commentsUpdates.isEmpty()) {
                userNotificationDto = new UserNotificationDTO(codeListUpdates, dataModelUpdates, terminologyUpdates, commentsUpdates);
            }
            return userNotificationDto;
        }
        return null;
    }

    private void sendUserNotifications(final Map<UUID, UserNotificationDTO> userNotifications) {
        userNotifications.keySet().forEach(userId -> {
            sendSingleUserNotifications(userId, userNotifications.get(userId));
        });
    }

    private void sendSingleUserNotifications(final UUID userId,
                                             final UserNotificationDTO userNotificationDto) {
        final String message = constructMessage(userNotificationDto);
        emailService.sendMail(userId, message);
    }

    private String constructMessage(final UserNotificationDTO userNotificationDto) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<body>");
        builder.append("Hyvä käyttäjä,<br/>");
        builder.append("<br/>");
        builder.append("Saat tämän viestin, koska olet tilannut muutosviestit Yhteentoimivuusalustalta yhdestä tai useammasta sisällöstä.");
        builder.append("Voit perua tilauksen työkaluista <a href=\"https://vrk-ewiki.eden.csc.fi/pages/viewpage.action?pageId=21779517\">Käyttäjätiedot</a>-osiossa.");
        builder.append("<br/>");
        final Set<IntegrationResourceDTO> terminologyUpdates = userNotificationDto.getTerminologyResources();
        final Set<IntegrationResourceDTO> codelistUpdates = userNotificationDto.getCodelistResources();
        final Set<IntegrationResourceDTO> datamodelUpdates = userNotificationDto.getDatamodelResouces();
        final Set<IntegrationResourceDTO> commentsUpdates = userNotificationDto.getCommentsResources();
        if (!terminologyUpdates.isEmpty()) {
            builder.append("<h3>Sanastot</h3>");
            addResourceUpdates(APPLICATION_TERMINOLOGY, builder, terminologyUpdates);
        }
        if (!codelistUpdates.isEmpty()) {
            builder.append("<h3>Koodistot</h3>");
            addResourceUpdates(APPLICATION_CODELIST, builder, codelistUpdates);
        }
        if (!datamodelUpdates.isEmpty()) {
            builder.append("<h3>Tietomallit</h3>");
            addResourceUpdates(APPLICATION_DATAMODEL, builder, datamodelUpdates);
        }
        if (!commentsUpdates.isEmpty()) {
            builder.append("<h3>Kommentit</h3>");
            addResourceUpdates(APPLICATION_COMMENTS, builder, commentsUpdates);
        }
        builder.append("<br/>");
        builder.append("<br/>");
        builder.append("Tämä viesti on lähetetty automaattisesti. Ethän vastaa viestiin!");
        builder.append("</body>");
        return builder.toString();
    }

    private void addResourceUpdates(final String applicationIdentifier,
                                    final StringBuilder builder,
                                    final Set<IntegrationResourceDTO> resources) {
        resources.forEach(resource -> {
            addResourceToBuilder(false, applicationIdentifier, builder, resource);
            final IntegrationResponseDTO subResourceResponse = resource.getSubResourceResponse();
            if (subResourceResponse != null) {
                final Set<IntegrationResourceDTO> subResources = subResourceResponse.getResults();
                if (subResources != null && !subResources.isEmpty()) {
                    final Set<IntegrationResourceDTO> subResourcesWithStatusChanges = new HashSet<>();
                    final Set<IntegrationResourceDTO> subResourcesWithContentChanges = new HashSet<>();
                    subResources.forEach(subResource -> {
                        final Date statusModified = subResource.getStatusModified();
                        final Date statusModifiedComparisonDate = createAfterDateForModifiedComparison();
                        if (statusModified != null && (statusModified.after(statusModifiedComparisonDate) || statusModified.equals(statusModifiedComparisonDate))) {
                            subResourcesWithStatusChanges.add(subResource);
                        } else {
                            subResourcesWithContentChanges.add(subResource);
                        }
                    });
                    addSubResourcesWithStatusChanges(applicationIdentifier, builder, subResourcesWithStatusChanges);
                    addSubResourcesWithContentChanges(applicationIdentifier, builder, subResourcesWithContentChanges);
                    final Meta meta = subResourceResponse.getMeta();
                    if (meta != null && meta.getTotalResults() > RESOURCES_PAGE_SIZE) {
                        appendTotalSubResources(builder, meta.getTotalResults());
                    }
                } else {
                    appendInformationChanged(builder, resource.getType());
                }
            } else {
                appendInformationChanged(builder, resource.getType());
            }
        });
    }

    private void appendTotalSubResources(final StringBuilder builder,
                                         final int count) {
        builder.append("<ul>");
        builder.append("<li>");
        builder.append(count);
        builder.append(" muutosta yhteensä");
        builder.append("</li>");
        builder.append("</ul>");
    }

    private void appendInformationChanged(final StringBuilder builder,
                                          final String type) {
        builder.append("<ul>");
        builder.append("<li>");
        final String typeLabel = resolveLocalizationForType(type);
        builder.append(typeLabel);
        builder.append(" tiedot ovat muuttuneet");
        builder.append("</li>");
        builder.append("</ul>");
    }

    private String resolveLocalizationForType(final String type) {
        if (type == null) {
            return "aineiston";
        }
        switch (type) {
            case TYPE_TERMINOLOGY:
                return "sanaston";
            case TYPE_CODELIST:
                return "koodiston";
            case TYPE_LIBRARY:
                return "tietokomponenttikirjaston";
            case TYPE_PROFILE:
                return "soveltamisprofiilin";
            case TYPE_COMMENTROUND:
                return "kommentointikierroksen";
            case TYPE_COMMENTTHREAD:
                return "kommentointiketjun";
            default:
                return "aineiston";
        }
    }

    private void addSubResourcesWithStatusChanges(final String applicationIdentifier,
                                                  final StringBuilder builder,
                                                  final Set<IntegrationResourceDTO> resources) {
        if (resources != null && !resources.isEmpty()) {
            builder.append("<ul>");
            builder.append("<li>muuttuneet tiedot ja tilat</li>");
            builder.append("<ul>");
            resources.forEach(resource -> addResourceToBuilder(true, applicationIdentifier, builder, resource));
            builder.append("</ul>");
            builder.append("</ul>");
        }
    }

    private void addSubResourcesWithContentChanges(final String applicationIdentifier,
                                                   final StringBuilder builder,
                                                   final Set<IntegrationResourceDTO> resources) {
        if (resources != null && !resources.isEmpty()) {
            builder.append("<ul>");
            builder.append("<li>muuttuneet tiedot</li>");
            builder.append("<ul>");
            resources.forEach(resource -> addResourceToBuilder(true, applicationIdentifier, builder, resource));
            builder.append("</ul>");
            builder.append("</ul>");
        }
    }

    private void addResourceToBuilder(final boolean wrapToList,
                                      final String applicationIdentifier,
                                      final StringBuilder builder,
                                      final IntegrationResourceDTO resource) {
        final String prefLabel = getPrefLabelValueForEmail(resource.getPrefLabel());
        final String resourceUri = resource.getUri();
        if (wrapToList) {
            builder.append("<li>");
        }
        builder.append("<a href=");
        builder.append(embedEnvironmentToUri(resourceUri));
        builder.append(">");
        if (prefLabel != null) {
            builder.append(prefLabel);
        } else {
            builder.append(resourceUri);
        }
        builder.append("</a>");
        final Date statusModified = resource.getStatusModified();
        final Date statusModifiedComparisonDate = createAfterDateForModifiedComparison();
        if (statusModified != null && (statusModified.after(statusModifiedComparisonDate) || statusModified.equals(statusModifiedComparisonDate))) {
            builder.append(": " + localizeStatus(resource.getStatus()));
        }
        if (APPLICATION_COMMENTS.equalsIgnoreCase(applicationIdentifier) && TYPE_COMMENTTHREAD.equalsIgnoreCase(resource.getType())) {
            final Date contentModified = resource.getContentModified();
            final Date contentModifiedComparisonDate = createAfterDateForModifiedComparison();
            if (contentModified != null && (contentModified.after(contentModifiedComparisonDate) || contentModified.equals(contentModifiedComparisonDate))) {
                builder.append(" tietosisältöön on tullut uusia kommentteja");
            }
        }
        if (wrapToList) {
            builder.append("</li>");
        }
    }

    private String getPrefLabelValueForEmail(final Map<String, String> prefLabel) {
        if (prefLabel != null) {
            if (prefLabel.get(LANGUAGE_FI) != null) {
                return prefLabel.get(LANGUAGE_FI);
            } else if (prefLabel.get(LANGUAGE_EN) != null) {
                return prefLabel.get(LANGUAGE_EN);
            } else if (prefLabel.get(LANGUAGE_SV) != null) {
                return prefLabel.get(LANGUAGE_SV);
            } else if (prefLabel.get(LANGUAGE_UND) != null) {
                return prefLabel.get(LANGUAGE_UND);
            } else {
                return prefLabel.get(0);
            }
        }
        return null;
    }

    private String localizeStatus(final String status) {
        switch (status) {
            case "VALID":
                return "Voimassa oleva";
            case "INCOMPLETE":
                return "Keskeneräinen";
            case "DRAFT":
                return "Luonnos";
            case "SUGGESTED":
                return "Ehdotus";
            case "SUPERSEDED":
                return "Korvattu";
            case "RETIRED":
                return "Poistettu käytöstä";
            case "INVALID":
                return "Virheellinen";
            case "INPROGRESS":
                return "Käynnissä";
            case "AWAIT":
                return "Odottaa";
            case "ENDED":
                return "Päättynyt";
            case "CLOSED":
                return "Suljettu";
            default:
                return status;
        }
    }

    private String embedEnvironmentToUri(final String uri) {
        final String env = messagingServiceProperties.getEnv();
        if ("awsprod".equalsIgnoreCase(env)) {
            return uri;
        } else {
            return uri + "?env=" + env;
        }
    }

    private Set<IntegrationResourceDTO> getUpdatedContainersForAllApplications(final UUID userId) {
        final Set<IntegrationResourceDTO> updatedResources = new HashSet<>();
        addUpdatedContainersForApplication(APPLICATION_CODELIST, updatedResources, userId);
        addUpdatedContainersForApplication(APPLICATION_DATAMODEL, updatedResources, userId);
        addUpdatedContainersForApplication(APPLICATION_TERMINOLOGY, updatedResources, userId);
        addUpdatedContainersForApplication(APPLICATION_COMMENTS, updatedResources, userId);
        return updatedResources;
    }

    private void addUpdatedContainersForApplication(final String applicationIdentifier,
                                                    final Set<IntegrationResourceDTO> updatedResources,
                                                    final UUID userId) {
        final Set<IntegrationResourceDTO> resources;
        if (userId != null) {
            resources = getUpdatedApplicationContainersForUserId(applicationIdentifier, userId);
        } else {
            resources = getUpdatedApplicationContainers(applicationIdentifier);
        }
        if (resources != null && !resources.isEmpty()) {
            updatedResources.addAll(resources);
        }
    }

    private Set<IntegrationResourceDTO> getUpdatedApplicationContainersForUserId(final String applicationIdentifier,
                                                                                 final UUID userId) {
        final Set<String> containerUris = resourceService.getResourceUrisForApplicationAndUserId(applicationIdentifier, userId);
        if (containerUris != null && !containerUris.isEmpty()) {
            return getUpdatedApplicationContainersWithUris(applicationIdentifier, containerUris, true);
        }
        return null;
    }

    private Set<IntegrationResourceDTO> getUpdatedApplicationContainers(final String applicationIdentifier) {
        final Set<String> containerUris = resourceService.getResourceUrisForApplication(applicationIdentifier);
        if (containerUris != null && !containerUris.isEmpty()) {
            return getUpdatedApplicationContainersWithUris(applicationIdentifier, containerUris);
        }
        return null;
    }

    private Set<IntegrationResourceDTO> getUpdatedApplicationContainersWithUris(final String applicationIdentifier,
                                                                                final Set<String> containerUris) {
        return getUpdatedApplicationContainersWithUris(applicationIdentifier, containerUris, false);

    }

    private Set<IntegrationResourceDTO> getUpdatedApplicationContainersWithUris(final String applicationIdentifier,
                                                                                final Set<String> containerUris,
                                                                                final boolean getLatest) {
        LOG.info("Fetching containers for: " + applicationIdentifier);
        if (containerUris != null && !containerUris.isEmpty()) {
            final IntegrationResponseDTO integrationResponse = integrationService.getIntegrationContainers(applicationIdentifier, containerUris, true, getLatest);
            final Set<IntegrationResourceDTO> containers = integrationResponse.getResults();
            if (containers != null && !containers.isEmpty()) {
                LOG.info("Found " + containers.size() + " for application: " + applicationIdentifier);
                containers.forEach(container -> {
                    final Date contentModified = container.getContentModified();
                    final Date contentModifiedComparisonDate = createAfterDateForModifiedComparison();
                    final IntegrationResponseDTO integrationResponseForResources;
                    if (applicationIdentifier.equalsIgnoreCase(APPLICATION_TERMINOLOGY) || (contentModified != null && (contentModified.after(contentModifiedComparisonDate) || contentModified.equals(contentModifiedComparisonDate)))) {
                        LOG.info("Container: " + container.getUri() + " has content that has been modified lately, fetching resources.");
                        integrationResponseForResources = integrationService.getIntegrationResources(applicationIdentifier, container.getUri(), true, getLatest);
                        LOG.info("Resources for " + applicationIdentifier + " have " + integrationResponseForResources.getResults().size() + " updates.");
                        container.setSubResourceResponse(integrationResponseForResources);
                    }
                });
                return containers;
            } else {
                LOG.info("No containers have updates for " + applicationIdentifier);
            }
        }
        return null;
    }
}
