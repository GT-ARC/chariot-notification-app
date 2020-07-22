package de.gtarc.server.message.handler;

import javax.websocket.Session;

import de.gtarc.server.WebServer;
import de.gtarc.server.entity.HealthData;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.message.request.HealthDataMessage;
import de.gtarc.server.message.response.ResponseMessage;
import de.gtarc.server.service.KMSService;
import de.gtarc.server.service.handler.ServerWsHandler;
import de.gtarc.server.util.CommonDate;
import de.gtarc.server.util.DeviceSessionMap;

import java.net.URISyntaxException;

public class HealthMessagesHandler {
	public void execute(Session session, HealthDataMessage message) throws URISyntaxException {
		ServerWsHandler ws = WebServer.instance().getWsHandler();
		DeviceSessionMap aw = ws.getSessions();
		//DatabaseService db = DatabaseService.getInstance();
		KMSService db = KMSService.getInstance();
		String userUuid = aw.getUserUuid(session);

		if (userUuid == null) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("You must login!");
			ws.immediateResponse(session, response);
			return;
		}

		Worker worker = db.getWorkerWithUUID(userUuid);
		CommonDate time = null;
		int heartRate = 0;
		String stepStatus = message.getStepStatus();
		String feelStatus = message.getFeelStatus();
		String workStatus = message.getWorkStatus();
		try {
			time = new CommonDate(CommonDate.WEARABLE_TIMESTAMP, message.getTime());
			heartRate = Integer.parseInt(message.getHeartRate());
		} catch (Exception e) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("Wrong arguments!");
			ws.immediateResponse(session, response);
			return;
		}

		HealthData healthData = new HealthData(worker.getWorkerID(), time, heartRate, stepStatus, feelStatus, workStatus);
		boolean result = db.addHealthData(healthData);

		if (!result) {
			ResponseMessage response = new ResponseMessage();
			response.setCode(ResponseMessage.EXECUTION_REJECTED);
			response.setDetail("Database insertion failed!");
			ws.immediateResponse(session, response);
		}
	}
}
