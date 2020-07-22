package de.gtarc.server.message.request;

import de.gtarc.server.message.IMessage;

import java.util.List;

public class TaskRequestMessage implements IMessage {

	private static final long serialVersionUID = -719850697949476399L;

	public static final String VALUE_HEADER = "task_request";
	public static final String TAG_NAME = "name";
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_REQUIREMENT = "requirement";
	public static final String TAG_DEADLINE = "deadline";

	private String name;
	private String description;
	private String deadline;
	private List<String> requiredSkillIDs;

	public TaskRequestMessage() {

	}

	public TaskRequestMessage(String name, String description, String deadline, List<String>  requiredSkillIds) {
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.requiredSkillIDs = requiredSkillIds;
	}

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getRequiredSkillIDs() {
		return requiredSkillIDs;
	}

	public void setRequiredSkillIDs(List<String> requiredSkillIDs) {
		this.requiredSkillIDs = requiredSkillIDs;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
