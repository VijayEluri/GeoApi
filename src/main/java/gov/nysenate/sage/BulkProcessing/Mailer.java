package gov.nysenate.sage.BulkProcessing;

import gov.nysenate.sage.model.BulkProcessing.JobProcess;
import gov.nysenate.sage.util.Resource;

import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class Mailer {
	private static final String SMTP_HOST_NAME = Resource.get("hostname");

	private static final String SMTP_PORT = Resource.get("port");
	
	private static final String SMTP_ACCOUNT_USER = Resource.get("user");
	private static final String SMTP_ACCOUNT_PASS = Resource.get("pass");
	
	private static final String STMP_USER = Resource.get("admin.email");
	
	public static void sendMail(String to, String subject, String message, String from, String fromDisplay) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.starttls.enable","false");
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.ssl.enable","false");

		Session session = Session.getDefaultInstance(props,	new javax.mail.Authenticator() {
										protected PasswordAuthentication getPasswordAuthentication() {
											return new PasswordAuthentication(SMTP_ACCOUNT_USER, SMTP_ACCOUNT_PASS);}});
		session.setDebug(false);
		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(from);
		addressFrom.setPersonal(fromDisplay);
		msg.setFrom(addressFrom);
	
		
		StringTokenizer st = new StringTokenizer (to,",");
		
		InternetAddress[] rcps = new InternetAddress[st.countTokens()];
		int idx = 0;
		
		while (st.hasMoreTokens())
		{
			InternetAddress addressTo = new InternetAddress(st.nextToken());
			rcps[idx++] = addressTo;
			
		}
		
		msg.setRecipients(Message.RecipientType.TO,rcps);
		
		msg.setSubject(subject);
		msg.setContent(message, "text/html");
		
		Transport.send(msg);
	}
	
	public static void mailError(Exception e) {
		try {
			sendMail(STMP_USER,
					"bulk upload error",
					e.getMessage(),
					STMP_USER,
					"SAGE Bulk error");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public static void mailError(Exception e, JobProcess jp) {
		try {
			sendMail(STMP_USER,
					"bulk processing complete",
					jp.getContact() + " - " + jp.getClassName() + " - " + jp.getFileName() + "<br/><br/>" + e.getMessage(),
					STMP_USER,
					"SAGE Bulk front-end error");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public static void mailAdminComplete(JobProcess jp) {
		try {
			sendMail(STMP_USER,
					"bulk processing complete",
					jp.getContact() + " - " + jp.getClassName() + " - " + jp.getFileName() + "<br/><br/>",
					STMP_USER,
					"SAGE Bulk processing complete");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public static void mailUserComplete(JobProcess jp) {
		try {
			sendMail(jp.getContact(),
					"SAGE Districting Completed",
					"Your request from " + new Date(jp.getRequestTime()) + " has been completed and can be downloaded at http://sage.nysenate.gov/GeoApi/download/" + jp.getFileName() +
					"<br/><br/>This is an automated message.",
					STMP_USER,
					"SAGE");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
