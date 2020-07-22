var tasklist = {
	tasks: [],
	addTask: function(task) {
		//Check if task id exists
		var exists = false;
		for(var i = 0; i<this.tasks.length; i++){
			var t = this.tasks[i];
			if(t[TaskInformationTemplate.TAG_ID] == task[TaskInformationTemplate.TAG_ID]){
				exists = true;
				this.tasks[i] = task;
			}
		}
		if(!exists){
			this.tasks.push(task);
			showAlert(NotificationStrings.NEW_TASK_INCOME, function(){
				tasklist.showInfo(tasklist.tasks.length - 1)
			});
		}
	},
	updateUI: function() {
		var ulTaskList = document.getElementById("ulTaskList");
		ulTaskList.innerHTML = "";
		
		for(var i=0; i<this.tasks.length; i++){
			var task = this.tasks[i];
			var li = document.createElement("li");
			li.setAttribute("id", task.id);
			li.innerHTML = task.name;
			li.setAttribute("onclick", "tasklist.showInfo("+ i + ");");
			
			ulTaskList.appendChild(li);
		}
	},
	showInfo: function(index){
		this.selectedTask = this.tasks[index];
		Pages.change(Pages.PAGE_TASKINFORMATION);
	},
	selectedTask: {}
}

var workerInformation = {
	worker_id: "",
	worker_name: "",
	worker_surname: "",
	worker_birthdate: "",
	worker_authorisations: [],
	worker_skills: []
}

function resetUserData() {
	tasklist.tasks = [];
	selectedTask = {};
	HealthHandler.dataBuffer = [];
	
	workerInformation.worker_id = "";
	workerInformation.worker_name = "";
	workerInformation.worker_surname = "";
	workerInformation.worker_birthdate = "";
	workerInformation.worker_skills = [];
	workerInformation.worker_authorisations = [];
}