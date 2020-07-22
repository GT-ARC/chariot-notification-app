package de.gtarc.server.entity;

import de.gtarc.server.util.CommonDate;

public class HealthData {
	public static final String TAG_HEALTH_DATA = "health_data";
	public static final String TAG_WORKER_ID = "worker_id";
	public static final String TAG_MEASUREMENT_TIME = "measurement_time";
	public static final String TAG_HEART_RATE = "heart_rate";
	public static final String TAG_STEP_STATUS = "step_status";
	public static final String TAG_FEEL_STATUS = "feel_status";
	public static final String TAG_WORK_STATUS = "work_status";

	private String workerId;
	private CommonDate time;
	private int heartRate;
	private String stepStatus;
	private String feelStatus;
	private String workStatus;

	public HealthData(String workerId, CommonDate time, int heartRate, String stepStatus, String feelStatus,
			String workStatus) {
		this.workerId = workerId;
		this.time = time;
		this.heartRate = heartRate;
		this.stepStatus = stepStatus;
		this.feelStatus = feelStatus;
		this.workStatus = workStatus;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public CommonDate getTime() {
		return time;
	}

	public void setTime(CommonDate time) {
		this.time = time;
	}

	public int getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(int heartRate) {
		this.heartRate = heartRate;
	}

	public String getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}

	public String getFeelStatus() {
		return feelStatus;
	}

	public void setFeelStatus(String feelStatus) {
		this.feelStatus = feelStatus;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	@Override
	public String toString() {
		return "HealthData{" +
				"workerId='" + workerId + '\'' +
				", time=" + time +
				", heartRate=" + heartRate +
				", stepStatus='" + stepStatus + '\'' +
				", feelStatus='" + feelStatus + '\'' +
				", workStatus='" + workStatus + '\'' +
				'}';
	}
}