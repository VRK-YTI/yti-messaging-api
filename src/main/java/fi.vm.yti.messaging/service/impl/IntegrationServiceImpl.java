package fi.vm.yti.messaging.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import fi.vm.yti.messaging.configuration.CodelistProperties;
import fi.vm.yti.messaging.configuration.CommentsProperties;
import fi.vm.yti.messaging.configuration.CustomObjectMapper;
import fi.vm.yti.messaging.configuration.DataModelProperties;
import fi.vm.yti.messaging.configuration.TerminologyProperties;
import fi.vm.yti.messaging.dto.ErrorModel;
import fi.vm.yti.messaging.dto.IntegrationResourceRequestDTO;
import fi.vm.yti.messaging.dto.IntegrationResponseDTO;
import fi.vm.yti.messaging.exception.NotFoundException;
import fi.vm.yti.messaging.exception.YtiMessagingException;
import fi.vm.yti.messaging.service.IntegrationService;
import static fi.vm.yti.messaging.api.ApiConstants.*;
import static org.assertj.core.util.DateUtil.now;
import static org.assertj.core.util.DateUtil.yesterday;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationServiceImpl.class);
    private static final String DATE_SUFFIX = "T07:00:00.000+0200";

    private final CodelistProperties codelistProperties;
    private final DataModelProperties dataModelProperties;
    private final TerminologyProperties terminologyProperties;
    private final CommentsProperties commentsProperties;
    private final RestTemplate restTemplate;

    public IntegrationServiceImpl(final CodelistProperties codelistProperties,
                                  final DataModelProperties dataModelProperties,
                                  final TerminologyProperties terminologyProperties,
                                  final CommentsProperties commentsProperties,
                                  final RestTemplate restTemplate) {
        this.codelistProperties = codelistProperties;
        this.dataModelProperties = dataModelProperties;
        this.terminologyProperties = terminologyProperties;
        this.commentsProperties = commentsProperties;
        this.restTemplate = restTemplate;
    }

    public IntegrationResponseDTO getIntegrationContainers(final String applicationIdentifier,
                                                           final Set<String> containerUris) {
        return getIntegrationContainers(applicationIdentifier, containerUris, false, false);
    }

    public IntegrationResponseDTO getIntegrationContainers(final String applicationIdentifier,
                                                           final Set<String> containerUris,
                                                           final boolean fetchDateRangeChanges,
                                                           final boolean getLatest) {
        final String requestUrl = resolveContainersRequestUrl(applicationIdentifier);
        LOG.info("Fetching integration containers from: " + requestUrl);
        final String requestBody = createContainerRequestBody(containerUris, fetchDateRangeChanges, getLatest);
        LOG.info("Fetching integration containers request body: " + requestBody);
        final HttpEntity requestEntity = new HttpEntity<>(requestBody, createRequestHeaders());
        try {
            final ResponseEntity response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseIntegrationResponse(response);
            } else {
                throw new NotFoundException();
            }
        } catch (final Exception e) {
            LOG.error("Fetching integration containers failed for application: " + applicationIdentifier, e.getMessage());
            throw new YtiMessagingException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch integration containers for application: " + applicationIdentifier));
        }
    }

    public IntegrationResponseDTO getIntegrationResources(final String applicationIdentifier,
                                                          final String containerUri,
                                                          final boolean fetchDateRangeChanges,
                                                          final boolean getLatest) {
        final String requestUrl = resolveResourcesRequestUrl(applicationIdentifier);
        LOG.info("Fetching integration resources from: " + requestUrl);
        final String requestBody = createResourcesRequestBody(containerUri, fetchDateRangeChanges, getLatest);
        final HttpEntity requestEntity = new HttpEntity<>(requestBody, createRequestHeaders());
        try {
            final ResponseEntity response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseIntegrationResponse(response);
            } else {
                throw new NotFoundException();
            }
        } catch (final Exception e) {
            LOG.error("Fetching integration resources failed for application: " + applicationIdentifier, e.getMessage());
            throw new YtiMessagingException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch integration resources for application: " + applicationIdentifier));
        }
    }

    private IntegrationResponseDTO parseIntegrationResponse(final ResponseEntity response) {
        final Object responseBody = response.getBody();
        LOG.info("Fetching integration resources: " + responseBody);
        if (responseBody != null) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
                final String data = responseBody.toString();
                return mapper.readValue(data, new TypeReference<IntegrationResponseDTO>() {
                });
            } catch (final IOException e) {
                throw new YtiMessagingException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to parse integration resources!"));
            }
        } else {
            throw new YtiMessagingException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to parse integration resources!"));
        }
    }

    private HttpHeaders createRequestHeaders() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON);
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON);
        return requestHeaders;
    }

    private String createContainerRequestBody(final Set<String> containerUris,
                                              final boolean fetchDateRangeChanges) {
        return createContainerRequestBody(containerUris, fetchDateRangeChanges, false);
    }

    private String createContainerRequestBody(final Set<String> containerUris,
                                              final boolean fetchDateRangeChanges,
                                              final boolean getLatest) {
        final ObjectMapper mapper = new CustomObjectMapper();
        final IntegrationResourceRequestDTO integrationResourceRequestDto = new IntegrationResourceRequestDTO();
        integrationResourceRequestDto.setIncludeIncomplete(true);
        if (fetchDateRangeChanges) {
            setAfterAndBefore(integrationResourceRequestDto, getLatest);
        }
        if (containerUris != null && !containerUris.isEmpty()) {
            integrationResourceRequestDto.setUri(new ArrayList<>(containerUris));
        }
        try {
            return mapper.writeValueAsString(integrationResourceRequestDto);
        } catch (final JsonProcessingException e) {
            throw new YtiMessagingException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Integration request body generation failed due to error: " + e.getMessage()));
        }
    }

    private String createResourcesRequestBody(final String container,
                                              final boolean fetchDateRangeChanges) {
        return createResourcesRequestBody(container, fetchDateRangeChanges, false);
    }

    private String createResourcesRequestBody(final String container,
                                              final boolean fetchDateRangeChanges,
                                              final boolean getLatest) {
        final ObjectMapper mapper = new CustomObjectMapper();
        final IntegrationResourceRequestDTO integrationResourceRequestDto = new IntegrationResourceRequestDTO();
        integrationResourceRequestDto.setIncludeIncomplete(true);
        if (fetchDateRangeChanges) {
            setAfterAndBefore(integrationResourceRequestDto, getLatest);
        }
        if (container != null && !container.isEmpty()) {
            final List<String> containerUris = new ArrayList<>();
            containerUris.add(container);
            integrationResourceRequestDto.setContainer(containerUris);
        }
        integrationResourceRequestDto.setPageFrom(0);
        integrationResourceRequestDto.setPageSize(RESOURCES_PAGE_SIZE);
        try {
            return mapper.writeValueAsString(integrationResourceRequestDto);
        } catch (final JsonProcessingException e) {
            throw new YtiMessagingException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Integration request body generation failed due to error: " + e.getMessage()));
        }
    }

    private void setAfterAndBefore(final IntegrationResourceRequestDTO integrationResourceRequestDto,
                                   final boolean getLatest) {
        final TimeZone tz = TimeZone.getTimeZone("Europe/Helsinki");
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(tz);
        final String after = df.format(yesterday()) + DATE_SUFFIX;
        integrationResourceRequestDto.setAfter(after);
        if (!getLatest) {
            final String before = df.format(now()) + DATE_SUFFIX;
            integrationResourceRequestDto.setBefore(before);
        }
    }

    private String resolveRequestUrl(final String applicationIdentifier,
                                     final String endPoint) {
        switch (applicationIdentifier) {
            case APPLICATION_CODELIST:
                return codelistProperties.getPublicUrl() + PATH_CODELIST_API + PATH_API_WITH_VERSION + endPoint;
            case APPLICATION_DATAMODEL:
                return dataModelProperties.getPublicUrl() + PATH_DATAMODEL_API + PATH_API_WITH_VERSION + endPoint;
            case APPLICATION_TERMINOLOGY:
                return terminologyProperties.getPublicUrl() + PATH_TERMINOLOGY_API + PATH_API_WITH_VERSION + endPoint;
            case APPLICATION_COMMENTS:
                return commentsProperties.getPublicUrl() + PATH_COMMENTS_API + PATH_API_WITH_VERSION + endPoint;
            default:
                throw new YtiMessagingException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Unknown applicationIdentifier: " + applicationIdentifier));
        }
    }

    private String resolveContainersRequestUrl(final String applicationIdentifier) {
        return resolveRequestUrl(applicationIdentifier, PATH_CONTAINERS_API);
    }

    private String resolveResourcesRequestUrl(final String applicationIdentifier) {
        return resolveRequestUrl(applicationIdentifier, PATH_RESOURCES_API);
    }
}
