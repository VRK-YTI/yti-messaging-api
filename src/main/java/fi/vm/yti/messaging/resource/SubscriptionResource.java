package fi.vm.yti.messaging.resource;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.messaging.dto.ErrorModel;
import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.dto.SubscriptionRequestDTO;
import fi.vm.yti.messaging.exception.NoContentException;
import fi.vm.yti.messaging.exception.UnauthorizedException;
import fi.vm.yti.messaging.exception.YtiMessagingException;
import fi.vm.yti.messaging.security.AuthorizationManager;
import fi.vm.yti.messaging.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Component
@Path("/v1/subscriptions")
@Produces("application/json")
@Tag(name = "Subscription")
public class SubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionResource.class);

    private final static String ACTION_GET = "GET";
    private final static String ACTION_ADD = "ADD";
    private final static String ACTION_DELETE = "DELETE";

    private final AuthorizationManager authorizationManager;
    private final SubscriptionService subscriptionService;

    @Inject
    public SubscriptionResource(final AuthorizationManager authorizationManager,
                                final SubscriptionService subscriptionService) {
        this.authorizationManager = authorizationManager;
        this.subscriptionService = subscriptionService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Gets, adds or deletes the user subscription to a given URI resource.")
    @ApiResponse(responseCode = "200", description = "Returns the resource either found or created.")
    @ApiResponse(responseCode = "204", description = "No subscription for given resource.")
    @ApiResponse(responseCode = "401", description = "Authentication failed.")
    public Response postSubscription(@Parameter(description = "Subscription request as JSON payload.") @RequestBody final String subscriptionRequest) {
        final SubscriptionRequestDTO subscriptionRequestDto = parseSubscriptionRequestDto(subscriptionRequest);
        final String uri = subscriptionRequestDto.getUri();
        final String type = subscriptionRequestDto.getType();
        final String action = subscriptionRequestDto.getAction();
        final ResourceDTO resource;
        final UUID userId = authorizationManager.getUserId();
        if (userId != null) {
            switch (action) {
                case ACTION_GET:
                    if (authorizationManager.canAddSubscription()) {
                        resource = subscriptionService.getSubscription(uri, authorizationManager.getUserId());
                    } else {
                        throw new UnauthorizedException();
                    }
                    break;
                case ACTION_ADD:
                    if (authorizationManager.canAddSubscription()) {
                        resource = subscriptionService.addSubscription(uri, type, authorizationManager.getUserId());
                    } else {
                        throw new UnauthorizedException();
                    }
                    break;
                case ACTION_DELETE:
                    if (authorizationManager.canAddSubscription()) {
                        resource = subscriptionService.deleteSubscription(uri, authorizationManager.getUserId());
                    } else {
                        throw new UnauthorizedException();
                    }
                    break;
                default:
                    throw new YtiMessagingException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Unsupported action found in request payload: " + type));
            }
        } else {
            resource = null;
        }
        if (resource != null) {
            return Response.ok(resource).build();
        } else {
            // TODO: Return 204, not 500 not found!
            LOG.debug("Resource not found for user: " + userId + " , returning 204 for uri: " + uri);
            throw new NoContentException();
        }
    }

    private SubscriptionRequestDTO parseSubscriptionRequestDto(final String subscriptionRequestData) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(subscriptionRequestData, new TypeReference<SubscriptionRequestDTO>() {
            });
        } catch (IOException e) {
            throw new YtiMessagingException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Malformed resources in request body!"));
        }
    }
}
