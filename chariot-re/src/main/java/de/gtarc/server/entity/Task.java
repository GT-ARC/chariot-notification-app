package de.gtarc.server.entity;

import de.gtarc.server.util.CommonDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

public class Task {
	public static final String TAG_TASK = "task";
	public static final String TAG_ID = "id";
	public static final String TAG_NAME = "name";
	public static final String TAG_DESC = "description";
	public static final String TAG_STATUS = "status";
	public static final String TAG_ASSIGNED = "assigned";
	public static final String TAG_DEADLINE = "deadline";
	public static final String EMPTY = "";

	public static final String VALUE_STATUS_UNASSIGNED = "unassigned";
	public static final String VALUE_STATUS_ASSIGNED = "assigned";
	public static final String VALUE_STATUS_DONE = "done";

	private String taskId = "";
	private String taskName = "";
	private String taskDesc = "";
	private String taskStatus = "";
	private String assignedID = "";
	private Date taskDeadline;

	private List<String> skillIds = null; // list of skill Ids

	public Task(String taskID, String taskName, String taskDesc, String taskStatus, String assignedID, CommonDate taskDeadline) {
		this.taskId = taskID;
		this.taskName = taskName;
		this.taskDesc = taskDesc;
		this.taskStatus = taskStatus;
		this.assignedID = assignedID;
		this.taskDeadline = taskDeadline.getDate();
	}
	public Task(String taskID, String taskName, String taskDesc, String taskStatus, String assignedID, List<String> skillIds) {
		this.taskId = taskID;
		this.taskName = taskName;
		this.taskDesc = taskDesc;
		this.taskStatus = taskStatus;
		this.assignedID = assignedID;
		this.skillIds = skillIds;
	}

	public Task(String taskID, String taskName, String taskDesc, String taskStatus, String assignedID, CommonDate taskDeadline, List<String> skillIds) {
		this.taskId = taskID;
		this.taskName = taskName;
		this.taskDesc = taskDesc;
		this.taskStatus = taskStatus;
		this.assignedID = assignedID;
		this.taskDeadline = taskDeadline.getDate();
		this.skillIds = skillIds;

	}
	public List<String > getSkillIds(){
		return skillIds;
	}
	public String getSkillIdsAsString(){
		if(skillIds != null){
			return String.join(", ", skillIds);
		}
		return EMPTY;
	}

	public String getTaskID() {
		return taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public String getAssignedID() {
		if (assignedID == null)
			assignedID = VALUE_STATUS_UNASSIGNED;
		return assignedID;
	}

	public Date getTaskDeadline() {
		return taskDeadline;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public void setAssignedID(String assignedID) {
		this.assignedID = assignedID;
	}

	public void setTaskDeadline(CommonDate taskDeadline) {
		this.taskDeadline = taskDeadline.getDate();
	}
}
