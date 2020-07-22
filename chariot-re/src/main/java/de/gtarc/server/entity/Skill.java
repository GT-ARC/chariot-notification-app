package de.gtarc.server.entity;


public class Skill {
	public static final String TAG_SKILL = "skill";
	public static final String TAG_ID = "skill_id";
	public static final String TAG_DESC = "skill_desc";
	private String id;
	private String name;
	private String desc;

	public Skill(String id, String name, String desc) {
		this.id = id;
		this.name = name;
		this.desc = desc;
	}
	public Skill(String id) {
		this.id = id;
	}
	public Skill(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	public String getId(){
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}