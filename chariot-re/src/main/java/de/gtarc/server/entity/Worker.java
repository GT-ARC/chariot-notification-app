package de.gtarc.server.entity;

import java.util.ArrayList;
import java.util.List;
import de.gtarc.server.util.CommonDate;

public class Worker {
	public static final String TAG_WORKER = "worker";
	public static final String TAG_ID = "id";
	public static final String TAG_PASSWORD = "password";
	public static final String TAG_NAME = "name";
	public static final String TAG_SURNAME = "surname";
	public static final String TAG_BIRTHDATE = "bdate";
	public static final String TAG_UUID = "uuid";

	private String uuid;
	private String workerID;
	private String password;
	private String name;
	private String surname;
	private CommonDate birthDate;

	private List<String> tasks = new ArrayList<String>();
	private List<String> skills = new ArrayList<String>();

	public Worker(){}
	public Worker(String uuid, String workerId, String password, String name, String surname, CommonDate birthdate) {
		this.uuid = uuid;
		this.workerID = workerId;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.birthDate = birthdate;
	}

	public String getUuid() {
		return uuid;
	}

	public String getWorkerID() {
		return workerID;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public CommonDate getBirthDate() {
		return birthDate;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setBirthDate(CommonDate birthdate) {
		this.birthDate = birthdate;
	}

	public List<String> getTasks() {
		return tasks;
	}

	public void setTasks(List<String> tasks) {
		this.tasks = tasks;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

}
