package de.gtarc.server.service.handler;

import de.gtarc.server.service.KMSService;
import org.eclipse.paho.client.mqttv3.*;

import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.gtarc.server.WebServer;
import de.gtarc.server.entity.Skill;
import de.gtarc.server.entity.Task;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.message.response.TaskInformationMessage;
import de.gtarc.server.util.DeviceType;
import de.gtarc.server.util.FactoryTopic;
import de.gtarc.server.util.Serializer;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.URISyntaxException;
import java.util.List;

public class ServerMqttHandler {

	private MqttClient subscriber;
	private MqttClient publisher;

	private class ServerMqttCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable cause) {

		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			System.out.println(
					"Server mqtt subscriber received from " + topic + " with " + new String(message.getPayload()));
			String payload = new String(message.getPayload());

			FactoryTopic type = FactoryTopic.typeOf(topic);

			switch (type) {
			case SERVER_TASK_RECEIVE:
				Task task = KMSService.getInstance().getTaskWithId(payload);
				if (task != null) {
					TaskInformationMessage taskInfoMessage = new TaskInformationMessage();
					taskInfoMessage.parse(task);
					Worker worker = KMSService.getInstance().getWorkerWithUUID(task.getAssignedID());
					WebServer.instance().getWsHandler().deliver(worker.getUuid(), DeviceType.SMARTWATCH, taskInfoMessage);

				}
			default:
				break;
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {

		}
	}

	public ServerMqttHandler() throws MqttException, URISyntaxException {
		System.out.println("ServerMqttHandler is started!");
		subscriber = new MqttClient(WebServer.MQTT_BROKER_ADDRESS, MqttClient.generateClientId(),new MemoryPersistence());
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		subscriber.setCallback(new ServerMqttCallback());
		subscriber.connect(connOpts);
		subscriber.subscribe(FactoryTopic.SERVER_TASK_RECEIVE.getTopic());

		for (Worker worker : KMSService.getInstance().getAllWorkers()) {
			subscriber.subscribe(FactoryTopic.HUMAN_SENDING.getTopic(worker.getUuid()));
		}

		publisher = new MqttClient(WebServer.MQTT_BROKER_ADDRESS, MqttClient.generateClientId(),new MemoryPersistence());
		publisher.connect(connOpts);

	}

	public boolean notifyHuman(Worker worker, IFact message) {
		try {
			System.out.println("Notifying human " + worker.getUuid() + ":" + worker.getName());
			publisher.publish(FactoryTopic.HUMAN_RECEIVE.getTopic(worker.getUuid()),
					new MqttMessage(new Serializer().serialize(message)));
			return true;
		} catch (MqttException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean notifyTaskDispatcher(IFact message) {
		try {
			System.out.println("Notifying task dispatcher: ");
			publisher.publish(FactoryTopic.TASK_DISPATCHER_NOTIFY.getTopic(),
					new MqttMessage(new Serializer().serialize(message)));
			return true;
		} catch (MqttException e) {
			e.printStackTrace();
			return false;
		}
	}
}