package de.gtarc.server.message.handler;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.UUID;

import javax.websocket.Session;

import de.gtarc.server.WebServer;
import de.gtarc.server.entity.Task;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.message.request.TaskCompletedMessage;
import de.gtarc.server.message.request.TaskRequestMessage;
import de.gtarc.server.message.response.ResponseMessage;
import de.gtarc.server.message.response.TaskInformationMessage;
import de.gtarc.server.service.KMSService;
import de.gtarc.server.util.CommonDate;

public class TaskMessagesHandler {
	public void execute(Session session, TaskRequestMessage message) throws URISyntaxException {

		String taskId = UUID.randomUUID().toString();
		String taskName = message.getName();
		String taskDesc = message.getDescription();
		String taskStatus = Task.VALUE_STATUS_UNASSIGNED;
		CommonDate taskDeadline;
		try {
			taskDeadline = new CommonDate(CommonDate.HTML_DATETIME, message.getDeadline());
		} catch (ParseException e) {
			e.printStackTrace();
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("Invalid deadline!");
			WebServer.instance().getWsHandler().immediateResponse(session, response);
			return;
		}
		Task task = new Task(taskId, taskName, taskDesc, taskStatus, "",  taskDeadline, message.getRequiredSkillIDs());
		System.out.println("message required skills"+  message.getRequiredSkillIDs().toString());
		KMSService db = KMSService.getInstance();
		boolean result = db.addTask(task);

		if (!result) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("Task could not be added to the database!");
			WebServer.instance().getWsHandler().immediateResponse(session, response);
			return;
		}

		ResponseMessage response = new ResponseMessage();
		response.setCode(ResponseMessage.EXECUTION_COMPLETED);
		response.setDetail("Task request completed!");
		WebServer.instance().getWsHandler().immediateResponse(session, response);

		WebServer.instance().getMqttHandler().notifyTaskDispatcher(message);
	}

	public void execute(Session session, TaskCompletedMessage message) throws URISyntaxException {
		KMSService db = KMSService.getInstance();

		String userUuid = WebServer.instance().getWsHandler().getSessions().getUserUuid(session);
		String deviceId = WebServer.instance().getWsHandler().getSessions().getDeviceUuid(session);
		Task task = db.getTaskWithId(message.getId());
		//System.out.println("completed Task with taskId "+task.getTaskID() +" userId:"+ userUuid);
		if (userUuid != null && task != null) {
			Worker worker = KMSService.getInstance().getWorkerWithUUID(userUuid);
			if (worker.getUuid().equals(task.getAssignedID())) {
				task.setTaskStatus(Task.VALUE_STATUS_DONE);
				db.updateTask(task);
				ResponseMessage response = new ResponseMessage();
				response.setCode(ResponseMessage.EXECUTION_COMPLETED);
				response.setDetail("The task with id " + task.getTaskID() + " is set to done!");
				WebServer.instance().getWsHandler().deliver(deviceId, response);

				TaskInformationMessage taskMessage = new TaskInformationMessage();
				taskMessage.parse(task);
				WebServer.instance().getWsHandler().deliver(deviceId, taskMessage);
				WebServer.instance().getMqttHandler().notifyTaskDispatcher(message);
			}
		}
	}
}
