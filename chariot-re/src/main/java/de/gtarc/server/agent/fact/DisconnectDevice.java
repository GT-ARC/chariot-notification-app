package de.gtarc.server.agent.fact;

import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.gtarc.server.util.DeviceType;

public class DisconnectDevice implements IFact {

	private static final long serialVersionUID = -2688776283459783558L;

	private String uuid;
	private DeviceType type;
	private String deviceUuid;

	public DisconnectDevice(String uuid, DeviceType type, String deviceUuid) {
		this.uuid = uuid;
		this.type = type;
		this.setDeviceUuid(deviceUuid);
	}

	public String getUserUuid() {
		return uuid;
	}

	public void setUserUuid(String uuid) {
		this.uuid = uuid;
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public String getDeviceUuid() {
		return deviceUuid;
	}

	public void setDeviceUuid(String deviceUuid) {
		this.deviceUuid = deviceUuid;
	}
}
