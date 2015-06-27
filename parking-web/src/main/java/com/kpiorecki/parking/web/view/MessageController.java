package com.kpiorecki.parking.web.view;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.kpiorecki.parking.web.util.MessageProvider;

@ManagedBean
@RequestScoped
public class MessageController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private MessageProvider messageProvider;

	private String messageId;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void validateMessageId(FacesContext context, UIComponent component, Object object) throws ValidatorException {
		String messageKey = getMessageKey((String) object);
		if (!messageProvider.containsMessage(messageKey)) {
			logger.warn("did not find messageKey={} in messages bundle", messageKey);

			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Page not found", null);
			throw new ValidatorException(message);
		}
	}

	public String forwardInvalidMessageId() {
		this.messageId = "404";
		return "pretty:message";
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
