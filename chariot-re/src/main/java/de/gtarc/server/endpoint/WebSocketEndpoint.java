package de.gtarc.server.endpoint;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import de.gtarc.server.WebServer;
import de.gtarc.server.message.IMessage;
import de.gtarc.server.message.MessageDecoder;
import de.gtarc.server.message.MessageEncoder;
import de.gtarc.server.message.handler.HealthMessagesHandler;
import de.gtarc.server.message.handler.InformationMessagesHandler;
import de.gtarc.server.message.handler.LoginLogoutHandler;
import de.gtarc.server.message.handler.TaskMessagesHandler;
import de.gtarc.server.message.request.HealthDataMessage;
import de.gtarc.server.message.request.InformationRequestMessage;
import de.gtarc.server.message.request.LoginRequestMessage;
import de.gtarc.server.message.request.LogoutMessage;
import de.gtarc.server.message.request.TaskCompletedMessage;
import de.gtarc.server.message.request.TaskRequestMessage;
import de.gtarc.server.message.request.WorkerRequestMessage;

@ServerEndpoint(value = "/websocket", decoders = { MessageDecoder.class }, encoders = { MessageEncoder.class })
public class WebSocketEndpoint {

	@OnOpen
	public void onOpen(Session session) throws IOException, EncodeException {
		System.out.println("Log: A session " + session.getId() + " has opened a connection");
	}

	@OnClose
	public void onClose(Session session) throws IOException, EncodeException, URISyntaxException {
		System.out.println("Log: Session " + session.getId() + " has ended");
		String userUuid = WebServer.instance().getWsHandler().getSessions().getUserUuid(session);
		if (userUuid != null) {
			LogoutMessage msg = new LogoutMessage();
			LoginLogoutHandler handler = new LoginLogoutHandler();
			handler.execute(session, msg);
		}
	}

	@OnMessage
	public void onMessage(IMessage message, Session session) throws IOException, EncodeException, URISyntaxException {
		System.out.println("Log: message income from session id '" + session.getId() + "'");
		System.out.println("Log: message content is '" + message + "'");
		String header = message.getHeader();
		switch (header) {
		case LoginRequestMessage.VALUE_HEADER:
			LoginRequestMessage msg1 = (LoginRequestMessage) message;
			LoginLogoutHandler handler1 = new LoginLogoutHandler();
			handler1.execute(session, msg1);
			break;
		case LogoutMessage.VALUE_HEADER:
			LogoutMessage msg2 = (LogoutMessage) message;
			LoginLogoutHandler handler2 = new LoginLogoutHandler();
			handler2.execute(session, msg2);
			break;
		case TaskRequestMessage.VALUE_HEADER:
			TaskRequestMessage taskREquestMsg = (TaskRequestMessage) message;
			TaskMessagesHandler handler3 = new TaskMessagesHandler();
			handler3.execute(session, taskREquestMsg);
			break;
		case TaskCompletedMessage.VALUE_HEADER:
			TaskCompletedMessage taskCompletedMsg = (TaskCompletedMessage) message;
			TaskMessagesHandler taskCompletedHnd = new TaskMessagesHandler();
			taskCompletedHnd.execute(session, taskCompletedMsg);
			break;
		case WorkerRequestMessage.VALUE_HEADER:
			WorkerRequestMessage workerRequestMsg = (WorkerRequestMessage) message;
			LoginLogoutHandler workerRequestHnd = new LoginLogoutHandler();
			workerRequestHnd.execute(session, workerRequestMsg);
			break;
		case InformationRequestMessage.VALUE_HEADER:
			InformationRequestMessage informationRequestMsg = (InformationRequestMessage) message;
			InformationMessagesHandler informationRequestHander = new InformationMessagesHandler();
			informationRequestHander.execute(session, informationRequestMsg);
			break;
		case HealthDataMessage.VALUE_HEADER:
			HealthDataMessage healthDataMessage = (HealthDataMessage) message;
			HealthMessagesHandler healthDataHandler = new HealthMessagesHandler();
			healthDataHandler.execute(session, healthDataMessage);
			break;
		}
	}

	@OnError
	public void onError(Throwable e) {
		System.out.println("Log: An error occurred!");
		e.printStackTrace();
	}
}
