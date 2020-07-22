package de.gtarc.server.message.response;

import de.gtarc.server.entity.Skill;
import de.gtarc.server.message.IMessage;

public class SkillInformationMessage implements IMessage {

	public static final String VALUE_HEADER = "skill_information";

	public static final String TAG_ID = "id";
	public static final String TAG_NAME = "name";
	public static final String TAG_DESCRIPTION = "description";

	private String id;
	private String description;
	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



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

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public void parse(Skill skill) {
		setName(skill.getName());
		setId(skill.getId());
		setDescription(skill.getDesc());
	}
}
