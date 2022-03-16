package fi.vm.yti.messaging.service;

import fi.vm.yti.messaging.dao.ResourceDao;
import fi.vm.yti.messaging.dao.UserDao;
import fi.vm.yti.messaging.dao.impl.ResourceDaoImpl;
import fi.vm.yti.messaging.dao.impl.UserDaoImpl;
import fi.vm.yti.messaging.dto.ResourceDTO;
import fi.vm.yti.messaging.entity.Resource;
import fi.vm.yti.messaging.entity.User;
import fi.vm.yti.messaging.jpa.ResourceRepository;
import fi.vm.yti.messaging.service.impl.DtoMapperServiceImpl;
import fi.vm.yti.messaging.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({
        UserServiceImpl.class,
        ResourceDaoImpl.class,
        DtoMapperServiceImpl.class
})
public class UserServiceTest {

    @MockBean
    UserDaoImpl userDao;

    @MockBean
    ResourceDao resourceDao;

    @Autowired
    UserServiceImpl userService;

    @Test
    public void deleteSubscription() {
        UUID uuid = UUID.randomUUID();

        User user = getUser(uuid);
        when(userDao.findById(eq(uuid))).thenReturn(user);

        ResourceDTO resourceDTO = userService.deleteResourceFromUser("http://uri1", uuid);

        Assertions.assertEquals("http://uri1", resourceDTO.getUri());
    }

    @Test
    public void deleteMultipleSubscriptions() {
        UUID uuid = UUID.randomUUID();

        User user = getUser(uuid);
        when(userDao.findById(eq(uuid))).thenReturn(user);

        ResourceDTO resourceDTO = userService.deleteResourceFromUser("http://uri1,http://uri2", uuid);

        Assertions.assertEquals("http://uri1,http://uri2", resourceDTO.getUri());
    }

    private User getUser(UUID uuid) {
        User user = new User();

        Resource r1 = new Resource();
        Resource r2 = new Resource();

        r1.setUri("http://uri1");
        r2.setUri("http://uri2");
        Set<Resource> resources = new HashSet<>();
        resources.add(r1);
        resources.add(r2);
        user.setId(uuid);
        user.setResources(resources);

        return user;
    }
}
