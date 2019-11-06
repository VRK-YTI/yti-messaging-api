package fi.vm.yti.messaging.configuration;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Priorities;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import fi.vm.yti.messaging.exception.exceptionmapping.YtiMessagingExceptionMapper;
import fi.vm.yti.messaging.filter.CacheFilter;
import fi.vm.yti.messaging.filter.CharsetResponseFilter;
import fi.vm.yti.messaging.filter.RequestLoggingFilter;
import fi.vm.yti.messaging.resource.AdminResource;
import fi.vm.yti.messaging.resource.PingResource;
import fi.vm.yti.messaging.resource.SubscriptionResource;
import fi.vm.yti.messaging.resource.UserResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@Component
@OpenAPIDefinition(
    info = @Info(
        description = "YTI Messaging - Messaging API - Spring Boot microservice.",
        version = "v1",
        title = "YTI Messaging API",
        termsOfService = "https://opensource.org/licenses/EUPL-1.1",
        contact = @Contact(
            name = "Messaging API by the Population Register Center of Finland",
            url = "https://yhteentoimiva.suomi.fi/",
            email = "yhteentoimivuus@vrk.fi"
        ),
        license = @License(
            name = "EUPL-1.2",
            url = "https://opensource.org/licenses/EUPL-1.1"
        )
    ),
    servers = {
        @Server(
            description = "Messaging API",
            url = "/messaging-api")
    }
)
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        final JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(new CustomObjectMapper());

        // ExceptionMappers
        register(YtiMessagingExceptionMapper.class);

        // Charset filter
        register(CharsetResponseFilter.class, Priorities.AUTHENTICATION);

        // Cache control headers to no cache.
        register(CacheFilter.class, Priorities.AUTHENTICATION);

        // OpenAPI
        register(OpenApiResource.class);

        // Health
        register(PingResource.class);

        // Logging
        register(RequestLoggingFilter.class);

        // API Resources
        register(UserResource.class);
        register(SubscriptionResource.class);
        register(AdminResource.class);
    }
}
