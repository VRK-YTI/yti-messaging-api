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

import fi.vm.yti.messaging.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Component
@Path("/v1/admin")
@Produces("application/json")
public class AdminResource {

    private final NotificationService notificationService;

    public AdminResource(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GET
    @Path("/notify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Send e-mail notifications to everyone.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications sent successfully."),
        @ApiResponse(responseCode = "404", description = "User not found.")
    })
    public Response sendNotifications() {
        notificationService.sendAllNotifications();
        return Response.ok().build();
    }

    @GET
    @Path("/{userId}/notify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Send e-mail notifications to user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications sent successfully."),
        @ApiResponse(responseCode = "404", description = "User not found.")
    })
    public Response sendNotificationsToUser(@Parameter(description = "User ID to be notified.", required = true, in = ParameterIn.QUERY) @PathParam("userId") final UUID userId) {
        notificationService.sendUserNotifications(userId);
        return Response.ok().build();
    }
}
