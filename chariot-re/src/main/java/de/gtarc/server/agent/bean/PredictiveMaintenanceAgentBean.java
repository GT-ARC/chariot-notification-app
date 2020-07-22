package de.gtarc.server.agent.bean;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.IMessageBoxAddress;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.ontology.AgentDescription;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;
import de.gtarc.server.message.request.TaskCompletedMessage;
import de.gtarc.server.message.request.TaskRequestMessage;
import de.gtarc.server.util.CommonDate;
import de.gtarc.server.util.HttpClientHelper;
import de.gtarc.server.util.Serializer;
 /**
 This class is implemented only to test the task dispatcher agent.
  @author cemakpolat
 */
public class PredictiveMaintenanceAgentBean extends AbstractAgentBean {

	private static final String SERVICE_REQUEST_PATH = "http://10.0.2.93:5000/cloud/db/getLatestData/";
	public static final IAgentDescription TEMPLATE = new AgentDescription(null, "PMAgent", null, null, null, null);

	IActionDescription sendAction;
	private boolean requested; // TODO make it list to maintain multiple device

	@Override
	public void doStart() throws Exception {

		super.doStart();
		requested = false;

		IActionDescription template = new Action(ICommunicationBean.ACTION_SEND);
		sendAction = memory.read(template);
		memory.attach(new MessageObserver(), new JiacMessage(new TaskCompletedMessage()));
	}

	@Override
	public void execute() {


//		TaskRequestMessage requestMessage = new TaskRequestMessage();
//		CommonDate oneHLater = new CommonDate(Date.from(new Date().toInstant().plus(Duration.ofHours(1))));
//
//		requestMessage.setName("Repair");
//		requestMessage.setDescription("There is a machine that needs to be fixed! Motor id is 1");
//		requestMessage.setDeadline(oneHLater.format(CommonDate.HTML_DATETIME));
//
//		IAgentDescription tdAgent = thisAgent.searchAgent(TaskDispatcherAgent.TEMPLATE);
//
//		if (tdAgent != null) {
//			requested = true;
//			JiacMessage jiacMsg = new JiacMessage(requestMessage);
//			IMessageBoxAddress receiver = tdAgent.getMessageBoxAddress();
//			invoke(sendAction, new Serializable[]{jiacMsg, receiver});
//		}



//		HttpClientHelper httpClient = new HttpClientHelper();
//		System.out.println("Predictive Maintenance execute ");
//		String result = httpClient.sendByPost(httpClient.createRequestPath(SERVICE_REQUEST_PATH, ""), createRequest());
//		System.out.println("Response from motor1: " + result);
//
//		if (result != null) {
//			try {
//				JSONParser parser = new JSONParser();
//
//				JSONObject res = (JSONObject) parser.parse(result);
//				System.out.println("Is task requested: " + requested + res.get("result"));
//
//				if (res.get("result").toString().equalsIgnoreCase("1")) {
//
//					TaskRequestMessage requestMessage = new TaskRequestMessage();
//					CommonDate oneHLater = new CommonDate(Date.from(new Date().toInstant().plus(Duration.ofHours(1))));
//
//					requestMessage.setName("Repair");
//					requestMessage.setDescription("There is a machine that needs to be fixed! Motor id is 1");
//					requestMessage.setDeadline(oneHLater.format(CommonDate.HTML_DATETIME));
//
//					IAgentDescription tdAgent = thisAgent.searchAgent(TaskDispatcherAgentBean.TEMPLATE);
//
//					if (tdAgent != null) {
//						requested = true;
//						JiacMessage jiacMsg = new JiacMessage(requestMessage);
//						IMessageBoxAddress receiver = tdAgent.getMessageBoxAddress();
//						invoke(sendAction, new Serializable[]{jiacMsg, receiver});
//					}
//				}
//			} catch(Exception e ){
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//
//			}
//		}

	}


	private class MessageObserver implements SpaceObserver<IFact> {

		@Override
		public void notify(SpaceEvent<? extends IFact> event) {
			if (event instanceof WriteCallEvent<?>) {

				WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;
				IJiacMessage message = memory.read(wce.getObject());
				IFact payload = (IFact) new Serializer().clone(message.getPayload());

				if (payload instanceof TaskCompletedMessage) {
					TaskCompletedMessage content = (TaskCompletedMessage) payload;

					requested = false;
				}
			}
		}
	}
}
