package fi.vm.yti.messaging.service;

import java.util.UUID;

public interface NotificationService {

    void sendAllNotifications();

    void sendUserNotifications(final UUID userId);
}
