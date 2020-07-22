package de.gtarc.server.message.request;

import de.gtarc.server.message.IMessage;

import java.util.List;

public class WorkerRequestMessage implements IMessage {

	public static final String VALUE_HEADER = "worker_request";
	public static final String TAG_ID = "id";
	public static final String TAG_PASSWORD = "password";
	public static final String TAG_NAME = "name";
	public static final String TAG_SURNAME = "surname";
	public static final String TAG_BIRTHDATE = "birthdate";
	public static final String TAG_ACQUIRED = "acquired";

	private String workerID;
	private String password;
	private String name;
	private String surname;
	private String birthdate;
	private List<String> acquiredSkills;

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public String getWorkerID() {
		return workerID;
	}

	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public List<String> getAcquiredSkills() {
		return acquiredSkills;
	}

	public void setAcquiredSkills(List<String> acquiredSkills) {
		this.acquiredSkills = acquiredSkills;
	}
}
