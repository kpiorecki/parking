package com.kpiorecki.parking.web.view;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.kpiorecki.parking.web.util.MessageProvider;

@Named
@RequestScoped
public class MessageController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private MessageProvider messageProvider;

	private String messageId;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTitle() {
		String message = messageProvider.getMessage(getTitleKey(messageId));
		if (message == null) {
			// title is optional - by default "Parking" is returned
			return "Parking";
		}
		return message;
	}

	public String getMessage() {
		return messageProvider.getMessage(getMessageKey(messageId));
	}

	private String getMessageKey(String messageId) {
		return String.format("MessageController.message.%s", messageId);
	}

	private String getTitleKey(String messageId) {
		return String.format("MessageController.title.%s", messageId);
	}

}
