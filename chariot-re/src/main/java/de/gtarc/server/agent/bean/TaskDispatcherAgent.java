package de.gtarc.server.agent.bean;

import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.IMessageBoxAddress;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.ontology.AgentDescription;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;
import de.gtarc.chariot.registrationapi.agents.ServiceAgent;
import de.gtarc.chariot.serviceapi.impl.ServiceBuilder;
import de.gtarc.server.WebServer;
import de.gtarc.server.agent.fact.ConnectedDevice;
import de.gtarc.server.agent.fact.DisconnectDevice;
import de.gtarc.server.entity.Skill;
import de.gtarc.server.entity.Task;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.message.request.TaskCompletedMessage;
import de.gtarc.server.message.request.TaskRequestMessage;
import de.gtarc.server.service.KMSService;
import de.gtarc.server.util.CommonDate;
import de.gtarc.server.util.DeviceType;
import de.gtarc.server.util.FactoryTopic;
import de.gtarc.server.util.Serializer;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;


/***
 *
 * @author cemakpolat
 */

public class TaskDispatcherAgent extends ServiceAgent implements MqttCallback {
	public static final IAgentDescription PM_TEMPLATE = new AgentDescription(null, "PMAgent", null, null, null, null);
	public static final IAgentDescription TEMPLATE = new AgentDescription(null, "TaskDispatcherAgent", null, null, null,
			null);

	MqttClient publisherClient;
	MqttClient subscriberClient;
	IActionDescription sendAction;
	private  String uuid = "9d0262ca-9cb4-451a-8a4d-677c725caabd";
	@Override
	public void doStart() throws Exception {
		super.doStart();

		this.setEntity(
				new ServiceBuilder()
						.setUuid(UUID.fromString(uuid))
						.setName("TaskDispatcherAgent")
						.buildService()
		);
		this.register();

		IActionDescription template = new Action(ICommunicationBean.ACTION_SEND);
		sendAction = memory.read(template);

		publisherClient = new MqttClient(WebServer.MQTT_BROKER_ADDRESS, MqttClient.generateClientId(), new MemoryPersistence());
		publisherClient.connect();

		subscriberClient = new MqttClient(WebServer.MQTT_BROKER_ADDRESS, MqttClient.generateClientId(), new MemoryPersistence());
		subscriberClient.setCallback(this);
		subscriberClient.connect();
		subscriberClient.subscribe(FactoryTopic.TASK_DISPATCHER_NOTIFY.getTopic());

		memory.attach(new MessageObserver());
		log.info("TaskDispatcherAgent agent is started with uuid ->"+ this.getEntityID());
	}

	@Override
	public void execute() {
		KMSService dbservice = null;
		try {
			dbservice = KMSService.getInstance();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		System.out.println("There is " + memory.readAll(new ConnectedDevice(null, DeviceType.SMARTWATCH, null)).size()
				+ " online device");

		Comparator<Task> taskComparator = (Task task1, Task task2) -> task1.getTaskDeadline()
				.compareTo(task2.getTaskDeadline());
		PriorityQueue<Task> tasks = new PriorityQueue<>(taskComparator);
		for (Task task : dbservice.getTasksWithStatus(Task.VALUE_STATUS_UNASSIGNED)) {
			tasks.add(task);
		}

		while (!tasks.isEmpty()) {
			System.out.println("Task will be distributed!");
			Task task = tasks.poll();
			if (task != null) {
				List<Skill> skills = dbservice.getRequiredSkills(task);

				Worker worker = null;
				try {
					worker = findWorkerForTask(skills);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				if (worker != null) {
					task.setAssignedID(worker.getUuid());
					task.setTaskStatus(Task.VALUE_STATUS_ASSIGNED);
					dbservice.updateTask(task);
					dbservice.assignTaskToWorker(task, worker.getUuid());
					//System.out.println("db send update task!" + worker.getUuid());
					MqttMessage msg = new MqttMessage(task.getTaskID().getBytes());
					try {
						publisherClient.publish(FactoryTopic.SERVER_TASK_RECEIVE.getTopic(), msg);
						publisherClient.publish(FactoryTopic.HUMAN_RECEIVE.getTopic(worker.getUuid()), msg);
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Worker findWorkerForTask(List<Skill> skills) throws URISyntaxException {
		KMSService dbservice = KMSService.getInstance();

		for (Worker worker : dbservice.getWorkerBySkills(skills)) {
			// if worker is online
			Set<ConnectedDevice> devices = memory
					.readAll(new ConnectedDevice(worker.getUuid(), DeviceType.SMARTWATCH, null));

			if (!devices.isEmpty()) {
				// if worker has no assigned tasks
				List<Task> tasks = dbservice.getUnfinishedTasksOf(worker.getUuid());
				if (tasks == null || tasks.size() == 0) {
					return worker;
				}
			}
		}
		return null;
	}

	public void revalidateTasks(String uuid) throws URISyntaxException, InterruptedException {
		KMSService db = KMSService.getInstance();
		Worker worker = db.getWorkerWithUUID(uuid);
		List<Task> tasks = db.getUnfinishedTasksOf(worker.getUuid());
		for (Task task : tasks) {
			MqttMessage msg = new MqttMessage(task.getTaskID().getBytes());
			try {
				publisherClient.publish(FactoryTopic.SERVER_TASK_RECEIVE.getTopic(), msg);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}

	private class MessageObserver implements SpaceObserver<IFact> {

		@Override
		public void notify(SpaceEvent<? extends IFact> event) {
			if (event instanceof WriteCallEvent<?>) {
				IJiacMessage message = null;

				try { // TODO We have to make sure wce is WriteCallEvent<IJiacMessage> without using
						// try-catch
					WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;
					message = memory.read(wce.getObject());
				} catch (Exception e) {
					return;
				} finally {
					if (message == null)
						return;
				}
				IFact payload = (IFact) new Serializer().clone(message.getPayload());

				if (payload instanceof ConnectedDevice) {
					ConnectedDevice content = (ConnectedDevice) payload;
					memory.write(content);
					try {
						revalidateTasks(content.getUserUuid());
					} catch (URISyntaxException | InterruptedException e) {
						e.printStackTrace();
					}
					execute();
				} else if (payload instanceof DisconnectDevice) {
					DisconnectDevice msgContent = (DisconnectDevice) payload;
					memory.remove(new ConnectedDevice(msgContent.getUserUuid(), msgContent.getType(),
							msgContent.getDeviceUuid()));
					execute();
				} else if (payload instanceof TaskRequestMessage) {
					TaskRequestMessage msgContent = (TaskRequestMessage) payload;
					String uuid = UUID.randomUUID().toString();
					try {
						Task task = new Task(uuid, msgContent.getName(), msgContent.getDescription(),
								Task.VALUE_STATUS_UNASSIGNED, null,
								new CommonDate(CommonDate.HTML_DATETIME, msgContent.getDeadline()));
						KMSService.getInstance().addTask(task);
						execute();
					} catch (ParseException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		Object payload = new Serializer().deserialize(message.getPayload());
		//System.out.println("Task dispatache mqtt income : " + payload);
		if (payload instanceof TaskCompletedMessage) {
			System.out.println("task is taskCompleted");
			try {
				IAgentDescription agent = thisAgent.searchAgent(PM_TEMPLATE);
				if (agent != null) {
					JiacMessage jiacMsg = new JiacMessage((TaskCompletedMessage) payload);
					IMessageBoxAddress receiver = agent.getMessageBoxAddress();

					System.out
							.println("task is sending " + invoke(sendAction, new Serializable[] { jiacMsg, receiver }));

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		execute();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}
}
