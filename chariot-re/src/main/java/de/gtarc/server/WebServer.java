package de.gtarc.server;

import java.io.File;
import java.net.URISyntaxException;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.servlet.ServletException;

import de.gtarc.server.agent.bean.HumanWorker;
import de.gtarc.server.service.KMSService;
import de.gtarc.server.util.CommonDate;
import de.gtarc.server.util.SecurityUtil;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.service.handler.ServerMqttHandler;
import de.gtarc.server.service.handler.ServerWsHandler;

public class WebServer {

	public final static String MQTT_BROKER_ADDRESS = "tcp://130.149.232.235:1883";

	public final static String SERVER_WEB_PORT = "10001";
	public final static String SERVER_DIR_LOCATION = "target/classes";
	public final static String SERVER_WEB_INF_CLASSES = "/WEB-INF/classes";
	public final static String SERVER_WEB_INF_CLASSES_ADDITIONAL = "target/classes";

	private static WebServer instance;

	private ServerMqttHandler mqttHandler;
	private ServerWsHandler wsHandler;
	SimpleAgentNode humanContainerNode;
	SimpleAgentNode pmNode;

	private WebServer() throws ServletException, LifecycleException, InterruptedException, URISyntaxException {
		System.out.println("WebServer instance creating..");
		wsHandler = new ServerWsHandler();
		try {
			mqttHandler = new ServerMqttHandler();
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// double checked singleton
	public static WebServer instance() {
		if (instance == null) {
			synchronized (WebServer.class) {
				if (instance == null) {
					try {
						instance = new WebServer();
					} catch (ServletException | LifecycleException | InterruptedException | URISyntaxException e) {
						e.printStackTrace();
						System.exit(2);
					}
				}
			}
		}
		return instance;
	}

	public ServerMqttHandler getMqttHandler() {
		return mqttHandler;
	}

	public ServerWsHandler getWsHandler() {
		return wsHandler;
	}

	private static Tomcat createTomcat() throws ServletException, InterruptedException {
		System.out.println("Log: Tomcat Creating...");
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.valueOf(SERVER_WEB_PORT));

		StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(SERVER_DIR_LOCATION).getAbsolutePath());
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(
				new DirResourceSet(resources, SERVER_WEB_INF_CLASSES, SERVER_WEB_INF_CLASSES_ADDITIONAL, "/"));
		ctx.setResources(resources);
		ctx.addErrorPage(new ErrorPage() {
			@Override
			public int getErrorCode() {
				return 404;
			}

			@Override
			public String getLocation() {
				return "/error";
			}
		});
		System.out.println("Log: Tomcat created!");

		return tomcat;
	}

	// This is required for the testbed!

	static void  addHumanAgents() throws LifecycleException, InterruptedException{
		SimpleAgentNode humanContainerNode = (SimpleAgentNode) new ClassPathXmlApplicationContext(
				"bean/human_container_node.xml").getBean("HumanContainerNode");
		// Note: The following two lines codes can be used if the human data is available in the database, otherwise we utilize the user credentials from a config/users file.
		//		List<Worker> workers = KMSService.getInstance().getAllWorkers();
		//		workers.stream().forEach(worker->{
		SecurityUtil security = new SecurityUtil();
		security.getUsers().stream().forEach(worker ->{
			ApplicationContext agentcontext = SimpleAgentNode.startAgentNode("classpath:bean/human_agent.xml",
					"jiactng_log4j.properties");
			IAgent agent = (IAgent) agentcontext.getBean("HumanAgent");
			for (IAgentBean bean : agent.getAgentBeans()) {
				if (bean.getBeanName().equals("HumanWorkerAgentBean")) {
					((HumanWorker) bean).setParams(worker.getUUID(),worker.getUsername(), worker.getPassword(), worker.getName());
				}
			}
			humanContainerNode.addAgent(agent);
			try {
				agent.start();
			} catch (LifecycleException e) {
				e.printStackTrace();
			}
		});
	}

	static void  startServicesNode() throws URISyntaxException, LifecycleException, InterruptedException {
		ApplicationContext context = SimpleAgentNode.startAgentNode("classpath:bean/predictive_maintenance_node.xml",
				"jiactng_log4j.properties");
		SimpleAgentNode node = (SimpleAgentNode) context.getBean("PMNode");
		node.start();

		System.setProperty("log4j.configuration", "jiactng_log4j.properties");

	}
	void startWebServer() throws ServletException, InterruptedException, org.apache.catalina.LifecycleException {
		// Start web server
		Tomcat	tomcat = createTomcat();
		System.out.println("Log: Starting tomcat..");
		tomcat.start();
		System.out.println("Log: Server running..");
		tomcat.getServer().await();
	}

	public static void main(String[] args) throws Exception {

		startServicesNode();
		addHumanAgents();

		WebServer server = new WebServer();
		server.startWebServer();


	}

	/**
	 * Helper function that is utilized for adding new users to the database without any registration process.
	 * @throws URISyntaxException
	 */
	void addHumanWorkers() throws URISyntaxException {
		SecurityUtil security = new SecurityUtil();
		security.readConfigFile();
		KMSService service = KMSService.getInstance();
		security.getUsers().stream().forEach(user ->{
			Worker worker = new Worker(user.getUUID(),  user.getUsername(),user.getPassword(), user.getName(),user.getSurname(),null);
			service.addWorker(worker);
		});
	}


		// Dynamic agent deployment on an agent node

	/**
	 * Helper function that is utilized to add users dynamically in the environment
	 * @throws LifecycleException
	 * @throws InterruptedException
	 */
	void addAgent() throws LifecycleException, InterruptedException {
		SimpleAgentNode humanContainerNode = (SimpleAgentNode) new ClassPathXmlApplicationContext(
				"bean/human_container_node.xml").getBean("HumanContainerNode");
		System.out.print("\n\n Agents will be added");
		for (int i = 0; i < 10; i++) {

			ApplicationContext agentcontext = SimpleAgentNode.startAgentNode("classpath:bean/human_agent.xml",
					"jiactng_log4j.properties");
			IAgent agent = (IAgent) agentcontext.getBean("HumanAgent");

			for (IAgentBean bean : agent.getAgentBeans()) {
				if (bean.getBeanName().equals("HumanWorkerAgentBean")) {
					((HumanWorker) bean).setEntityId(UUID.randomUUID().toString());
				}
			}

		Thread.sleep(1500);
			humanContainerNode.addAgent(agent);
			agent.start();
		}
	}

}
