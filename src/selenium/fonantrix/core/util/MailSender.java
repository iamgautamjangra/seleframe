package selenium.fonantrix.core.util;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to send auto generated e-mail.
 *          </p>
 */
public class MailSender {
	private static final Logger logger = LoggerFactory
			.getLogger(MailSender.class.getName());
	static final Properties properties = System.getProperties();

	static final String mailFrom = ConfigurationMap.getProperty("senderEmail");
	static final String password = ConfigurationMap
			.getProperty("senderEmailPassword");
	static final String host = ConfigurationMap
			.getProperty("senderEmailHostName");
	// get the session object
	private static Session session = Session.getDefaultInstance(properties,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailFrom, password);
				}
			});

	/**
	 * Method to send auto generated e-mail with results as attachment.
	 * 
	 * @param outputDirPath
	 *            Path where the results will be stored on local machine.
	 */
	public static void sendMail(String outputDirPath) {
		final String emailTOReciepients = ConfigurationMap
				.getProperty("emailTOReciepients");
		final String emailCCReciepients = ConfigurationMap
				.getProperty("emailCCReciepients");
		String[] to = emailTOReciepients.split(",");
		String[] cc = emailCCReciepients.split(",");
		String zipFileName = ConfigurationMap.getProperty("zipFileName");
		String outputDirectory = outputDirPath;
		String zipFileLocation = String.format("%s%s", outputDirectory,
				zipFileName);
		String xlsFileName = ConfigurationMap.getProperty("xlsReportFileName");
		String[] filesToZip = new String[2];
		filesToZip[0] = xlsFileName;
		ZipFile.makeZipFile(filesToZip, zipFileLocation, outputDirectory);
		try {
			properties.setProperty("mail.smtp.host", host);
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.auth", "true");
			// compose message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailFrom));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			for (int i = 0; i < to.length; i++) {
				toAddress[i] = new InternetAddress(to[i]);
			}
			message.setRecipients(Message.RecipientType.TO, toAddress);
			if (!emailCCReciepients.isEmpty()) {
				InternetAddress[] ccAddress = new InternetAddress[cc.length];
				for (int i = 0; i < cc.length; i++) {
					ccAddress[i] = new InternetAddress(cc[i]);
				}
				message.setRecipients(Message.RecipientType.CC, ccAddress);
			}
			message.setSubject("Campaign Manager Automation Test Result");
			// create MimeBodyPart object and set your message content
			BodyPart messageBody = new MimeBodyPart();
			String bodyPart = "<div style=\"color:#000000;font-size:14px\">Please review the attached report.<br>";

			// bodyPart = bodyPart
			// +"The automation execution is performed with test environment Win7/Mozilla Firefox 30 browser.<br>";

			bodyPart = bodyPart
					+ "*******************************************************************************************<br>";
			bodyPart = bodyPart
					+ "This is an auto-generated e-mail from Fonantrix team. Please do not reply to this auto generated email notification.<br>";
			bodyPart = bodyPart
					+ " It has been sent from an e-mail account that is not monitored. In case of any assistance queries/feedback, please email us at (qa@fonantrix.com).<br>";
			bodyPart = bodyPart
					+ "If you are not the intended recipient: any disclosure, copying, use or distribution of the information included in this message and any attachments is prohibited.";

			bodyPart = bodyPart
					+ "If you have received this communication in error, please notify us by reply e-mail at (qa@fonantrix.com) and permanently delete this message and any attachments.<br>";
			bodyPart = bodyPart
					+ "*******************************************************************************************<br><br>";
			bodyPart = bodyPart + "Regards,<br>";
			bodyPart = bodyPart + " Fonantrix QA team<br></div>";
			// create new MimeBodyPart object and set DataHandler object to
			// this object
			MimeBodyPart messageAttachment = new MimeBodyPart();
			messageBody.setContent(bodyPart, "text/html");
			DataSource source = new FileDataSource(zipFileLocation);
			messageAttachment.setDataHandler(new DataHandler(source));
			messageAttachment.setFileName(zipFileName);
			// create Multipart object and add MimeBodyPart objects to this
			// object
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBody);
			multipart.addBodyPart(messageAttachment);
			// set the multiplart object to the message object
			message.setContent(multipart);
			// send message to reciever
			Transport transport = session.getTransport("smtp");
			transport.connect(host, mailFrom, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("mail send successfully  with attachment");
		} catch (Exception e) {
			logger.error("Exception while sending email and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}
	}
}
