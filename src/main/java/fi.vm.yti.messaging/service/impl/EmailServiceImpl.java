package fi.vm.yti.messaging.service.impl;

import java.util.UUID;

import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import fi.vm.yti.messaging.service.EmailService;
import fi.vm.yti.messaging.service.UserLookupService;
import static javax.mail.Message.RecipientType.TO;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final UserLookupService userLookupService;
    private final JavaMailSender javaMailSender;

    @Inject
    public EmailServiceImpl(final UserLookupService userLookupService,
                            final JavaMailSender javaMailSender) {
        this.userLookupService = userLookupService;
        this.javaMailSender = javaMailSender;
    }

    private static Address createAddress(final String emailAddress) {
        try {
            return new InternetAddress(emailAddress);
        } catch (final AddressException e) {
            LOG.error("createAddress failed for " + emailAddress);
            throw new RuntimeException(e);
        }
    }

    public void sendMail(final UUID userId,
                         final String message) {
        final String emailAddress = userLookupService.getUserEmailById(userId);
        try {
            if (emailAddress != null && !emailAddress.endsWith("localhost")) {
                LOG.info("Sending email to: " + userId);
                LOG.info("Email message: " + message);
                final MimeMessage mail = javaMailSender.createMimeMessage();
//                mail.setRecipient(TO, createAddress(emailAddress));
                mail.setRecipient(TO, createAddress("antti.tohmo@gmail.com"));
                mail.setFrom("y-alusta.tuotetiimi@vrk.fi");
                mail.setSubject("Yhteentoimivuusalustan päivittyneet aineistot");
                mail.setContent(message, "text/html");
                javaMailSender.send(mail);
            } else {
                LOG.info("Not sending e-mail to a localhost user: " + emailAddress);
            }
        } catch (final MessagingException e) {
            LOG.error("Email sending failed due to " + e.getMessage());
        }
    }
}
