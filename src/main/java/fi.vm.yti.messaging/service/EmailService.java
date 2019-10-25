package fi.vm.yti.messaging.service;

public interface EmailService {

    void sendMail(final String email,
                  final String message);
}
