package org.ecp.backend.service;

public interface MailService {
    void sendMail(String from, String fromName, String to, String subject, String text);
}
