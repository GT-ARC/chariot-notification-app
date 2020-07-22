package de.gtarc.server.message.response;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//import de.gtarc.server.entity.Authorisation;
import de.gtarc.server.entity.Skill;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.message.IMessage;

public class WorkerInformationMessage implements IMessage {

	public static final String VALUE_HEADER = "worker_information";

	public static final String TAG_ID = "id";
	public static final String TAG_NAME = "name";
	public static final String TAG_SURNAME = "surname";
	public static final String TAG_BIRTHDATE = "birthdate";
	public static final String TAG_SKILLS = "skills";
	public static final String TAG_AUTHORISATIONS = "auths";

	private String workerId;
	private String name;
	private String surname;
	private String birthdate;
	private String[] skills;
	private String[] authorisations;

	public WorkerInformationMessage() {

	}

	@Override
	public String getHeader() {
		return VALUE_HEADER;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
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

	public String[] getSkills() {
		return skills;
	}

	public void setSkills(String[] skills) {
		this.skills = skills;
	}

	public String[] getAuthorisations() {
		return authorisations;
	}

	public void setAuthorisations(String[] authorisations) {
		this.authorisations = authorisations;
	}

//	public void parse(Worker worker, Skill[] skills, Authorisation[] auths) {
//		String[] strSkills = new String[0];
//		String[] strAuths = new String[0];
//
//		if (skills != null) {
//			strSkills = IntStream.range(0, skills.length).mapToObj(i -> skills[i].getId()).toArray(String[]::new);
//		}
//
//		if (auths != null) {
//			strAuths = IntStream.range(0, auths.length).mapToObj(i -> auths[i].getId()).toArray(String[]::new);
//		}
//
//		this.setWorkerId(worker.getWorkerID());
//		this.setName(worker.getName());
//		this.setSurname(worker.getSurname());
//		this.setBirthdate(worker.getBirthDate().toString());
//		this.setSkills(strSkills);
//		this.setAuthorisations(strAuths);
//	}
	public void parse(Worker worker, List<Skill> skills) {
		// TODO: skills can be sometimes null, this issue should be later fixed! For now, skills are not part of the worker object
//		if (skills != null) {
//			List<String> list = skills.stream().map(item -> item.getName()).collect(Collectors.toList());
//			String[] array = list.toArray(new String[list.size()]);
//			if (array.length > 0 )
//				this.setSkills(array);
//			else
//				this.setSkills( new String[0]);
//		}else {
//			this.setSkills( new String[0]);
//		}
		this.setSkills( new String[0]);

		this.setWorkerId(worker.getWorkerID());
		this.setName(worker.getName());
		this.setSurname(worker.getSurname());
		this.setBirthdate(null);

	}
}
