package de.gtarc.server.message.response;

import de.gtarc.server.message.IMessage;

public class ResponseMessage implements IMessage {

	public final static String VALUE_HEADER = "response";
	public final static String TAG_CODE = "code";
	public final static String TAG_DETAIL = "detail";

	public final static int LOGIN_SUCCESSFUL = 100;
	public final static int LOGIN_FAILED = 101;
	public final static int CONNECTION_ESTABLISHED = 102;
	public final static int CONNECTION_REJECTED = 103;
	public final static int EXECUTION_COMPLETED = 104;
	public final static int EXECUTION_REJECTED = 105;

	private int code;
	private String detail;

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}