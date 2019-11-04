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
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fi.vm.yti.messaging.configuration.MessagingServiceProperties;
import fi.vm.yti.messaging.dto.ErrorModel;
import fi.vm.yti.messaging.dto.IntegrationResourceDTO;
import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.dto.UserDTO;
import fi.vm.yti.messaging.dto.UserNotificationDTO;
import fi.vm.yti.messaging.exception.NotFoundException;
import fi.vm.yti.messaging.exception.YtiMessagingException;
import fi.vm.yti.messaging.service.EmailService;
import fi.vm.yti.messaging.service.IntegrationService;
import fi.vm.yti.messaging.service.NotificationService;
import fi.vm.yti.messaging.service.ResourceService;
import fi.vm.yti.messaging.service.UserService;
import static fi.vm.yti.messaging.api.ApiConstants.*;
import static fi.vm.yti.messaging.util.ApplicationUtils.*;
import static org.assertj.core.util.DateUtil.yesterday;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

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

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Helsinki")
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
        if (user != null) {
            // TODO: Optimize and check only this users resources for updates!
            final Map<String, IntegrationResourceDTO> updatedResourcesMap = fetchAndMapUpdatedResources();
            final UserNotificationDTO userNotification = mapUserNotificationResource(user, updatedResourcesMap);
            if (userNotification != null) {
                sendSingleUserNotifications(user.getId(), userNotification);
            }
        } else {
            throw new NotFoundException();
        }
    }

    private Map<String, IntegrationResourceDTO> fetchAndMapUpdatedResources() {
        final Map<String, IntegrationResourceDTO> updatedResourcesMap = new HashMap<>();
        final Set<IntegrationResourceDTO> allUpdates = getUpdatedResourcesForAllApplications();
        allUpdates.forEach(updatedResource -> updatedResourcesMap.put(updatedResource.getUri(), updatedResource));
        return updatedResourcesMap;
    }

    private Map<UUID, UserNotificationDTO> mapUserNotifications(final Map<String, IntegrationResourceDTO> updatedResourcesMap) {
        final Map<UUID, UserNotificationDTO> userNotifications = new HashMap<>();
        final Set<UserDTO> users = userService.findAll();
        for (final UserDTO user : users) {
            final UserNotificationDTO userNotificationDto = mapUserNotificationResource(user, updatedResourcesMap);
            if (userNotificationDto != null) {
                userNotifications.put(user.getId(), userNotificationDto);
            }
        }
        return userNotifications;
    }

    private UserNotificationDTO mapUserNotificationResource(final UserDTO user,
                                                            final Map<String, IntegrationResourceDTO> updatedResourcesMap) {
        final Set<ResourceDTO> resources = user.getResources();
        if (resources != null && !resources.isEmpty()) {
            final Set<IntegrationResourceDTO> codeListUdpdates = new HashSet<>();
            final Set<IntegrationResourceDTO> dataModelUpdates = new HashSet<>();
            final Set<IntegrationResourceDTO> terminologyUpdates = new HashSet<>();
            final Set<IntegrationResourceDTO> commentsUpdates = new HashSet<>();
            for (final ResourceDTO resource : resources) {
                final String resourceUri = resource.getUri();
                if (updatedResourcesMap.keySet().contains(resourceUri)) {
                    switch (resource.getApplication()) {
                        case APPLICATION_CODELIST:
                            codeListUdpdates.add(updatedResourcesMap.get(resourceUri));
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
            if (!codeListUdpdates.isEmpty() || !dataModelUpdates.isEmpty() || !terminologyUpdates.isEmpty()) {
                userNotificationDto = new UserNotificationDTO(codeListUdpdates, dataModelUpdates, terminologyUpdates, commentsUpdates);
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
            addResourceToBuilder(false, applicationIdentifier, null, builder, resource);
            final Set<IntegrationResourceDTO> subResources = resource.getResources();
            if (subResources != null && !subResources.isEmpty()) {
                final Set<IntegrationResourceDTO> subResourcesWithStatusChanges = new HashSet<>();
                final Set<IntegrationResourceDTO> subResourcesWithContentChanges = new HashSet<>();
                subResources.forEach(subResource -> {
                    final Date statusModified = subResource.getStatusModified();
                    if (statusModified != null && statusModified.after(yesterday())) {
                        subResourcesWithStatusChanges.add(subResource);
                    } else {
                        subResourcesWithContentChanges.add(subResource);
                    }
                });
                addSubResourcesWithStatusChanges(applicationIdentifier, resource.getUri(), builder, subResourcesWithStatusChanges);
                addSubResourcesWithContentChanges(applicationIdentifier, resource.getUri(), builder, subResourcesWithContentChanges);
            } else {
                builder.append("<ul>");
                builder.append("<li>");
                final String typeLabel = resolveLocalizationForType(resource.getType());
                builder.append(typeLabel);
                builder.append(" tiedot ovat muuttuneet");
                builder.append("</li>");
                builder.append("</ul>");
            }
        });
    }

    private String resolveLocalizationForType(final String type) {
        if (type == null) {
            return "aineiston";
        }
        switch (type) {
            case TYPE_VOCABULARY:
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
                                                  final String containerUri,
                                                  final StringBuilder builder,
                                                  final Set<IntegrationResourceDTO> resources) {
        if (resources != null && !resources.isEmpty()) {
            builder.append("<ul>");
            builder.append("<li>muuttuneet tiedot ja tilat</li>");
            builder.append("<ul>");
            resources.forEach(resource -> addResourceToBuilder(true, applicationIdentifier, containerUri, builder, resource));
            builder.append("</ul>");
            builder.append("</ul>");
        }
    }

    private void addSubResourcesWithContentChanges(final String applicationIdentifier,
                                                   final String containerUri,
                                                   final StringBuilder builder,
                                                   final Set<IntegrationResourceDTO> resources) {
        if (resources != null && !resources.isEmpty()) {
            builder.append("<ul>");
            builder.append("<li>muuttuneet tiedot</li>");
            builder.append("<ul>");
            resources.forEach(resource -> addResourceToBuilder(true, applicationIdentifier, containerUri, builder, resource));
            builder.append("</ul>");
            builder.append("</ul>");
        }
    }

    private void addResourceToBuilder(final boolean wrapToList,
                                      final String applicationIdentifier,
                                      final String containerUri,
                                      final StringBuilder builder,
                                      final IntegrationResourceDTO resource) {
        final String prefLabel = getPrefLabelValueForEmail(resource.getPrefLabel());
        final String resourceUri = resource.getUri();
        if (wrapToList) {
            builder.append("<li>");
        }
        builder.append("<a href=");
        if (applicationIdentifier.equalsIgnoreCase(APPLICATION_COMMENTS)) {
            if (resource.getType().equalsIgnoreCase(TYPE_COMMENTROUND)) {
                builder.append(constructCommentRoundUri(resourceUri));
            } else if (resource.getType().equalsIgnoreCase(TYPE_COMMENTTHREAD)) {
                builder.append(constructCommentThreadUri(containerUri, resourceUri));
            }
        } else {
            builder.append(embedEnvironmentToUri(resourceUri));
        }
        builder.append(">");
        if (prefLabel != null) {
            builder.append(prefLabel);
        } else {
            builder.append(resourceUri);
        }
        builder.append("</a>");
        final Date statusModified = resource.getStatusModified();
        if (statusModified != null && resource.getStatusModified().after(yesterday())) {
            builder.append(": " + localizeStatus(resource.getStatus()));
        }
        if (applicationIdentifier.equalsIgnoreCase(APPLICATION_COMMENTS) && resource.getType().equals(TYPE_COMMENTTHREAD)) {
            if (resource.getContentModified().after(yesterday())) {
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
            default:
                return status;
        }
    }

    private String constructCommentRoundUri(final String commentRoundId) {
        final String env = messagingServiceProperties.getEnv();
        switch (env) {
            case "awsprod":
                return "https://kommentit.suomi.fi/commentround;commentRoundId=" + commentRoundId;
            case "awstest":
                return "https://kommentit.test.yti.cloud.vrk.fi/commentround;commentRoundId=" + commentRoundId;
            case "awsdev":
                return "https://kommentit.dev.yti.cloud.vrk.fi/commentround;commentRoundId=" + commentRoundId;
            case "local":
                return "http://localhost:9700/commentround;commentRoundId=" + commentRoundId;
            default:
                throw new YtiMessagingException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to resolve comments url address for unknown environment: " + env));
        }
    }

    private String constructCommentThreadUri(final String commentRoundId,
                                             final String commentThreadId) {
        final String env = messagingServiceProperties.getEnv();
        switch (env) {
            case "awsprod":
                return "https://kommentit.suomi.fi/commentround;commentRoundId=" + commentRoundId + ";commentThreadId=" + commentThreadId;
            case "awstest":
                return "https://kommentit.test.yti.cloud.vrk.fi/commentround;commentRoundId=" + commentRoundId + ";commentThreadId=" + commentThreadId;
            case "awsdev":
                return "https://kommentit.dev.yti.cloud.vrk.fi/commentround;commentRoundId=" + commentRoundId + ";commentThreadId=" + commentThreadId;
            case "local":
                return "http://localhost:9700/commentround;commentRoundId=" + commentRoundId + ";commentThreadId=" + commentThreadId;
            default:
                throw new YtiMessagingException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to resolve comments url address for unknown environment: " + env));
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

    private Set<IntegrationResourceDTO> getUpdatedResourcesForAllApplications() {
        final Set<IntegrationResourceDTO> updatedResources = new HashSet<>();
        updatedResources.addAll(getUpdatedApplicationResources(APPLICATION_CODELIST));
        updatedResources.addAll(getUpdatedApplicationResources(APPLICATION_DATAMODEL));
        updatedResources.addAll(getUpdatedApplicationResources(APPLICATION_TERMINOLOGY));
        updatedResources.addAll(getUpdatedApplicationResources(APPLICATION_COMMENTS));
        return updatedResources;
    }

    private Set<IntegrationResourceDTO> getUpdatedApplicationResources(final String applicationIdentifier) {
        final Set<String> containerUris = resourceService.getResourceUrisForApplication(applicationIdentifier);
        final Set<IntegrationResourceDTO> containers = integrationService.getIntegrationContainers(applicationIdentifier, containerUris, true);
        if (containers != null && !containers.isEmpty()) {
            LOG.info("Found " + containers.size() + " for application " + applicationIdentifier);
            containers.forEach(container -> {
                final Date contentModified = container.getContentModified();
                if (contentModified != null && contentModified.after(yesterday())) {
                    LOG.info("Container: " + container.getUri() + " has content that has been modified lately, fetch resources");
                    final Set<IntegrationResourceDTO> resources = integrationService.getIntegrationResources(applicationIdentifier, container.getUri(), true);
                    LOG.info("Resources for " + applicationIdentifier + " have " + resources.size() + " updates.");
                    container.setResources(resources);
                }
            });
        } else {
            LOG.info("No containers have updates for " + applicationIdentifier);
        }
        return containers;
    }
}
