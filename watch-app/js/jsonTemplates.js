var Templates = {
	TAG_HEADER: "header",
	constructJson: function(){
		var object = new Object();
		object[this.TAG_HEADER] = arguments[0];
		
		var i;
		for(i=1; i+1<arguments.length; i += 2){
			object[arguments[i]] = arguments[i+1];
		}
		return object;
	}
}

var ResponseTemplate = {
	VALUE_HEADER: "response",
	TAG_CODE: "code",
	TAG_DETAIL: "detail",
	CODE_LOGIN_SUCCESSFULL: 100,
	CODE_LOGIN_FAILED: 101,
	CONNECTION_ESTABLISHED: 102,
	CONNECTION_REJECTED: 103,
	EXECUTION_COMPLETED: 104,
	EXECUTION_REJECTED: 105
}

var TaskInformationTemplate = {
	VALUE_HEADER: "task_information",
	TAG_ID: "id",
	TAG_NAME: "name",
	TAG_DESCRIPTION: "description",
	TAG_STATUS: "status",
	TAG_ASSIGNED: "assigned",
	TAG_REQUIREMENT: "requirement",
	TAG_DEADLINE: "deadline"
}

var WorkerInformationTemplate = {
	VALUE_HEADER: "worker_information",
	TAG_ID: "id",
	TAG_NAME: "name",
	TAG_SURNAME: "surname",
	TAG_BIRTHDATE: "birthdate",
	TAG_SKILLS: "skills",
	TAG_AUTHS: "auths"
	
}

var SkillInformationTemplate = {
	VALUE_HEADER: "skill_information",
	TAG_ID: "id",
	TAG_DESCRIPTION: "description"
}

var LoginTemplate = {
	VALUE_HEADER: "login",
	TAG_USERNAME: "username",
	TAG_PASSWORD: "password",
	TAG_DEVICE_TYPE: "type",
	TAG_DEVICE_ID: "id",
	VALUE_TYPE_SMARTWATCH: "smartwatch",
	VALUE_TYPE_OTHER: "other",
	toJson: function(username, password, type, id){
		return Templates.constructJson(this.VALUE_HEADER, 
				this.TAG_USERNAME, username, 
				this.TAG_PASSWORD, password, 
				this.TAG_DEVICE_TYPE, type, 
				this.TAG_DEVICE_ID, id);
	}
}

var LogoutTemplate = {
	VALUE_HEADER: "logout",
	toJson: function() {
		return Templates.constructJson(this.VALUE_HEADER);
	}
}

var TaskRequestTemplate = {
	VALUE_HEADER: "task_request",
	TAG_NAME: "name",
	TAG_DESCRIPTION: "description",
	TAG_REQUIREMENT: "requirement",
	TAG_DEADLINE: "deadline",
	toJson: function(name, description, deadline, requirements) {
		return Templates.constructJson(this.VALUE_HEADER, this.TAG_NAME, name, this.TAG_DESCRIPTION, description
				,this.TAG_DEADLINE, deadline, this.TAG_REQUIREMENT, requirements);
	}
}

var TaskCompletedTemplate = {
	VALUE_HEADER: "task_completed",
	TAG_ID: "id",
	toJson: function(task_id){
		return Templates.constructJson(this.VALUE_HEADER, this.TAG_ID, task_id);
	}
}

var WorkerRequestTemplate = {
	VALUE_HEADER: "worker_request",
	TAG_ID: "id",
	TAG_PASSWORD: "password",
	TAG_NAME: "name",
	TAG_SURNAME: "surname",
	TAG_BIRTHDATE: "birthdate",
	TAG_ACQUIRED_SKILLS: "acquired",
	toJson: function(id, password, name, surname, birthdate, acquired){
		return Templates.constructJson(this.VALUE_HEADER,
			this.TAG_ID, id,
			this.TAG_PASSWORD, password,
			this.TAG_NAME, name,
			this.TAG_SURNAME, surname,
			this.TAG_BIRTHDATE, birthdate,
			this.TAG_ACQUIRED_SKILLS, acquired
		);
	}
}

var InformationRequestTemplate = {
	VALUE_HEADER: "information_request",
	TAG_TYPE: "type",
	TAG_ID: "id",
	VALUE_TYPE_WORKER: "worker",
	VALUE_TYPE_SKILL: "skill",
	VALUE_TYPE_TASK: "task",
	toJson: function(type, id) {
		return Templates.constructJson(this.VALUE_HEADER,
			this.TAG_TYPE, type,
			this.TAG_ID, id
		);
	}
}

var HealthDataTemplate = {
	VALUE_HEADER: "health_data",
	TAG_TIME: "time",
	TAG_HEART_RATE: "heart_rate",
	TAG_STEP_STATUS: "step_status",
	TAG_FEEL_STATUS: "feel_status",
	TAG_WORK_STATUS: "work_status",
	toJson: function(time, heartRate, stepStatus, feelStatus, workStatus) {
		return Templates.constructJson(this.VALUE_HEADER,
			this.TAG_TIME, "" + time,
			this.TAG_HEART_RATE, "" + heartRate,
			this.TAG_STEP_STATUS, "" + stepStatus,
			this.TAG_FEEL_STATUS, "" + feelStatus,
			this.TAG_WORK_STATUS, "" + workStatus
		);
	}
}