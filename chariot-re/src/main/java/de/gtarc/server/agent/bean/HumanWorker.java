package de.gtarc.server.agent.bean;

import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.IMessageBoxAddress;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;
import de.gtarc.chariot.connectionapi.impl.MqttConnectionImpl;
import de.gtarc.chariot.humanapi.HumanProperty;
import de.gtarc.chariot.humanapi.ObjectTypes;
import de.gtarc.chariot.humanapi.impl.HumanBuilder;
import de.gtarc.chariot.humanapi.impl.HumanPropertyImpl;
import de.gtarc.chariot.humanapi.impl.Skill;
import de.gtarc.chariot.messageapi.impl.EntityBuilder;
import de.gtarc.chariot.messageapi.payload.PayloadEntity;
import de.gtarc.chariot.registrationapi.agents.HumanAgent;
import de.gtarc.commonapi.impl.EntityProperty;
import de.gtarc.commonapi.impl.OperationImpl;
import de.gtarc.commonapi.utils.Indoorposition;
import de.gtarc.commonapi.utils.IoTEntity;
import de.gtarc.commonapi.utils.Location;
import de.gtarc.commonapi.utils.Position;
import de.gtarc.server.WebServer;
import de.gtarc.server.agent.fact.ConnectedDevice;
import de.gtarc.server.agent.fact.DisconnectDevice;
import de.gtarc.server.util.FactoryTopic;
import de.gtarc.server.util.Serializer;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;
public class HumanWorker extends HumanAgent implements MqttCallback {

    private  String uuid = "9d0262ca-9cb4-451a-8a4d-677c723caabd";
    private String username, password, name;

    MqttClient subscribedClient;
    MqttClient publisherClient;

    IActionDescription sendAction = null;

    @Override
    public void doStart() throws Exception {
        super.doStart();
        this.setEntity(
                new HumanBuilder()
                        .setUuid(getEntityId())
                        .setName(username)
                        .setType(IoTEntity.HUMAN)
                        .setLocation( new Location(
                                "Smart Factory", "Room",
                                "Production Line", 0,
                                new Position(0, 0, "0"),
                                new Indoorposition("0", 0, 0)
                        ))
                        .addProperty(new HumanPropertyImpl(0, "account","string",username+","+password,"",true) )
                        .addProperty(new HumanPropertyImpl(0, "name","string",name,"",true) )
                        .addProperty(new HumanPropertyImpl(0, "skills","string","","",true) )
                        .addProperty(new HumanPropertyImpl(0, "tasks","string","","",true))
                        .addProperty(new HumanPropertyImpl(0, "permissions","string","","",true))
                        .build()
        );
        this.register();
        log.info("Human agent is started with uuid ->"+ this.getEntityID());

        IActionDescription template = new Action(ICommunicationBean.ACTION_SEND);
        sendAction = memory.read(template);

        subscribedClient = new MqttClient(WebServer.MQTT_BROKER_ADDRESS, MqttClient.generateClientId(),new MemoryPersistence());
        subscribedClient.setCallback(this);
        subscribedClient.connect();
        System.out.println("human-agent is subscribed to the channel ->"+ FactoryTopic.HUMAN_RECEIVE.getTopic(uuid));
        subscribedClient.subscribe(FactoryTopic.HUMAN_RECEIVE.getTopic(uuid));

        publisherClient = new MqttClient(WebServer.MQTT_BROKER_ADDRESS, MqttClient.generateClientId(),new MemoryPersistence());
        publisherClient.connect();
    }

    @Override
    @Expose(name = PROPERTY_ACTION, scope = ActionScope.GLOBAL)
    public void handleProperty(String message) {

    }
    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Object payload = new Serializer().deserialize(message.getPayload());

        if (payload instanceof ConnectedDevice) {
            ConnectedDevice connectedDevice = (ConnectedDevice) payload;
            memory.write(connectedDevice);
            IAgentDescription agent = thisAgent.searchAgent(TaskDispatcherAgent.TEMPLATE);
            if (agent != null) {
                JiacMessage jiacMsg = new JiacMessage(connectedDevice);
                IMessageBoxAddress receiver = agent.getMessageBoxAddress();
                invoke(sendAction, new Serializable[] { jiacMsg, receiver });
            }
        } else if (payload instanceof DisconnectDevice) {
            DisconnectDevice disconnectDevice = (DisconnectDevice) payload;
            memory.remove(new ConnectedDevice(disconnectDevice.getUserUuid(), disconnectDevice.getType(),
                    disconnectDevice.getDeviceUuid()));
            IAgentDescription agent = thisAgent.searchAgent(TaskDispatcherAgent.TEMPLATE);
            if (agent != null) {
                JiacMessage jiacMsg = new JiacMessage(disconnectDevice);
                IMessageBoxAddress receiver = agent.getMessageBoxAddress();
                invoke(sendAction, new Serializable[] { jiacMsg, receiver });
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public void setParams(String uuid, String username, String password, String name) {
        this.setEntityId(uuid);
        this.setMqttClientId(uuid);
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.name = name;
    }

    @Override
    public void doCleanup() {
        System.out.println("Cleanup");
    }

}
