package com.aries.tiger.smackappender;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmackAppender extends AppenderSkeleton {
	
	// config, hard code to gtalk
	private ConnectionConfiguration config = new ConnectionConfiguration(
			"talk.google.com", 5222, "gmail.com");
	
	private String senderName;
	private String senderPassword;
	
	private String receiverName;
	
	XMPPConnection connection = null;
	
	Chat chat = null;
	public SmackAppender(){
		config.setSASLAuthenticationEnabled(false);
		connection = new XMPPConnection(config);
	}
	
	@Override
	public void activateOptions(){
		try {
			connection.connect();
			connection.login(senderName, senderPassword);
		} catch (XMPPException e) {
			errorHandler.error("Error login with username: " + senderName + " and password: " + senderPassword);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void append(LoggingEvent event) {
		if(chat == null){
			chat = connection.getChatManager().createChat(receiverName, new MessageListener(){

				@Override
				public void processMessage(Chat arg0, Message arg1) {
					// do nothing
				}
				
			});
		}
		try {
			chat.sendMessage(layout.format(event));
		} catch (XMPPException e) {
			errorHandler.error("Error send message.");
		}
	}
	
	@Override
	public void close() {
		connection.disconnect();
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderPassword() {
		return senderPassword;
	}
	public void setSenderPassword(String senderPassword) {
		this.senderPassword = senderPassword;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	
	
}
