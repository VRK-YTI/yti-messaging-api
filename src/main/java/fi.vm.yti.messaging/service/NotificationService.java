package fi.vm.yti.messaging.service;

import java.util.UUID;

public interface NotificationService {

    void sendNotifications();

    void sendUserNotifications(final UUID userId);
}
