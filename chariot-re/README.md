# Notification System App

**Requirements**

- Maven

- Mqtt broker, mosquito is preferred

  

## Wearable Runtime Environment (WRE)

CHARIOT adds its REs a new one, with a specially designed for endusers who works in a smart factory environment. WRE integrates the human actor through a smart watch application being described below in detail.  The messages created by the system is shared with this RE to the factory workers through their smart watch application. Each human actor defined in `resources/config/users` file will be represented as human agent in CHARIOT platform. The task distribution is performed via Task Dispatcher Agents (TDA). These two agents are defined using CHARIOT APIs and they are recognized by all other agents in the middleware. As soon as a message such as repair task, fix a machine, replace the broken temperature sensor, take the packet from the warehouse is created, this message is first sent to the TDA, where the most adequate available worker is selected.  If a worker is found, then the message is directed to her. 

The communication between agents are operated through ActiveMQ protocol, whereas the communication between agents and WRE is based on MQTT protocol. The interaction between wearable app and WRE is constructed on Websocket communication.  The following figure depics the interactions among all these entities.

<Place here the CPS.layer figure>

**Building & Starting WRE**

1. Start mosquitto or another mqtt broker. For mosquitto execute  `mosquito -d - p 1883`
2. git clone notification-app
3. Run `mvn clean package `
4. Ensure registrar agent and kms DB run before the execution of 3. step, if not read base guideline for having a simpliest chariot middleware to start them.
5. start server, ./target/bin/webapp or ./startWRE.sh
6. human-agents are executed, and their data models are generated if they are not in the database.
7. launch the smartwatch simulation, whose explanation is given below.
8. open localhost:10001 to enter the admin panel where the workers, tasks, skills are created.

**Admin Panel**

For testing the smartwatch and WRE is a web-application is implemented. This app offers a number of features to demonstrate the interaction between smartwatch app and WRE. The communication of the app with WRE is based on two different comm. protocols, namely. Websocket and HTML. The following functionalities are supported:

- Task-related: create/delete a task, assign a task to a worker
- Skill-related: create/delete a skill, assign a skill to a worker
- Worker-related: create a worker (even this feature is supported there is no mechanism for starting a new human-agent), view all workers, skills and their assigned tasks

Figure X depicts partly the admin panels avaialable views.

**Code Structure** 

- **de.gtarc.server.agent** folder includes all agents such as humanworker and taskdispatcheragent mentioned above. In addition, two ifact that is used for message exchange among agents. When a device is connected or disconnected, human agent informs TDA through this message types
- de.gtarc.server.endpoint folder has a single class that provides the websocket interface to the smartwatch and admin web application. Two classes are added as message encoder and decoder. Each incoming or outgoing message are formed by these classes.
- de.gtarc.server.entity folder accomodates the required entities for describing objects in the system such as task, skill, worker. Note that the human defined in the human-api is much more complicated then worker class.
- de.gtarc.server.message folder involves the classes that handles the messages with respect to their types. `handler`defines what to do as a next step, whereas the classes in `request` and `response` folder defines the message types. MessageDecoder and -Encoder are the place where the message is first encoded according to its `header`type. First the message type is fetched from header, then the related message type is created and returned. The following operation continue in `WebSocketEndpoint` located in de.gtarc.server.endpoint.  
- de.gtarc.server.pages folder is composed of Admin panel webapp. The backend side of the webapp is located in this folder.
-  de.gtarc.server.service folder is consisted of 2 communication protocol handlers, namely, mqtt and websocket, and one `KMSService` that interact with CHARIOT KMS database. All database operations are performed through KMSService via HTTP protocol except IoT entity registration process.
- de.gtarc.server.util has all classes that are common. DeviceSessionMap.java is reponsible for keeping the `user, device,devicetype and sessionId ` information to be able to follow the worker. SecurityUtil is designed to read the user credentials from a file located in `configs/users`. In the future version of this app is to use an additional security service. 
- de.gtarc.server.WebServer.java is the main class where the program is started. First the agents are started and then the web server along with the websocket communication.
- `resources` folder is also actively consumed by the whole application. `beans` includes JIAC agent configuration, whereas css, js, libs and templates are all related to the Admin panel webapp. As mentioned above, `configs/users` contains the user credentials along with their UUIDs.

