package Tools.Mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import Tools.PropertiesReader;


public class MailTools {

    private Properties prop = System.getProperties();
    private Session sess;

    public MailTools() {
        CreateSession();
    }


    private void CreateSession() {
        prop.put("mail.smtp.host", PropertiesReader.getProperties("SERVERENVOIE"));
        prop.put("mail.smtp.port", PropertiesReader.getProperties("PORTENVOIE"));
        prop.put("mail.from", PropertiesReader.getProperties("MAIL"));
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.starttls.required", "true");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.debug", "true");
        prop.put("file.encoding", "iso-8859-1");
        prop.put("mail.smtp.ssl.trust", PropertiesReader.getProperties("SERVERENVOIE"));

        sess = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(PropertiesReader.getProperties("MAIL"), PropertiesReader.getProperties("PWD"));
            }
        });
    }

    public void SendMail(String mailTO, String mailObject, String text)
    throws MessagingException {

        Multipart   multipart = new MimeMultipart();
        MimeMessage msg       = new MimeMessage(sess);

        msg.setFrom(new InternetAddress(PropertiesReader.getProperties("MAIL")));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTO));
        msg.setSubject(mailObject);
        //msg.setText (messageTextArea.getText());

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(text);
        multipart.addBodyPart(messageBodyPart);

        msg.setContent(multipart);

        System.out.println("Envoi du message");
        Transport.send(msg);
        //JOptionPane.showMessageDialog(null, "Message envoyé !");

    }
}
