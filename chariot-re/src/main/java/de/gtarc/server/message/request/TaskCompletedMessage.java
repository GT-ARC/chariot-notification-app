package de.gtarc.server.message.request;

import de.gtarc.server.message.IMessage;

public class TaskCompletedMessage implements IMessage {

	public static final String VALUE_HEADER = "task_completed";
	public static final String TAG_ID = "id";

	private String id;

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
