package de.gtarc.server.service;

import com.google.gson.*;
import de.gtarc.chariot.connectionapi.impl.WebClientConnection;
import de.gtarc.chariot.dbhandlerapi.impl.KmsHandlerImpl;
import de.gtarc.chariot.humanapi.Human;
import de.gtarc.chariot.humanapi.impl.HumanBuilder;
import de.gtarc.chariot.humanapi.impl.HumanPropertyImpl;
import de.gtarc.chariot.messageapi.impl.EntityBuilder;
import de.gtarc.chariot.messageapi.payload.PayloadEntity;
import de.gtarc.chariot.messageapi.payload.PayloadEntityProperty;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.impl.EntityProperty;
import de.gtarc.commonapi.impl.OperationImpl;
import de.gtarc.commonapi.utils.Indoorposition;
import de.gtarc.commonapi.utils.IoTEntity;
import de.gtarc.commonapi.utils.Location;
import de.gtarc.commonapi.utils.Position;
import de.gtarc.server.entity.*;
import de.gtarc.server.util.CommonDate;
import de.gtarc.server.util.SecurityUtil;
import org.apache.jena.base.Sys;

import java.util.*;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

/**
 * http://chariot-km.dai-lab.de:8080/v1/ui/#/
 */
public class KMSService {
	private static KMSService instance;
	public static KMSService getInstance() throws URISyntaxException {
		if (instance == null) {
			synchronized (KMSService.class) {
				if (instance == null) {
					instance = new KMSService();
				}
			}
		}

		return instance;
	}

	public static void main(String[] args) throws Exception {

		KMSService service = new KMSService();
		//service.addHumanWorkers();
		//service.getTasks();
	//	service.updateTask("a8d9cd47-57b1-4ea9-bc4e-4904a3162952",null);
		//service.getSkills();
		//service.addWorker();
//		service.addSkill(); ok
		//service.addTask();
		service.deleteTask(UUID.fromString("45633708-2ed8-4b72-bf4a-447a3632cbe8"));
	}
	void addHumanWorkers() throws URISyntaxException {
		SecurityUtil security = new SecurityUtil();
		security.readConfigFile();
		security.getUsers().stream().forEach(user ->{

			Worker worker = new Worker(user.getUUID(),  user.getUsername(),user.getPassword(), user.getName(),user.getSurname(),null);
			this.addWorker(worker);
		});
	}

	KmsHandlerImpl kms ;

	KMSService() throws URISyntaxException {
		kms = new KmsHandlerImpl("http://chariot-km.dai-lab.de:8080/v1","");
	}
	public void addWorker(){
		// custom workers
	}
	public boolean addWorker(Worker worker){
		// generate a worker
		PayloadEntity human = new EntityBuilder().setUUIdentifier(UUID.fromString(worker.getUuid())).setSecurityKey("123")
				.setGroupId("humans").setName(worker.getWorkerID()).setObjectType(IoTEntity.HUMAN).setIp("0.0.0.0").
				setLocation(new Location()).setReId(IoTEntity.REID_NOTREQUIRED)
				.setLocation( new Location(
						"Smart Factory", "Room",
						"Production Line", 0,
						new Position(0, 0, "0"),
						new Indoorposition("0", 0, 0)
				))
				.addProperty(new HumanPropertyImpl(0, "account","string",worker.getWorkerID()+','+worker.getPassword(),"",true) )
				.addProperty(new HumanPropertyImpl(0, "name","string",worker.getName() +" "+ worker.getSurname(),"",true) )
				.addProperty(new HumanPropertyImpl(0, "skills","string","","",true) )
				.addProperty(new HumanPropertyImpl(0, "tasks","string","","",true))
				.addProperty(new HumanPropertyImpl(0, "permissions","string","","",true))
				.addOperation(new OperationImpl("none", new String[0],new String[0]))
				.buildEntity();

		kms.addEntity(human);
		return true;
	}
	public boolean addSkill(Skill skill){
		List<UUID> list = new ArrayList<UUID>();
		PayloadEntity skill1 = new EntityBuilder().setUUIdentifier(UUID.fromString(skill.getId())).setGroupId("skills").setName("skill-a")
				.addProperty(new EntityProperty(0, "name","string",skill.getName(),"",true))
				.addProperty(new EntityProperty(0, "description","string",skill.getDesc(),"",true))
				.setObjectType("skill")
				.buildEntity();
		list.add(UUID.fromString(skill1.getUuid()));
		kms.addEntity(skill1);
		return true;
	}
	public boolean assignTaskToWorker(Task task, String workerId){
		Human human = getHumanWithUUID(workerId);
		// update property
		List<String> taskIds = human.getTasks().stream().map(taskItem -> taskItem.toString()).collect(Collectors.toList());
		if (!taskIds.contains(task.getTaskID())){
			taskIds.add(task.getTaskID());
			String listString = String.join(", ", taskIds);
			kms.updateEntityProperty(workerId, new PayloadEntityProperty(IoTEntity.HUMAN, "string","tasks", listString));
		}
		return true;
	}


	public boolean addTask(Task task){
		task.setTaskId(UUID.randomUUID().toString());
		List<UUID> list = new ArrayList<UUID>();
		PayloadEntity task1 = new EntityBuilder().setUUIdentifier(UUID.fromString(task.getTaskID())).
				setGroupId("tasks").setName(task.getTaskName()).setObjectType("task")
				.addProperty(new EntityProperty(0, "name","string",task.getTaskName(),"",true))
				.addProperty(new EntityProperty(0, "skills","string",task.getSkillIdsAsString(),"",true) )
				.addProperty(new EntityProperty(0, "deadline","string",task.getTaskDeadline(),"",true))
				.addProperty(new EntityProperty(0, "assigned","string", task.getAssignedID(),"",true))
				.addProperty(new EntityProperty(0, "description","string",task.getTaskDesc(),"...",true))
				.addProperty(new EntityProperty(0, "status","string",task.getTaskStatus(),"...",true))
				.buildEntity();

		list.add(UUID.fromString(task1.getUuid()));
		kms.addEntity(task1);
		return true;
	}

	public void deleteTask(UUID taskId){
		WebClientConnection conn = new WebClientConnection(8080);
		try {
			conn.connect();
			conn.sendByDelete(kms.getURI()+"/tasks/"+taskId.toString()+"/");
			conn.disconnect();

		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}

	public void deleteSkill(UUID skillId){
		WebClientConnection conn = new WebClientConnection(8080);
		try {
			conn.connect();
			conn.sendByDelete(kms.getURI()+"/skills/"+skillId.toString()+"/");
			conn.disconnect();
		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}

	public void updateTask(String entityId, PayloadEntityProperty property){
		PayloadEntityProperty property1 = new PayloadEntityProperty("task", "string","name","");
		property1.setWritable(true);
		kms.updateEntityProperty(entityId,property1);
	}

	public void updateSkill(String entityId, PayloadEntityProperty property){
		PayloadEntityProperty property1 = new PayloadEntityProperty("skill", "string","name", "skill-a");
		kms.updateEntityProperty(entityId,property1);
	}

	public List<String> getAsList(String value){
		if(!value.isEmpty() && !value.equals("\"\"")){
			List<String> list = new ArrayList<>(Arrays.asList(value.replaceAll("\\s+", "").split(",")));
			return list;
		}
		return null;
	}
	public void addSkillToTask(String skillId, String taskId){
		// get property
		WebClientConnection conn = new WebClientConnection(8080);
		try {
			conn.connect();
			String skill = conn.sendByGet(kms.getURI()+"/tasks/"+taskId+"/properties/skills/");
			conn.disconnect();
			JsonElement object = new JsonParser().parse(skill);
			if(object.isJsonObject()){
				List<String> skillsIds = getAsList(object.getAsJsonObject().get("skills").getAsString());
				if (! skillsIds.stream().anyMatch(item -> item.equalsIgnoreCase(skillId))){
					skillsIds.add(skillId);
					String listString = String.join(", ", skillsIds);
					kms.updateEntityProperty(taskId, new PayloadEntityProperty("skill", "string","skills", listString));
				}else {
					System.out.print("UUID does exist!");
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}

	public void removeSkillFromTask(String skillId, String taskId){
		WebClientConnection conn = new WebClientConnection(8080);
		try {
			conn.connect();
			String skill = conn.sendByGet(kms.getURI()+"/tasks/"+taskId+"/properties/skills/");
			conn.disconnect();
			JsonElement object = new JsonParser().parse(skill);
			if(object.isJsonObject()){

				List<String> skillsIds = getAsList(object.getAsJsonObject().get("skills").getAsString());
				if (skillsIds.stream().anyMatch(item -> item.equalsIgnoreCase(skillId))){
					skillsIds.remove(skillId);
					String listString = String.join(", ", skillsIds);
					kms.updateEntityProperty(taskId, new PayloadEntityProperty("skill", "string","skills", listString));
				}else {
					System.out.print("UUID does exist!");
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}



	public List<Task> getTasks(){
		List<Task> tasks = new ArrayList<Task>();
		WebClientConnection conn = new WebClientConnection(8080);
		try {
			conn.connect();
			String allentitites = conn.sendByGet(kms.getURI()+"/tasks/");
			JsonElement objects = new JsonParser().parse(allentitites);
			if(objects.isJsonArray()){
				for (JsonElement element:  objects.getAsJsonArray()) {
					 JsonObject taskObj = element.getAsJsonObject();
					 JsonArray properties = element.getAsJsonObject().get("properties").getAsJsonArray();
					 String description = "", status = "", assignedId = " ", deadline = "";
					List<String> skillSet = null;
						for (JsonElement prop : properties) {
							JsonObject property = prop.getAsJsonObject();
							if (property.get("key").getAsString().equals("description")){
								description = property.get("value").getAsString();
							}else if (property.get("key").getAsString().equals("status")){
								status = property.get("value").getAsString();
							} else if (property.get("key").getAsString().equals("assigned")) {
								assignedId = property.get("value").getAsString();
							}else if (property.get("key").getAsString().equals("deadline")){
								deadline = property.get("value").getAsString();
							}else if (property.get("key").getAsString().equals("skills")){
								List<String> skillsIds = getAsList(property.get("value").getAsString());
								skillSet = skillsIds;
							}
						}
					tasks.add(new Task(taskObj.get("uuid").getAsString(),taskObj.get("name").getAsString(),description, status,assignedId,getRandomDate(),skillSet));
					}
				}
			conn.disconnect();
			return tasks;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}
	public CommonDate getRandomDate(){
		// assign a simple date
		Calendar c1 = Calendar.getInstance();
		c1.set(2020,11,05,10,0);
		Date dateOne = c1.getTime();
		return new CommonDate(dateOne);
	}

	public Task getTaskWithId(String taskId){
		if (taskId != null && !taskId.isEmpty() && !taskId.equals("\"\"")) {
			// get property
			WebClientConnection conn = new WebClientConnection(8080);
			try {
				conn.connect();
				String taskString = conn.sendByGet(kms.getURI() + "/tasks/" + taskId.replaceAll("^\"|\"$", ""));
				JsonElement object = new JsonParser().parse(taskString);
				if (object.isJsonObject()) {
					JsonObject taskObj = object.getAsJsonObject();
					JsonArray properties = taskObj.getAsJsonObject().get("properties").getAsJsonArray();
					String description = "", status = "", assignedId = " ", deadline = "";
					List<String> skillSet = null;

					for (JsonElement prop : properties) {
						JsonObject property = prop.getAsJsonObject();
						if (property.get("key").getAsString().equals("description")){
							description = property.get("value").getAsString();
						}else if (property.get("key").getAsString().equals("status")){
							status = property.get("value").getAsString();
						} else if (property.get("key").getAsString().equals("assigned")) {
							assignedId = property.get("value").getAsString();
						}else if (property.get("key").getAsString().equals("deadline")){
							deadline = property.get("value").getAsString();
						}else if (property.get("key").getAsString().equals("skills")){
							List<String> skillsIds = getAsList(property.get("value").getAsString());
							skillSet = skillsIds;
						}
					}

					return new Task(taskObj.get("uuid").getAsString(),taskObj.get("name").getAsString(),description, status,assignedId,getRandomDate(),skillSet);
				}
				conn.disconnect();
			} catch (Exception e) {
				throw e;
			} finally {
				conn.disconnect();
			}
		}
		return null;
	}

	public List<Task> getAssignedTasksTo(String workerId){
		// first get worker propertises
		WebClientConnection conn = new WebClientConnection(8080);
		List<Task> tList = new ArrayList<Task>();
		try {
			conn.connect();
			String allentitites = conn.sendByGet(kms.getURI()+"/humans/"+workerId+"/properties/tasks");
			JsonElement objects = new JsonParser().parse(allentitites);
			if(objects.isJsonObject()){
				List<String> tlist = getAsList(objects.getAsJsonObject().get("value").getAsString());
				tlist.stream().forEach(item ->{
					Task task = getTaskWithId(item);
					if (task != null)
						tList.add(task);
				} );
			}
			conn.disconnect();

			return tList;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}
	public List<Task> getUnfinishedTasksOf(String workerId){
		if (workerId != null && !workerId.isEmpty() && !workerId.equals("\"\"")) {
			WebClientConnection conn = new WebClientConnection(8080);
			List<Task> tList = new ArrayList<Task>();
			try {
				conn.connect();
				String taskProperty = conn.sendByGet(kms.getURI() + "/humans/" + workerId + "/properties/tasks");
				conn.disconnect();
				JsonElement objects = new JsonParser().parse(taskProperty);
				if (objects.isJsonObject()) {
					if (objects.getAsJsonObject().get("value") != null) {
						String inValue = objects.getAsJsonObject().get("value").toString();
						if (!inValue.isEmpty()) {
							List<String> taskList = getAsList(inValue);
							if (taskList.size() > 0) {
								taskList.stream().forEach(item -> {
									Task task = getTaskWithId(item);
									if (task != null) {
										if (!task.getTaskStatus().equalsIgnoreCase("done")) {
											tList.add(task);
										}
									}
								});
							}
						}
					}
				}
				return tList;
			} catch (Exception e) {
				throw e;
			} finally {
				conn.disconnect();
			}

		}
		return null;
	}

	public List<Task> getUnfinishedTasks(){
		List<Task> alltasks = getTasks();
		return alltasks.stream().filter(item -> !item.getTaskStatus().equalsIgnoreCase("done")).collect(Collectors.toList());
	}

	public List<Skill> getSkills(){
		List<Skill> skillList = new ArrayList<Skill>();
		WebClientConnection conn = new WebClientConnection(8080);
		try {
			conn.connect();
			String allEntities = conn.sendByGet(kms.getURI()+"/skills/");
			JsonElement objects = new JsonParser().parse(allEntities);
			if(objects.isJsonArray()){
				for (JsonElement element: objects.getAsJsonArray()) {
					skillList.add(getSkillById(element.getAsJsonObject().get("uuid").getAsString()));
				}
			}
			conn.disconnect();
			return skillList;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}

	// what are th re
	public List<Skill> getRequiredSkills(Task task){
		List<Skill> skills = new ArrayList<>();
		if (task.getSkillIds() != null ) {
			for (String item: task.getSkillIds()){
				if (item != null && !item.equalsIgnoreCase("")) {
					skills.add(getSkillById(item));
				}
			}
		}
		return skills;
	}

	public List<Skill> getRequiredSkills(String taskId){
		// get task
		List<Skill> skills = new ArrayList<Skill>();
		if (taskId != null && !taskId.isEmpty()){
		Task task = getTaskWithId(taskId);
			if (task != null){
				task.getSkillIds().stream().forEach(item -> {
					skills.add(getSkillById(item));
				});
			}
		}
		return skills;
	}

	public List<Worker> getAllWorkers(){

		List<Worker> workers = new ArrayList<Worker>();
		WebClientConnection conn = new WebClientConnection(8080);
		try {
			conn.connect();
			String allEntities = conn.sendByGet("http://chariot-km.dai-lab.de:8080/v1/humans/");
			//System.out.print("url:"+allEntities);
			JsonElement objects = new JsonParser().parse(allEntities);

			if(objects.isJsonArray()){
				for (JsonElement element:  objects.getAsJsonArray()) {
					JsonObject hobj = element.getAsJsonObject();
					Worker worker = getWorkerWithUUID(hobj.get("uuid").getAsString());
					Gson gson = new Gson();
					PayloadEntity entity = gson.fromJson(hobj, PayloadEntity.class);
					Arrays.stream(entity.getProperties()).forEach(property -> {
						if(property.getKey().equalsIgnoreCase("account")){
							String[] credentials = property.getValue().toString().split(",");
							worker.setWorkerID(credentials[0]);
							worker.setPassword(credentials[1]);
						}else if(property.getKey().equalsIgnoreCase("name")){
							worker.setName(property.getValue().toString());
						}else if(property.getKey().equalsIgnoreCase("tasks")){
							List<String> tasks = Arrays.asList(property.getValue().toString().split(","));
							worker.setTasks(tasks);
						}else if(property.getKey().equalsIgnoreCase("skills")){
							List<String> skills = Arrays.asList(property.getValue().toString().split(","));
							worker.setTasks(skills);
						}
					});
					workers.add(worker);
				}
			}
			conn.disconnect();

			return workers;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}
	public Human[] getWorkers(){

		List<Human> humans = new ArrayList<Human>();
		//System.out.print(kms.getAllEntities());
		WebClientConnection conn = new WebClientConnection(8080);

		try {
			conn.connect();
			String allEntities = conn.sendByGet(kms.getURI()+"/humans/");
			conn.disconnect();
			JsonElement objects = new JsonParser().parse(allEntities);

			if(objects.isJsonArray()){
				for (JsonElement element:  objects.getAsJsonArray()) {
					JsonObject hobj = element.getAsJsonObject();
					Human human = getHumanWithUUID(hobj.get("uuid").getAsString());
					humans.add(human);
				}
			}

			Human[] hlist = new Human[humans.size()];
			for(int i = 0 ; i < humans.size(); i++ ){
				hlist[i] = humans.get(i);
			}
			return hlist;
		} catch (Exception e) {
				throw e;
			} finally {
			conn.disconnect();
		}
	}

	public Worker getWorkerWithUsername(String username) {
		List<Worker> workers = getAllWorkers();
		for (Worker worker : workers) {
			if (worker.getWorkerID().equals(username))
				return worker;
		}
		return null;
	}

	public Worker getWorkerWithUUID(String uuid){
		if (uuid != null  && !uuid.isEmpty() && !uuid.equals("\"\"")) {
			Worker worker = new Worker();
			WebClientConnection conn = new WebClientConnection(8080);
			try {
				conn.connect();
				String humanEntity = conn.sendByGet(kms.getURI() + "/humans/" + uuid);
				conn.disconnect();
				JsonElement element = new JsonParser().parse(humanEntity);

				if (element.isJsonObject()) {
					worker.setUuid(uuid);
					Gson gson = new Gson();
					PayloadEntity entity = gson.fromJson(element, PayloadEntity.class);

					for(Property property: entity.getProperties()){
						if(property.getKey().equalsIgnoreCase("account")){
							String[] credentials = property.getValue().toString().split(",");
							worker.setWorkerID(credentials[0]);
							worker.setPassword(credentials[1]);
						}else if(property.getKey().equalsIgnoreCase("name")){
							worker.setName(property.getValue().toString());
						}else if(property.getKey().equalsIgnoreCase("tasks")){
							List<String> tasks = new ArrayList<String>(Arrays.asList(property.getValue().toString().split(",")));
							// set here worker.setTasks(tasks);
						}else if(property.getKey().equalsIgnoreCase("skills")){
							List<String> skills = new ArrayList<String>(Arrays.asList(property.getValue().toString().split(",")));
						}else if(property.getKey().equalsIgnoreCase("preferences")){
							List<String> preferences = new ArrayList<String>(Arrays.asList(property.getValue().toString().split(",")));
						}else if(property.getKey().equalsIgnoreCase("permissions")){
							List<String> permissions = new ArrayList<String>(Arrays.asList(property.getValue().toString().split(",")));
						}
					}
				}

				return worker;
			} catch (Exception e) {
				throw e;
			} finally {
				conn.disconnect();
			}
		}
		return null;
	}

	public Human getHumanWithUUID(String uuid) {
		if (uuid != null && !uuid.isEmpty() && !uuid.equals("\"\"")) {
			Human human = null;
			WebClientConnection conn = new WebClientConnection(8080);
			try {
				conn.connect();
				String humanEntity = conn.sendByGet(kms.getURI() + "/humans/" + uuid);
				conn.disconnect();
				JsonElement element = new JsonParser().parse(humanEntity);
				if (element.isJsonObject()) {
					Gson gson = new Gson();
					PayloadEntity entity = gson.fromJson(element, PayloadEntity.class);

					human = new HumanBuilder()
							.setUuid(UUID.fromString(entity.getUuid()))
							.setName(entity.getName())
							.build();

					List<UUID> uuidList;
					List<String> list;

					for (Property property : entity.getProperties()) {
						human.addProperty(property);
						if (property.getKey().equalsIgnoreCase("tasks")) {
							if(!property.getValue().toString().equals("")){
								list = Arrays.asList(property.getValue().toString().replaceAll("\\s+", "").split(","));
								uuidList = list.stream().map(item -> UUID.fromString(item)).collect(Collectors.toList());
								human.setSkills(uuidList);
							}
						} else if (property.getKey().equalsIgnoreCase("skills")) {
							if(!property.getValue().toString().equals("")){
								list = Arrays.asList(property.getValue().toString().replaceAll("\\s+", "").split(","));
								uuidList = list.stream().map(item -> UUID.fromString(item)).collect(Collectors.toList());
								human.setSkills(uuidList);
							}

						} else if (property.getKey().equalsIgnoreCase("preferences")) {
							if(!property.getValue().toString().equals("")) {
								list = Arrays.asList(property.getValue().toString().replaceAll("\\s+", "").split(","));
								uuidList = list.stream().map(item -> UUID.fromString(item)).collect(Collectors.toList());
								human.setPreferences(uuidList);
							}
						} else if (property.getKey().equalsIgnoreCase("permissions")) {
							if(!property.getValue().toString().equals("")) {
								list = Arrays.asList(property.getValue().toString().replaceAll("\\s+", "").split(","));
								uuidList = list.stream().map(item -> UUID.fromString(item)).collect(Collectors.toList());
								human.setPermissions(uuidList);
							}
						}
					}
				}
				return human;
			} catch (Exception e) {
				throw e;
			} finally {
				conn.disconnect();
			}
		}
		return null;
	}

	public List<Worker> getWorkerBySkills(List<Skill> skills){
		Human[] humans = getWorkers();
		List<Human> hlist = new ArrayList<Human>();
		List<Worker> wlist = new ArrayList<Worker>();
		Worker worker = null;
 		for (Human human: humans){
			List<UUID> skillList = human.getSkills();
			List<UUID> inSkills = new ArrayList<UUID>();
			if (skills != null) {
				skills.stream().forEach(item -> {
					inSkills.add(UUID.fromString(item.getId()));
				});

				if (skillList.containsAll(inSkills)) {
					worker = new Worker();
					worker.setUuid(human.getUUIdentifier().toString());

					for(Property property: human.getProperties()) {
						if (property.getKey().equalsIgnoreCase("account")) {
							String[] credentials = property.getValue().toString().split(",");
							worker.setWorkerID(credentials[0]);
							worker.setPassword(credentials[1]);
						}else if(property.getKey().equalsIgnoreCase("name")){
							worker.setName(property.getValue().toString());
						}else if(property.getKey().equalsIgnoreCase("tasks")){
							List<String> tasks = Arrays.asList(property.getValue().toString().split(","));
							worker.setTasks(tasks);
						}
					}

					hlist.add(human);
					wlist.add(worker);
				}
			}else{
				worker = new Worker();
				worker.setUuid(human.getUUIdentifier().toString());
				for(Property property: human.getProperties()) {
					if (property.getKey().equalsIgnoreCase("account")) {
						String[] credentials = property.getValue().toString().split(",");
						worker.setWorkerID(credentials[0]);
						worker.setPassword(credentials[1]);
					}else if(property.getKey().equalsIgnoreCase("name")){
						worker.setName(property.getValue().toString());
					}else if(property.getKey().equalsIgnoreCase("tasks")){
						List<String> tasks = Arrays.asList(property.getValue().toString().split(","));
						worker.setTasks(tasks);
					}
				}
				hlist.add(human);
				wlist.add(worker);
			}
		}

		return wlist;
	}

	public List<Human> getHumansBySkillIds(List<Skill> skills){
		Human[] humans = getWorkers();
		List<Human> list = new ArrayList<Human>();
		List<UUID> incomingSkills = new ArrayList<UUID>();
		skills.stream().forEach(skill ->{
			incomingSkills.add(UUID.fromString(skill.getId()));
		});
		Arrays.stream(humans).forEach(human ->{
			List<UUID> skillList = human.getSkills();
			if (skillList.containsAll(incomingSkills)){
				list.add(human);
			}
		});

		return list;
	}

	public List<Worker> getWorkerBySkillIds(List<Skill> skills){
		Human[] humans = getWorkers();
		List<Human> hlist = new ArrayList<Human>();
		List<UUID> incomingSkills = new ArrayList<UUID>();
		skills.stream().forEach(skill ->{
			incomingSkills.add(UUID.fromString(skill.getId()));
		});
		List<Worker> wlist = new ArrayList<Worker>();
		Arrays.stream(humans).forEach(human ->{
			List<UUID> skillList = human.getSkills();
			if (skillList.containsAll(incomingSkills)){
				hlist.add(human);
				wlist.add(new Worker(human.getUUIdentifier().toString(),human.getName(),"password","name","surname",null));
			}
		});
//		list.stream().forEach(human -> {
//			wlist.add(new Worker(human.getUUIdentifier().toString(),human.getUUIdentifier().toString(),"password","name","surname",null));
//		});

		return wlist;
	}
	public List<Skill> getSkillsOf(Human human){
		List<UUID> list = human.getSkills();
		List<Skill> humanSkills = new ArrayList<Skill>();
		for (UUID item: list){
			humanSkills.add(getSkillById(item.toString()));
		}
		return humanSkills;
	}
	public List<Skill> getSkillsOf(Worker worker){
		Human human = getHumanWithUUID(worker.getUuid());
		List<UUID> list = human.getSkills();
		List<Skill> humanSkills = new ArrayList<Skill>();
		for (UUID item: list){
			humanSkills.add(getSkillById(item.toString()));
		}
		return humanSkills;
	}

	public Skill getSkillWithId(String getID) {
		// TODO optimization
		for (Skill skill : getSkills()) {
			if (skill.getId().equals(getID)) {
				return skill;
			}
		}
		return null;
	}
	public Skill getSkillById(String id){
		if (id != null && !id.equalsIgnoreCase("")) {
			Skill skill = null;
			WebClientConnection conn = new WebClientConnection(8080);
			try {
				conn.connect();
				String skillStr = conn.sendByGet(kms.getURI() + "/skills/" + id);
				conn.disconnect();
				JsonElement element = new JsonParser().parse(skillStr);
				if (element.isJsonObject()) {
					JsonObject obj = element.getAsJsonObject();
					JsonArray prop = element.getAsJsonObject().get("properties").getAsJsonArray();
					skill = new Skill(obj.get("uuid").getAsString());
					for (JsonElement property : prop.getAsJsonArray()) {
						if (property.getAsJsonObject().get("key").getAsString().equalsIgnoreCase("name")) {
							skill.setName(property.getAsJsonObject().get("value").getAsString());
						} else if (property.getAsJsonObject().get("key").getAsString().equalsIgnoreCase("description")) {
							skill.setDesc(property.getAsJsonObject().get("value").getAsString());
						}
					}
				}
				return skill;
			} catch (Exception e) {
				throw e;
			} finally {
				conn.disconnect();
			}
		}
		return null;
	}


	public boolean addSkill(String workerId, String skillId) {
		WebClientConnection conn = new WebClientConnection(8080);
		try {
			conn.connect();
			String allentitites = conn.sendByGet(kms.getURI()+"/humans/"+workerId+"/properties/skills");
			conn.disconnect();

			JsonElement objects = new JsonParser().parse(allentitites);
			if(objects.isJsonObject()){
				if (objects.getAsJsonObject().get("value") != null) {
					List<String> tList = getAsList(objects.getAsJsonObject().get("value").getAsString());

					tList.add(skillId);
					String listString = String.join(", ", tList);
					kms.updateEntityProperty(workerId, new PayloadEntityProperty(IoTEntity.HUMAN, "string","skills", listString));
				}
			}
			return true;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.disconnect();
		}
	}

	public void updateTask(Task task) {
		System.out.println("current task status:"+task.getTaskStatus());
		PayloadEntityProperty property2 = new PayloadEntityProperty(IoTEntity.TASK, "string","skills",task.getSkillIdsAsString());
		PayloadEntityProperty property3 = new PayloadEntityProperty(IoTEntity.TASK, "string","assigned",task.getAssignedID());
		PayloadEntityProperty property4 = new PayloadEntityProperty(IoTEntity.TASK, "string","status",task.getTaskStatus());
//		PayloadEntityProperty property4 = new PayloadEntityProperty("task", "string","description",task.getTaskDesc());
		property2.setWritable(true);property3.setWritable(true);property4.setWritable(true);

		kms.updateEntityProperty(task.getTaskID(),property2);
		kms.updateEntityProperty(task.getTaskID(),property3);
		kms.updateEntityProperty(task.getTaskID(),property4);
	}

	public List<Task> getTasksWithStatus(String valueStatusUnassigned) {
		// find tasks with the matching tasks
		List<Task> tasks = getTasks();
		tasks = tasks.stream().filter(task -> task.getTaskStatus().equals(valueStatusUnassigned)).collect(Collectors.toList());
		return tasks;
	}


	public boolean addHealthData(HealthData hdata){
		//System.out.println(hdata.toString());
		return false;
	}
	public HealthData[] getHealthData(Human human){
		return null;
	}
	public HealthData[] getHealthData(Worker human){
		return null;
	}
}
