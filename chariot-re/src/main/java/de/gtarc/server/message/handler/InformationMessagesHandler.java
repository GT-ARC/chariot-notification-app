package de.gtarc.server.message.handler;

import javax.websocket.Session;

import de.gtarc.server.WebServer;
//import de.gtarc.server.entity.Authorisation;
import de.gtarc.server.entity.Skill;
import de.gtarc.server.entity.Task;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.message.request.InformationRequestMessage;
import de.gtarc.server.message.response.ResponseMessage;
import de.gtarc.server.message.response.SkillInformationMessage;
import de.gtarc.server.message.response.TaskInformationMessage;
import de.gtarc.server.message.response.WorkerInformationMessage;
import de.gtarc.server.service.KMSService;
import de.gtarc.server.service.handler.ServerWsHandler;

import java.net.URISyntaxException;
import java.util.List;

public class InformationMessagesHandler {
	public void execute(Session session, InformationRequestMessage message) throws URISyntaxException {
		switch (message.getInfoType()) {
		case InformationRequestMessage.VALUE_TYPE_SKILL:
			executeSkillInformation(session, message.getInfoId());
			break;
		case InformationRequestMessage.VALUE_TYPE_TASK:
			executeTaskInformation(session, message.getInfoId());
			break;
		case InformationRequestMessage.VALUE_TYPE_WORKER:
			executeWorkerInformation(session, message.getInfoId());
			break;
		}
	}

	private void executeTaskInformation(Session session, String taskId) throws URISyntaxException {
		ServerWsHandler ws = WebServer.instance().getWsHandler();

		String userId = ws.getSessions().getUserUuid(session);
		String deviceId = ws.getSessions().getDeviceUuid(session);

		if (userId == null) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("You must log in!");
			ws.immediateResponse(session, response);
			return;
		}

		Task task =  KMSService.getInstance().getTaskWithId(taskId);

		if (task == null) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("There is no task with id: " + taskId);
			ws.immediateResponse(session, response);
			return;
		}

		List<Skill> skills =  KMSService.getInstance().getRequiredSkills(task);
		TaskInformationMessage taskInfo = new TaskInformationMessage();
		taskInfo.parse(task);
		ws.deliver(deviceId, taskInfo);
	}

	private void executeWorkerInformation(Session session, String workerId) throws URISyntaxException {


		ServerWsHandler ws = WebServer.instance().getWsHandler();

		String userId = ws.getSessions().getUserUuid(session);
		String deviceId = ws.getSessions().getDeviceUuid(session);

		if (userId == null) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("You must log in!");
			ws.immediateResponse(session, response);
			return;
		}

		//Worker worker = db.getWorkerWithUsername(workerId);
		Worker worker =  KMSService.getInstance().getWorkerWithUUID(userId);

		if (worker == null) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("There is no worker with id: " + workerId);
			ws.immediateResponse(session, response);
			return;
		}

		List<Skill> skills =  KMSService.getInstance().getSkillsOf(worker);
		//Authorisation[] auths = db.getAuthorisationsOf(worker);

		WorkerInformationMessage workerInfo = new WorkerInformationMessage();
		//workerInfo.parse(worker, skills, auths);
		workerInfo.parse(worker, skills);
		ws.deliver(deviceId, workerInfo);
	}

	private void executeSkillInformation(Session session, String skillId) throws URISyntaxException {
		ServerWsHandler ws = WebServer.instance().getWsHandler();

		String userId = ws.getSessions().getUserUuid(session);
		String deviceId = ws.getSessions().getDeviceUuid(session);

		if (userId == null) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("You must log in!");
			ws.immediateResponse(session, response);
			return;
		}

		Skill skill =  KMSService.getInstance().getSkillWithId(skillId);

		if (skill == null) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("There is no skill with id: " + skillId);
			ws.immediateResponse(session, response);
			return;
		}

		SkillInformationMessage skillInfo = new SkillInformationMessage();
		skillInfo.parse(skill);
		ws.deliver(deviceId, skillInfo);
	}
}