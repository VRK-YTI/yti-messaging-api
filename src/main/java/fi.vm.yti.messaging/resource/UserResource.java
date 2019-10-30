package fi.vm.yti.messaging.resource;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.messaging.dto.ErrorModel;
import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.dto.SubscriptionTypeRequestDTO;
import fi.vm.yti.messaging.dto.UserDTO;
import fi.vm.yti.messaging.exception.NotFoundException;
import fi.vm.yti.messaging.exception.UnauthorizedException;
import fi.vm.yti.messaging.exception.YtiMessagingException;
import fi.vm.yti.messaging.security.AuthorizationManager;
import fi.vm.yti.messaging.service.ContainerNameService;
import fi.vm.yti.messaging.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Component
@Path("/v1/user")
@Produces("application/json")
@Tag(name = "User")
public class UserResource {

    private final AuthorizationManager authorizationManager;
    private final UserService userService;
    private final ContainerNameService containerNameService;

    @Inject
    public UserResource(final AuthorizationManager authorizationManager,
                        final UserService userService,
                        final ContainerNameService containerNameService) {
        this.authorizationManager = authorizationManager;
        this.userService = userService;
        this.containerNameService = containerNameService;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Get user subscription information for the logged in user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information for the logged in user with subscriptions."),
        @ApiResponse(responseCode = "401", description = "Authentication failed."),
        @ApiResponse(responseCode = "404", description = "User not found, no subscriptions yet.")
    })
    public Response getUserInformation() {
        if (authorizationManager.canGetUserInformation()) {
            final UserDTO user = userService.findById(authorizationManager.getUserId());
            if (user != null) {
                final Set<ResourceDTO> userResources = user.getResources();
                userResources.forEach(containerResource -> containerResource.setPrefLabel(containerNameService.getPrefLabel(containerResource.getUri())));
                return Response.ok(user).build();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Path("/subscriptiontype")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Sets the user subscription type.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information for the logged in user with subscriptions."),
        @ApiResponse(responseCode = "401", description = "Authentication failed."),
        @ApiResponse(responseCode = "404", description = "User not found.")
    })
    public Response setSubscriptionType(@Parameter(description = "Subscription type request as JSON payload.") @RequestBody final String subscriptionTypeRequest) {
        final SubscriptionTypeRequestDTO subscriptionTypeRequestDto = parseSubscriptionTypeRequestDto(subscriptionTypeRequest);
        final String subscriptionType = subscriptionTypeRequestDto.getSubscriptionType();
        if (authorizationManager.canGetUserInformation()) {
            final UserDTO user = userService.setSubscriptionType(authorizationManager.getUserId(), subscriptionType);
            if (user != null) {
                return Response.ok(user).build();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    private SubscriptionTypeRequestDTO parseSubscriptionTypeRequestDto(final String subscriptionTypeRequestData) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(subscriptionTypeRequestData, new TypeReference<SubscriptionTypeRequestDTO>() {
            });
        } catch (IOException e) {
            throw new YtiMessagingException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Malformed resources in request body!"));
        }
    }
}
