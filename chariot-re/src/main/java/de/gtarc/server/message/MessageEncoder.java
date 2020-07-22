package de.gtarc.server.message;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import de.gtarc.server.message.response.ResponseMessage;
import de.gtarc.server.message.response.SkillInformationMessage;
import de.gtarc.server.message.response.TaskInformationMessage;
import de.gtarc.server.message.response.WorkerInformationMessage;

import java.util.List;

public class MessageEncoder implements Encoder.Text<IMessage> {

	@Override
	public void init(EndpointConfig endpointConfig) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public String encode(IMessage object) throws EncodeException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		String header = object.getHeader();

		builder.add(IMessage.TAG_HEADER, header);
		switch (header) {
		case ResponseMessage.VALUE_HEADER:
			ResponseMessage message1 = (ResponseMessage) object;
			builder.add(ResponseMessage.TAG_CODE, message1.getCode());
			builder.add(ResponseMessage.TAG_DETAIL, message1.getDetail());
			break;
		case TaskInformationMessage.VALUE_HEADER:
			TaskInformationMessage taskInformationMessage = (TaskInformationMessage) object;
//			JsonArray skills2 = asJsonArray(taskInformationMessage.getRequirement());
			builder.add(TaskInformationMessage.TAG_ID, taskInformationMessage.getId());
			builder.add(TaskInformationMessage.TAG_NAME, "" + taskInformationMessage.getName()); // TODO make sure
																									// getName is not
																									// null
			builder.add(TaskInformationMessage.TAG_DESCRIPTION, taskInformationMessage.getDescription());
			builder.add(TaskInformationMessage.TAG_STATUS, taskInformationMessage.getStatus());
			builder.add(TaskInformationMessage.TAG_ASSIGNED, taskInformationMessage.getAssigned());
//			builder.add(TaskInformationMessage.TAG_REQUIREMENT, "");
			//builder.add(TaskInformationMessage.TAG_DEADLINE, taskInformationMessage.getDeadline());
			break;
		case WorkerInformationMessage.VALUE_HEADER:
			WorkerInformationMessage workerInformationMessage = (WorkerInformationMessage) object;
			JsonArray workerSkills = asJsonArray(workerInformationMessage.getSkills());
			JsonArray workerInfoAuths = asJsonArray(workerInformationMessage.getAuthorisations());
			builder.add(WorkerInformationMessage.TAG_ID, workerInformationMessage.getWorkerId());
			builder.add(WorkerInformationMessage.TAG_NAME, workerInformationMessage.getName());
			builder.add(WorkerInformationMessage.TAG_SURNAME, workerInformationMessage.getSurname());
			builder.add(WorkerInformationMessage.TAG_BIRTHDATE, workerInformationMessage.getBirthdate());
			builder.add(WorkerInformationMessage.TAG_SKILLS, workerSkills);
			builder.add(WorkerInformationMessage.TAG_AUTHORISATIONS, workerInfoAuths);
			break;
		case SkillInformationMessage.VALUE_HEADER:
			SkillInformationMessage skillInformationMessage = (SkillInformationMessage) object;
			builder.add(SkillInformationMessage.TAG_ID, skillInformationMessage.getId());
			builder.add(SkillInformationMessage.TAG_DESCRIPTION, skillInformationMessage.getDescription());
			break;
		}

		return builder.build().toString();
	}

	private JsonArray asJsonArray(String[] strArr) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (String str : strArr) {
			builder.add(str);
		}
		return builder.build();
	}
	private JsonArray asJsonArray(List<String> strArr) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (String str : strArr) {
			builder.add(str);
		}
		return builder.build();
	}
}
