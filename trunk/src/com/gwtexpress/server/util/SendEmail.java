package com.gwtexpress.server.util;

import com.google.gwt.user.client.rpc.SerializableException;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendEmail {

    public static void sendEmail(java.util.ArrayList<String> toList, String subject, 
                          String body) throws SerializableException {
        Message message = new MimeMessage(getSession());
        try {
            for (String email : toList) {
                message.addRecipient(Message.RecipientType.TO, 
                                     new InternetAddress(email));

            }
            message.addFrom(new InternetAddress[] { new InternetAddress("gwtexpress@gmail.com") });
            message.setSubject(subject);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Error sending email", e);
        }
    }


    private static Session getSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "localhost");
        properties.setProperty("mail.smtp.port", "25");
        return Session.getInstance(properties, null);
    }

}
