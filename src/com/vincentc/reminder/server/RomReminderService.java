package com.vincentc.reminder.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class RomReminderService extends HttpServlet {
	private final static String SENDER_EMAIL = "wanqiufeng@gmail.com";
	private final static String URL = "http://get.cm/?device=i9100&type=RC";
	private final static List<String> NOTIFY_EMAIL_LIST = new ArrayList<String>();
	static {
		NOTIFY_EMAIL_LIST.add("wanqiufeng@outlook.com");
		NOTIFY_EMAIL_LIST.add("vincentc@synnex.com");
	}

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		super.service(arg0, arg1);
		String result = getQueryResult(URL);
		if (hasROM(result)) {
			sendMail();
		}
	}

	private void sendMail() throws IOException {
		MailServiceFactory factory = new MailServiceFactory();
		MailService service = factory.getMailService();
		service.send(getMessage());
	}

	private Message getMessage() {
		MailService.Message msg = new MailService.Message();
		msg.setSender(SENDER_EMAIL);
		msg.setTo(NOTIFY_EMAIL_LIST);
		msg.setSubject("new CM rom for I9100(intl) has been released");
		msg.setTextBody("you can download the newest rom via '" + URL + "'");
		return msg;
	}

	private boolean hasROM(String result) {
		return StringUtils.isNotBlank(result);
	}

	private String getQueryResult(String url2) throws MalformedURLException,
			IOException {
		URLFetchServiceFactory factory = new URLFetchServiceFactory();
		URLFetchService service = factory.getURLFetchService();
		HTTPResponse response = service.fetch(new URL(url2));
		String str = new String(response.getContent());
		return StringUtils.substringBetween(str, "<tbody>", "</tbody>");
	}
}
