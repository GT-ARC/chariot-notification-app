package de.gtarc.server.message.request;

import de.gtarc.server.message.IMessage;

public class HealthDataMessage implements IMessage {

	public static final String VALUE_HEADER = "health_data";
	public static final String TAG_TIME = "time";
	public static final String TAG_HEART_RATE = "heart_rate";
	public static final String TAG_STEP_STATUS = "step_status";
	public static final String TAG_FEEL_STATUS = "feel_status";
	public static final String TAG_WORK_STATUS = "work_status";

	private String time;
	private String heartRate;
	private String stepStatus;
	private String workStatus;
	private String feelStatus;

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(String heartRate) {
		this.heartRate = heartRate;
	}

	public String getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getFeelStatus() {
		return feelStatus;
	}

	public void setFeelStatus(String feelStatus) {
		this.feelStatus = feelStatus;
	}

	@Override
	public String toString() {
		return time + " " + heartRate + " " + stepStatus;
	}
}
