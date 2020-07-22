package de.gtarc.server.message;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import de.gtarc.server.message.request.HealthDataMessage;
import de.gtarc.server.message.request.InformationRequestMessage;
import de.gtarc.server.message.request.LoginRequestMessage;
import de.gtarc.server.message.request.LogoutMessage;
import de.gtarc.server.message.request.TaskCompletedMessage;
import de.gtarc.server.message.request.TaskRequestMessage;
import de.gtarc.server.message.request.WorkerRequestMessage;
import de.gtarc.server.message.response.ResponseMessage;

public class MessageDecoder implements Decoder.Text<IMessage> {

	@Override
	public void init(EndpointConfig endpointConfig) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public IMessage decode(String jsonMessage) throws DecodeException {
		JsonObject jsonObject = Json.createReader(new StringReader(jsonMessage)).readObject();
		String header = jsonObject.getString(IMessage.TAG_HEADER);
		System.out.println("\n\n watch:" + jsonMessage);
		switch (header) {
		case LoginRequestMessage.VALUE_HEADER:
			LoginRequestMessage message = new LoginRequestMessage();
			message.setUsername(jsonObject.getString(LoginRequestMessage.TAG_USERNAME));
			message.setPassword(jsonObject.getString(LoginRequestMessage.TAG_PASSWORD));
			message.setDeviceId(jsonObject.getString(LoginRequestMessage.TAG_DEVICE_ID));
			message.setDeviceType(jsonObject.getString(LoginRequestMessage.TAG_DEVICE_TYPE));
			return message;
		case ResponseMessage.VALUE_HEADER:
			ResponseMessage message1 = new ResponseMessage();
			message1.setCode(jsonObject.getInt(ResponseMessage.TAG_CODE));
			message1.setDetail(jsonObject.getString(ResponseMessage.TAG_DETAIL));
			return message1;
		case LogoutMessage.VALUE_HEADER:
			LogoutMessage message2 = new LogoutMessage();
			return message2;
		case TaskRequestMessage.VALUE_HEADER:
			TaskRequestMessage taskRequestMsg = new TaskRequestMessage();
			List<String> skills = asStringList(jsonObject.get(TaskRequestMessage.TAG_REQUIREMENT).asJsonArray());
			taskRequestMsg.setName(jsonObject.getString(TaskRequestMessage.TAG_NAME));
			taskRequestMsg.setDescription(jsonObject.getString(TaskRequestMessage.TAG_DESCRIPTION));
			taskRequestMsg.setRequiredSkillIDs(skills);
			taskRequestMsg.setDeadline(jsonObject.getString(TaskRequestMessage.TAG_DEADLINE));
			return taskRequestMsg;
		case TaskCompletedMessage.VALUE_HEADER:
			TaskCompletedMessage taskCompletedMsg = new TaskCompletedMessage();
			taskCompletedMsg.setId(jsonObject.getString(TaskCompletedMessage.TAG_ID));
			return taskCompletedMsg;
		case WorkerRequestMessage.VALUE_HEADER:
			WorkerRequestMessage workerRequestMessage = new WorkerRequestMessage();
			List<String>  acquiredSkills = asStringList(jsonObject.get(WorkerRequestMessage.TAG_ACQUIRED).asJsonArray());
			workerRequestMessage.setWorkerID(jsonObject.getString(WorkerRequestMessage.TAG_ID));
			workerRequestMessage.setPassword(jsonObject.getString(WorkerRequestMessage.TAG_PASSWORD));
			workerRequestMessage.setName(jsonObject.getString(WorkerRequestMessage.TAG_NAME));
			workerRequestMessage.setSurname(jsonObject.getString(WorkerRequestMessage.TAG_SURNAME));
			workerRequestMessage.setBirthdate(jsonObject.getString(WorkerRequestMessage.TAG_BIRTHDATE));
			workerRequestMessage.setAcquiredSkills(acquiredSkills);
			return workerRequestMessage;
		case InformationRequestMessage.VALUE_HEADER:
			InformationRequestMessage informationRequestMessage = new InformationRequestMessage();
			informationRequestMessage.setInfoId(jsonObject.getString(InformationRequestMessage.TAG_ID));
			informationRequestMessage.setInfoType(jsonObject.getString(InformationRequestMessage.TAG_TYPE));
			return informationRequestMessage;
		case HealthDataMessage.VALUE_HEADER:
			HealthDataMessage healthDataMessage = new HealthDataMessage();
			healthDataMessage.setHeartRate(jsonObject.getString(HealthDataMessage.TAG_HEART_RATE));
			healthDataMessage.setStepStatus(jsonObject.getString(HealthDataMessage.TAG_STEP_STATUS));
			healthDataMessage.setTime(jsonObject.getString(HealthDataMessage.TAG_TIME));
			healthDataMessage.setFeelStatus(jsonObject.getString(HealthDataMessage.TAG_FEEL_STATUS));
			healthDataMessage.setWorkStatus(jsonObject.getString(HealthDataMessage.TAG_WORK_STATUS));
			return healthDataMessage;
		}
		return null;
	}

	@Override
	public boolean willDecode(String s) {
		return true;
	}

	private List<String> asStringList(JsonArray jsonArray) {
		List<String> list = new ArrayList<String>();
		System.out.print("JSON Array:"+ jsonArray.toString());
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(jsonArray.getString(i));
			System.out.print(list.get(i));
		}
		return list;
	}
}
