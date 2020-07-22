package de.gtarc.server.message.request;

import de.gtarc.server.message.IMessage;

public class LogoutMessage implements IMessage {

	public final static String VALUE_HEADER = "logout";

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}
}
