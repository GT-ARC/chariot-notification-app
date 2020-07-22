package de.gtarc.server.message.request;

import de.gtarc.server.message.IMessage;

public class LoginRequestMessage implements IMessage {

	public final static String VALUE_HEADER = "login";
	public final static String TAG_USERNAME = "username";
	public final static String TAG_PASSWORD = "password";
	public final static String TAG_DEVICE_TYPE = "type";
	public final static String TAG_DEVICE_ID = "id";

	public final static String VALUE_TYPE_SMARTWATCH = "smartwatch";
	public final static String VALUE_TYPE_OTHER = "other";

	private String username;
	private String password;
	private String deviceType;
	private String deviceId;

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
