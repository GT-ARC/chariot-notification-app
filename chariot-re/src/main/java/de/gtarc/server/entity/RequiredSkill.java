package de.gtarc.server.entity;

public class RequiredSkill {
	public static final String TAG_REQUIRED_SKILL = "required_skill";
	public static final String TAG_SKILL_ID = "skill_id";
	public static final String TAG_TASK_ID = "task_id";

	private String skillId;
	private String taskId;

	public RequiredSkill(String skillId, String taskId) {
		this.skillId = skillId;
		this.taskId = taskId;
	}

	public String getSkillId() {
		return skillId;
	}

	public String getTaskId() {
		return taskId;
	}
}
