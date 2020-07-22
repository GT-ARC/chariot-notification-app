package de.gtarc.server.message.response;

import java.util.List;
import java.util.stream.IntStream;

import de.gtarc.server.entity.Skill;
import de.gtarc.server.entity.Task;
import de.gtarc.server.message.IMessage;
import de.gtarc.server.util.CommonDate;

public class TaskInformationMessage implements IMessage {

	public static final String VALUE_HEADER = "task_information";

	public static final String TAG_ID = "id";
	public static final String TAG_NAME = "name";
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_STATUS = "status";
	public static final String TAG_ASSIGNED = "assigned";
	public static final String TAG_REQUIREMENT = "requirement";
	public static final String TAG_DEADLINE = "deadline";

	private String id;
	private String name;
	private String description;
	private String status;
	private String assigned;
	private String deadline;
	private List<String> requirement;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssigned() {
		return assigned;
	}

	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}

//	public String[] getRequirement() {
//		return requirement;
//	}
//
//	public void setRequirement(String[] requirement) {
//		this.requirement = requirement;
//	}
	public List<String> getRequirement() {
		return requirement;
	}

	public void setRequirement(List<String> requirement) {
		this.requirement = requirement;
	}

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void parse(Task task) {
		this.setId(task.getTaskID());
		this.setName(task.getTaskName());
		this.setDescription(task.getTaskDesc());
		this.setStatus(task.getTaskStatus());
		this.setAssigned(task.getAssignedID());
//		this.setRequirement(task.getSkillIds());
//		this.setDeadline(task.getTaskDeadline().toString());
	}
}
