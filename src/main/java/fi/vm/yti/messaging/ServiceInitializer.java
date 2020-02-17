package fi.vm.yti.messaging;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import fi.vm.yti.messaging.configuration.VersionInformation;
import fi.vm.yti.messaging.service.ContainerNameService;
import fi.vm.yti.messaging.service.UserLookupService;

@Component
public class ServiceInitializer implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceInitializer.class);
    private final VersionInformation versionInformation;
    private final UserLookupService userService;
    private final ContainerNameService containerNameService;

    @Inject
    public ServiceInitializer(final VersionInformation versionInformation,
                              final UserLookupService userService,
                              final ContainerNameService containerNameService) {
        this.versionInformation = versionInformation;
        this.userService = userService;
        this.containerNameService = containerNameService;
    }

    @Override
    public void run(final ApplicationArguments applicationArguments) {
        initialize();
    }

    private void initialize() {
        printLogo();
        LOG.info("*** Updating users. ***");
        userService.updateUsers();
        LOG.info("*** Updating container prefLabels. ***");
        containerNameService.refreshPrefLabels();
        LOG.info("*** Application has started successfully. ***");
    }

    private void printLogo() {
        LOG.info("");
        LOG.info("          __  .__ ");
        LOG.info(" ___.__._/  |_|__|");
        LOG.info("<   |  |\\   __\\  |");
        LOG.info(" \\___  | |  | |  |");
        LOG.info(" / ____| |__| |__|");
        LOG.info(" \\/               ");
        LOG.info("                                             .__                ");
        LOG.info("  _____   ____   ______ ___________     ____ |__| ____    ____  ");
        LOG.info(" /     \\_/ __ \\ /  ___//  ___/\\__  \\   / ___\\|  |/    \\  / ___\\ ");
        LOG.info("|  Y Y  \\  ___/ \\___ \\ \\___ \\  / __ \\_/ /_/  >  |   |  \\/ /_/  >");
        LOG.info("|__|_|  /\\___  >____  >____  >(____  /\\___  /|__|___|  /\\___  / ");
        LOG.info("      \\/     \\/     \\/     \\/      \\//_____/         \\//_____/  ");
        LOG.info("              .__ ");
        LOG.info("_____  ______ |__|");
        LOG.info("\\__  \\ \\____ \\|  |");
        LOG.info(" / __ \\|  |_> >  |");
        LOG.info("(____  /   __/|__|");
        LOG.info("     \\/|__|       ");
        LOG.info(String.format("                --- Version %s starting up. --- ", versionInformation.getVersion()));
        LOG.info("");
    }
}
