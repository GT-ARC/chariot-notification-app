/**
 *  Send Message
 */
function doSendMessage() {
	var arraySkills = [];
	
	var checkBoxes = document.getElementsByName("skill");
	checkBoxes.forEach(function(element){
		if(element.checked == true)
			arraySkills.push(element.value);
	});
	
	var workerRequestMessage = WorkerRequestTemplate.toJson(
		document.getElementById("id").value,
		document.getElementById("password").value,
		document.getElementById("name").value,
		document.getElementById("surname").value,
		document.getElementById("birthdate").value,
		arraySkills
	);
	
 	var jsonString= JSON.stringify(workerRequestMessage);
  	console.log(workerRequestMessage);
  	
	webSocket.send(jsonString);
	txtAreaEcho.value += "Client : " + jsonString + "\n";
	jsonString = "";
};

var today = new Date();
var monthText = ("00" + (today.getMonth()+1)).slice(-2);
var dayText = ("00" + (today.getDate())).slice(-2);;
var currentTime = "2000" + '-'+monthText+'-'+dayText;
var birthdate = document.getElementById("birthdate");
birthdate.setAttribute("max", currentTime);
birthdate.value = currentTime;