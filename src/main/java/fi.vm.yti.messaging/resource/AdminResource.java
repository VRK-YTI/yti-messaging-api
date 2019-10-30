package fi.vm.yti.messaging.resource;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import fi.vm.yti.messaging.exception.UnauthorizedException;
import fi.vm.yti.messaging.security.AuthorizationManager;
import fi.vm.yti.messaging.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Component
@Path("/v1/admin")
@Produces("application/json")
@Tag(name = "Admin")
public class AdminResource {

    private final NotificationService notificationService;
    private final AuthorizationManager authorizationManager;

    public AdminResource(final NotificationService notificationService,
                         final AuthorizationManager authorizationManager) {
        this.notificationService = notificationService;
        this.authorizationManager = authorizationManager;
    }

    @GET
    @Path("/notify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Send e-mail notifications to everyone.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications sent successfully."),
        @ApiResponse(responseCode = "401", description = "Authentication failed.")
    })
    public Response sendNotifications() {
        if (authorizationManager.isSuperUser()) {
            notificationService.sendAllNotifications();
            return Response.ok().build();
        }
        throw new UnauthorizedException();
    }

    @GET
    @Path("/{userId}/notify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Send e-mail notifications to user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications sent successfully."),
        @ApiResponse(responseCode = "401", description = "Authentication failed.")
    })
    public Response sendNotificationsToUser(@Parameter(description = "User ID to be notified.", required = true, in = ParameterIn.QUERY) @PathParam("userId") final UUID userId) {
        if (authorizationManager.isSuperUser()) {
            notificationService.sendUserNotifications(userId);
            return Response.ok().build();
        }
        throw new UnauthorizedException();
    }
}
