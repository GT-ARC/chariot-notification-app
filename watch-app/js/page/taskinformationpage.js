document.getElementById("task-info-id").innerHTML = tasklist.selectedTask[TaskInformationTemplate.TAG_ID];
document.getElementById("task-info-name").innerHTML = tasklist.selectedTask[TaskInformationTemplate.TAG_NAME];
document.getElementById("task-info-status").innerHTML = tasklist.selectedTask[TaskInformationTemplate.TAG_STATUS];
document.getElementById("task-info-assigned").innerHTML = tasklist.selectedTask[TaskInformationTemplate.TAG_ASSIGNED];
document.getElementById("task-info-description").innerHTML = tasklist.selectedTask[TaskInformationTemplate.TAG_DESCRIPTION];
document.getElementById("task-info-deadline").innerHTML = tasklist.selectedTask[TaskInformationTemplate.TAG_DEADLINE];
document.getElementById('task-complete-button').addEventListener('click', function() {
	var task_id = document.getElementById("task-info-id").innerHTML;
	var message = TaskCompletedTemplate.toJson(task_id);
	Connectivity.sendMessage(message);
});

//set button passive if task is done
if(tasklist.selectedTask[TaskInformationTemplate.TAG_STATUS] === "done"){
	document.getElementById('task-complete-button').style.display="none";
}