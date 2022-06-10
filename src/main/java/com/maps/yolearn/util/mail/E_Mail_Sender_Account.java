package com.maps.yolearn.util.mail;

import com.maps.yolearn.constants.Constants;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * @author KOTARAJA
 */
public class E_Mail_Sender_Account {

    Properties props;

    {
        props = new Properties();
        props.put("mail.smtp.host", Constants.MAIL_HOST);
        props.put("mail.smtp.auth", "true");
    }

    private Session getSession() {
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Constants.MAIL_USERNAME, Constants.MAIL_PASSWORD);
            }
        });
        return session;
    }

    /**
     * MAIL API WITHOUT ATTACHMENT
     *
     * @param subject
     * @param to
     * @param message
     * @throws AddressException
     * @throws MessagingException
     */
    public void composeAndSend(String subject, Set<String> to, String message) throws AddressException, MessagingException {
        MimeMessage mimeMessage = new MimeMessage(this.getSession());
        mimeMessage.setFrom(new InternetAddress(Constants.MAIL_USERNAME));
        mimeMessage.setSubject(subject);
//        mimeMessage.setText(message);
        mimeMessage.setContent(message, "text/html;charset=utf-8");

        String[] tos = new String[to.size()];
        tos = to.toArray(tos);
        InternetAddress[] toAddrs = new InternetAddress[tos.length];
        for (int i = 0; i < tos.length; i++) {
            String addr = tos[i];
            InternetAddress internetAddress = new InternetAddress(addr.trim());
            toAddrs[i] = internetAddress;
        }
        mimeMessage.setRecipients(Message.RecipientType.TO, toAddrs);

        Transport.send(mimeMessage);
    }

    /**
     * MAIL API WITH ATTACHMENT
     *
     * @param subject
     * @param to
     * @param message
     * @param in
     * @param fileName
     */
    public void composeAndSends(String subject, Set<String> to, String message, InputStream in,
                                InputStream in2, String fileName, String fileName2) {
        try {
            MimeMessage mimeMessage = new MimeMessage(this.getSession());
            mimeMessage.setFrom(new InternetAddress(Constants.MAIL_USERNAME));
            mimeMessage.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(message, "text/html");

            multipart.addBodyPart(messageBodyPart);


            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(in, "application/pdf");
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(fileName);

            MimeBodyPart attachmentBodyPart2 = new MimeBodyPart();
            DataSource source2 = new ByteArrayDataSource(in2, "application/mp4");
            attachmentBodyPart2.setDataHandler(new DataHandler(source2));
            attachmentBodyPart2.setFileName(fileName2);

            multipart.addBodyPart(attachmentBodyPart);
            multipart.addBodyPart(attachmentBodyPart2);

            String[] tos = new String[to.size()];
            tos = to.toArray(tos);
            InternetAddress[] toAddrs = new InternetAddress[tos.length];
            for (int i = 0; i < tos.length; i++) {
                String addr = tos[i];
                InternetAddress internetAddress = new InternetAddress(addr.trim());
                toAddrs[i] = internetAddress;
            }
            mimeMessage.setRecipients(Message.RecipientType.TO, toAddrs);
            mimeMessage.setContent(multipart);

            Transport.send(mimeMessage);
        } catch (IOException | MessagingException e) {
        }
    }
//    public void composeAndSends(String subject, Set<String> to, String message, InputStream in, String fileName) {
//        try {
//            MimeMessage mimeMessage = new MimeMessage(this.getSession());
//            mimeMessage.setFrom(new InternetAddress(Constants.MAIL_USERNAME));
//            mimeMessage.setSubject(subject);
//
//            Multipart multipart = new MimeMultipart();
//
//            MimeBodyPart messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setContent(message, "text/html");
//
//            multipart.addBodyPart(messageBodyPart);
//
//            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
//            DataSource source = new ByteArrayDataSource(in, "application/pdf");
//            attachmentBodyPart.setDataHandler(new DataHandler(source));
//            attachmentBodyPart.setFileName(fileName);
//
//            multipart.addBodyPart(attachmentBodyPart);
//
//            String[] tos = new String[to.size()];
//            tos = to.toArray(tos);
//            InternetAddress[] toAddrs = new InternetAddress[tos.length];
//            for (int i = 0; i < tos.length; i++) {
//                String addr = tos[i];
//                InternetAddress internetAddress = new InternetAddress(addr.trim());
//                toAddrs[i] = internetAddress;
//            }
//            mimeMessage.setRecipients(Message.RecipientType.TO, toAddrs);
//            mimeMessage.setContent(multipart);
//
//            Transport.send(mimeMessage);
//        } catch (IOException | MessagingException e) {
//        }
//    }
}
