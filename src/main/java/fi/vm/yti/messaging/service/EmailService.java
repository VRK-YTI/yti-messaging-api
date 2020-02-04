package fi.vm.yti.messaging.service;

import java.util.UUID;

public interface EmailService {

    void sendMail(final UUID userId,
                  final String message);
}
