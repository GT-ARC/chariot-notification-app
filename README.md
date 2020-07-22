# Notification App

Notification mechanism aims at distributing the identified tasks to the most suitable workers inside the factory. The worker is informed about the new assignment request through the notification mechanism app on her smartwatch. In addition, each permitted worker can communicate with devices such as robots in both directions. For instance, the worker can execute a service in the robot, whereas a device can inform the worker about her current status. 

This tutorial covers only sharing the message generated by the system-related component with the human worker in the smart factory environment. The required components for realizing this environment are the human agents reprenting humans with different skills, a task dispatcher agent distributing the tasks, a runtime enviroment establishing the communication between devices and the human agents, an admin panel that creates a test environment for the task and skill creation, and finally a smart watch app displaying the coming message to the human worker. 

This project requires the execution of the following CHARIOT softwares:

- Knowledge Management Service (github reference)
- Monitoring Agent (github reference)
- Registrar Agent (github reference)

## Runtime Environment (RE) for Wearables

CHARIOT adds its existing REs a new one for endusers who works in a smart space environment.  RE integrates the human actor through a smart watch application being described below in detail.  The messages created by the system is shared over the RE to the factory workers through their smart watch application. Each human actor defined in `resources/config/users` file will be represented as human agent in CHARIOT platform. 

The task distribution is performed via Task Dispatcher Agents (TDA). These two agents are defined using CHARIOT APIs and they are recognized by all other agents in the middleware. As soon as a message such as repair task, fix a machine, replace the broken temperature sensor, take the packet from the warehouse is created, it is first sent to the TDA, where the most adequate available worker is selected.  If a worker is found, then the message is directed to her. 

The communication between agents are operated through ActiveMQ protocol, whereas the communication between agents and RE is based on MQTT protocol. The interaction between wearable app and WRE is constructed on Websocket communication.  The following figure depics the interactions among all these entities.

<Place here the CPS.layer figure>

**Building & Starting RE**

1. Start mosquitto or another mqtt broker running in the cloud. For the local usages, you can either install mosquitto or use the docker version. For the local installation type `mosquitto -d - p 1883`, and for the other option `eclipse-mosquitto` is recommended.

2. `git clone notification-app`

3. Run `mvn clean package `

4. Be sure registrar-agent and KMS are running.

5. Check user related data in `resources/config/users`. User credentials can be created based on [patternlock.js](http://www.ignitersworld.com/lab/patternLock.html). Username uses the rectangular pattern, wheares the password employs the circular pattern. 

   <Add here the credentials and related pictures from the watch>

6. Start RE, `./target/bin/webapp` or `./startWRE.sh`

7. Human-agents will be started and  their data models are generated if they are not in the database.

8. Launch the smartwatch simulation in the tizen studio, whose explanation is given below.

9. Open` localhost:10001` to enter the admin panel where the  tasks, skills are created and displayed.

**Admin Panel**

For testing the smartwatch and RE is a web-application is implemented. This app offers a number of features to demonstrate the interaction between smartwatch app and REW. The communication of the app with WRE is based on two different comm. protocols, namely. Websocket and HTML. The following functionalities are supported:

- Task-related: create/delete a task, assign a task to a worker
- Skill-related: create/delete a skill, assign a skill to a worker
- Worker-related: create a worker (even this feature is supported there is no mechanism for starting a new human-agent), view all workers, skills and their assigned tasks

Figure X depicts partly the admin panels avaialable views.

**RE components** 

- **de.gtarc.server.agent** package includes all agents such as humanworker and taskdispatcheragent mentioned above. In addition, two ifact that is used for message exchange among agents. When a device is connected or disconnected, human agent informs TDA through this message types
- **de.gtarc.server.endpoint** package has a single class that provides the websocket interface to the smartwatch and admin web application. Two classes are added as message encoder and decoder. Each incoming or outgoing message are formed by these classes.
- **de.gtarc.server.entity** package accomodates the required entities for describing objects in the system such as task, skill, worker. Note that the human defined in the human-api is much more complicated then worker class.
- **de.gtarc.server.message** package involves the classes that handles the messages with respect to their types. `handler`defines what to do as a next step, whereas the classes in `request` and `response` folder defines the message types. MessageDecoder and -Encoder are the place where the message is first encoded according to its `header`type. First the message type is fetched from header, then the related message type is created and returned. The following operation continue in `WebSocketEndpoint` located in de.gtarc.server.endpoint.  
- **de.gtarc.server.pages** package is composed of Admin panel webapp. The backend side of the webapp is located in this folder.
- **de.gtarc.server.service** package is consisted of 2 communication protocol handlers, namely, mqtt and websocket, and one `KMSService` that interact with CHARIOT KMS database. All database operations are performed through KMSService via HTTP protocol except IoT entity registration process.
- **de.gtarc.server.util package** has all classes that are common. DeviceSessionMap.java is reponsible for keeping the `user, device,devicetype and sessionId ` information to be able to follow the worker. SecurityUtil is designed to read the user credentials from a file located in `configs/users`. In the future version of this app is to use an additional security service. 
- **de.gtarc.server.WebServer.java** is the main class where the program is started. First the agents are started and then the web server along with the websocket communication.
- **resources/** folder is also actively consumed by the whole application. `beans` includes JIAC agent configuration, whereas css, js, libs and templates are all related to the Admin panel webapp. As mentioned above, `configs/users` contains the user credentials along with their UUIDs.



## Wearable Smartwatch App

**Requirements** 

- Download & Install Tizen Studio  (tested version is 3.7)

- Open Tizen's *Package Manager* and download packages below:
  - 3.0 and 4.0 Wearable
    - Emulator
    - Web app. developement (IDE)

**Certificates Generation for Emulator and Smartwatch**

- Emulator

  - if the app should only run in the emulator, then open Tizen Certificate Manager and simply create the certificates by pressing the `+` button. Select Tizen OS, provide the profile name, select `create a new author certificate`. Once the required fields are completed, the certificate will be created.

- Smartwatch

  -  Development process is constructed using [https://developer.samsung.com/tv/develop/getting-started/setting-up-sdk/creating-certificates]
  - Install the Samsung Certificate Extension using Package Manager under `Extension SDK`
  - Create a samsung account in TIZEN Studio under `Tools > certificate manager`
  - The generated keys will be placed under `tizen-studio-data/keystore/author/`. You can remove them if not needed
  - Samsung account is created if not available
    - User:<your email>
    - Password:<password>
  - Once the certificate is successfully created, you can deploy the application on the wearable device, and the application will be popped up on the watch screen.

  We have 2 certificates generated for a real app on the watch and for a simulator. Samsung certificates is used for the real app, and it has the following content in the certificate. You can choose your  certificate name and password as you wish.

  `certificate name: chariot, password: c	hariot2020, located under: Samsung certificates`

  For the emulator there is distinct certificate and it has

  `certificate name: chariot-sim, password: chariot2020, located under tizen-studio/keystore/...`

  

**Run Watch App**

1. Import wearable-app as an`Existing Project` inside the tizen studio.
2. Check the server host address in `WS_HOST_ADDRESS` written under `js/connectivity.js` file, in which the communication with the websocket server is established. Please replace your host IP with the existing address.
3. Select emulator from the emulator list and press **Run** button. You can install multiple emulator in order to create multiple users. 

Assuming that the worker credentials are already prepared on the server side. Now user will log in via pattern lock mechanism presented in [http://ignitersworld.com/lab/patternLock.html]. If you are not sure what credential data are generated, you can simply add a `console.log` where the pattern method is used.

**App components**

App.js is the control page to understand what action is performed on the watch by the user. Switching between watch faces can be controlled in this file. connectivity.js contains all connection mechanism with the websocket server. Login, logout, send/receive message are perfomed. jsonTemplates.js is used for the message creation that can be correctly interpreted by the server side. VALUE_HEADER is the message header and identifier for the backend. Task informatio page contains all assigned task to the worker. Each watch face is matched to its html pace. Health related measurements, watch battery and nfchandlers are implemented, however, they can be only used in a real smartwatch.

