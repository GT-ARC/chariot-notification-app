package de.gtarc.server.message.request;

import de.gtarc.server.message.IMessage;

public class InformationRequestMessage implements IMessage {

	public final static String VALUE_HEADER = "information_request";
	public final static String TAG_TYPE = "type";
	public final static String TAG_ID = "id";

	public final static String VALUE_TYPE_WORKER = "worker";
	public final static String VALUE_TYPE_SKILL = "skill";
	public final static String VALUE_TYPE_TASK = "task";

	private String infoType;
	private String infoId;

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}
}
