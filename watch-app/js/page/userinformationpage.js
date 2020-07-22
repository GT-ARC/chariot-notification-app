document.getElementById("user-info-id").innerHTML = workerInformation.worker_id;
document.getElementById("user-info-name").innerHTML = workerInformation.worker_name;
document.getElementById("user-info-surname").innerHTML = workerInformation.worker_surname;
document.getElementById("user-info-birthdate").innerHTML = workerInformation.worker_birthdate;
document.getElementById("user-info-skills").innerHTML = "";
document.getElementById("user-info-auths").innerHTML = "";

workerInformation.worker_skills.forEach(function(skill){
	document.getElementById("user-info-skills").innerHTML += " - " + skill;
});
workerInformation.worker_authorisations.forEach(function(auth){
	document.getElementById("user-info-auths").innerHTML += " - " + auth;
});

document.getElementById("user-update-button").addEventListener('click', function(){
	var info_type = InformationRequestTemplate.VALUE_TYPE_WORKER;
	var info_id = Connectivity.username;
	var message = InformationRequestTemplate.toJson(info_type, info_id);
	Connectivity.sendMessage(message);
});