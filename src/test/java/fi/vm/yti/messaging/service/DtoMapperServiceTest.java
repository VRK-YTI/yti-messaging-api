package fi.vm.yti.messaging.service;

import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.service.impl.DtoMapperServiceImpl;
import org.junit.jupiter.api.Test;
import fi.vm.yti.messaging.entity.Resource;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DtoMapperServiceTest {

    private DtoMapperService service = new DtoMapperServiceImpl();

    @Test
    public void mapMultipleResourceDtos() {
        var resource1 = new Resource();
        resource1.setApplication("APP");
        resource1.setType("TYPE");
        resource1.setUri("https://uri.suomi.fi/foo");

        var resource2 = new Resource();
        resource2.setApplication("APP");
        resource2.setType("TYPE");
        resource2.setUri("https://uri.suomi.fi/bar");

        ResourceDTO dto = service.mapResource(Arrays.asList(resource1, resource2));

        assertEquals("APP", dto.getApplication());
        assertEquals("TYPE", dto.getType());
        assertEquals("https://uri.suomi.fi/foo,https://uri.suomi.fi/bar", dto.getUri());
    }
}
