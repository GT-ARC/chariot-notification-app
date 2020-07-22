package de.gtarc.server.message.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.UUID;

import javax.websocket.EncodeException;
import javax.websocket.Session;

import de.gtarc.server.WebServer;
import de.gtarc.server.agent.fact.ConnectedDevice;
import de.gtarc.server.agent.fact.DisconnectDevice;
import de.gtarc.server.entity.Worker;
//import de.gtarc.server.entity.relation.HasSkill;
import de.gtarc.server.message.request.LoginRequestMessage;
import de.gtarc.server.message.request.LogoutMessage;
import de.gtarc.server.message.request.WorkerRequestMessage;
import de.gtarc.server.message.response.ResponseMessage;
import de.gtarc.server.service.KMSService;
import de.gtarc.server.service.handler.ServerWsHandler;
import de.gtarc.server.util.CommonDate;
import de.gtarc.server.util.DeviceSessionMap;
import de.gtarc.server.util.DeviceType;

public class LoginLogoutHandler {

	public void execute(Session session, LoginRequestMessage message) throws IOException, EncodeException, URISyntaxException {
		ServerWsHandler ws = WebServer.instance().getWsHandler();

		KMSService db = KMSService.getInstance();

		String username = message.getUsername();
		String password = message.getPassword();

		Worker worker = db.getWorkerWithUsername(username);

		if (worker == null) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.LOGIN_FAILED);
			response.setDetail("Invalid username/password!");
			session.getBasicRemote().sendObject(response);
			return;
		}

		String userUuid = worker.getUuid();
		String deviceUuid = message.getDeviceId();
		DeviceType type = DeviceType.value(message.getDeviceType());

		boolean activeSession = ws.getSessions().getUserUuid(session) != null;
		boolean activeUser = ws.getSessions().getSession(userUuid, type, deviceUuid) != null;

		if (activeSession || activeUser) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.LOGIN_FAILED);
			response.setDetail("You are already logged in!");
			session.getBasicRemote().sendObject(response); // TODO use ws handler immediate response
			return;
		}

		boolean loginResult = worker != null ? worker.getPassword().equals(password) : false;

		if (loginResult) {
			ws.getSessions().register(userUuid, deviceUuid, type, session);

			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.LOGIN_SUCCESSFUL);
			response.setDetail("Login Successful!");
			session.getBasicRemote().sendObject(response);

			WebServer.instance().getMqttHandler().notifyHuman(worker, new ConnectedDevice(userUuid, type, deviceUuid));
		} else {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.LOGIN_FAILED);
			response.setDetail("Invalid username/password!");
			session.getBasicRemote().sendObject(response);
		}
	}

	public void execute(Session session, LogoutMessage message) throws IOException, EncodeException, URISyntaxException {
		DeviceSessionMap ss = WebServer.instance().getWsHandler().getSessions();
		//Worker worker = DatabaseService.getInstance().getWorkerWithUuid(ss.getUserUuid(session));
		Worker worker = KMSService.getInstance().getWorkerWithUUID(ss.getUserUuid(session));
		DeviceType type = ss.getDeviceType(session);
		if (type != null) {
			String deviceUuid = ss.getDeviceUuid(session);
			if (type != null){
				ss.unregister(session);
				WebServer.instance().getMqttHandler().notifyHuman(worker,
						new DisconnectDevice(worker.getUuid(), type, deviceUuid));
			}
		}
	}

	public void execute(Session session, WorkerRequestMessage message) throws URISyntaxException {
		ServerWsHandler ws = WebServer.instance().getWsHandler();

		String uuid = UUID.randomUUID().toString();
		String workerID = message.getWorkerID();
		String password = message.getPassword();
		String name = message.getName();
		String surname = message.getSurname();
		CommonDate birthdate;
		try {
			birthdate = new CommonDate(CommonDate.HTML_DATE, message.getBirthdate());
		} catch (ParseException e) {
			e.printStackTrace();
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("Invalid birthdate!");
			ws.immediateResponse(session, response);
			return;
		}

		Worker worker = new Worker(uuid, workerID, password, name, surname, birthdate);

		//DatabaseService db = DatabaseService.getInstance();
		KMSService db = KMSService.getInstance();
		boolean result = db.addWorker(worker);

		if (!result) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("Could not add worker to the database!");
			ws.immediateResponse(session, response);
			return;
		}

		for (String sid : message.getAcquiredSkills()) {
			result = db.addSkill(workerID,sid);
			//result = db.addHasSkill(new HasSkill(workerID, sid));
		}

		ResponseMessage response = new ResponseMessage();
		response.setCode(ResponseMessage.EXECUTION_COMPLETED);
		response.setDetail("Worker added to the database!");
		ws.immediateResponse(session, response);
	}
}
