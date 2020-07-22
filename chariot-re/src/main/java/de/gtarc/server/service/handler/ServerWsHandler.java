package de.gtarc.server.service.handler;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.Session;

import de.gtarc.server.message.IMessage;
import de.gtarc.server.message.response.ResponseMessage;
import de.gtarc.server.util.DeviceSessionMap;
import de.gtarc.server.util.DeviceSessionMap.Tuple;
import de.gtarc.server.util.DeviceType;

public class ServerWsHandler {

	private DeviceSessionMap sessions;

	public ServerWsHandler() {
		sessions = new DeviceSessionMap();
	}

	public DeviceSessionMap getSessions() {
		return sessions;
	}

	public boolean deliver(String userUuid, DeviceType type, IMessage message) {
		System.out.println("\n\nuserUuid:"+userUuid +" message->"+message.getHeader().toString() );
		for (Tuple tuple : sessions.iterator()) {
			if (tuple.getUserUuid().equals(userUuid) && tuple.getType().equals(type)) {
				tuple.getSession().getAsyncRemote().sendObject(message);
//				try {
//					tuple.getSession().getBasicRemote().sendObject(message);
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (EncodeException e) {
//					e.printStackTrace();
//				}
				return true;
			}
		}
		return false;

	}

	public boolean deliver(String deviceUuid, IMessage message) {
		System.out.println("deviceId:"+deviceUuid +" message->"+message.getHeader().toString() );
		Session session = sessions.getSession(deviceUuid);

		if (session != null) {
			System.out.println("session id "+session.getId());
//			try {
//				session.getBasicRemote().sendObject(message);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (EncodeException e) {
//				e.printStackTrace();
//			}
			session.getAsyncRemote().sendObject(message);
			return true;
		}
		return false;
	}

	public void broadcast(IMessage message) {
		for (Tuple tuple : sessions.iterator()) {
			tuple.getSession().getAsyncRemote().sendObject(message);
		}
	}

	public void immediateResponse(Session session, ResponseMessage response) {
		try {
			session.getBasicRemote().sendObject(response);
		} catch (IOException | EncodeException e) {
			System.out.println("An error occurred while delivering the message!");
			e.printStackTrace();
		}
	}

	public void immediateResponse(String workerID, String deviceUuid, ResponseMessage response) {
		Session session = sessions.getSession(deviceUuid);
		if (session != null) {
			immediateResponse(session, response);
		}
	}
}
