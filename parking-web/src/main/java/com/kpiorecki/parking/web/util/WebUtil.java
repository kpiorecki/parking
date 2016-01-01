package com.kpiorecki.parking.web.util;

import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.omnifaces.util.Utils;
import org.slf4j.Logger;

import com.kpiorecki.parking.web.MessageController;

@Stateless
public class WebUtil {

	@Inject
	private Logger logger;

	@Inject
	private ExternalContext externalContext;

	@Inject
	private MessageController messageController;

	public String navigateToMessage(String messageId) {
		messageController.setMessageId(messageId);
		return "/WEB-INF/view/message.xhtml";
	}

	public String getContextRootURL() {
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		String requestURL = request.getRequestURL().toString();
		String rootURL = requestURL.substring(0, requestURL.length() - request.getRequestURI().length());
		return rootURL + request.getContextPath();
	}

	public String encode(String parameter) {
		return Utils.serializeURLSafe(parameter);
	}

	public String decode(String encodedParameter) {
		try {
			return Utils.unserializeURLSafe(encodedParameter);
		} catch (IllegalArgumentException e) {
			logger.warn("could not decode parameter {} - returning null", encodedParameter);
			return null;
		}
	}

}
