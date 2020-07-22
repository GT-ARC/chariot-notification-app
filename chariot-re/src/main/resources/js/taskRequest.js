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
	
	var taskRequestMessage = TaskRequestTemplate.toJson(
		document.getElementById("name").value,
		document.getElementById("description").value,
		document.getElementById("deadline").value,
		arraySkills
	);
	
 	var jsonString= JSON.stringify(taskRequestMessage);
  	console.log(taskRequestMessage);
  	
	webSocket.send(jsonString);
	txtAreaEcho.value += "Client : " + jsonString + "\n";
	jsonString = "";
};

var today = new Date();
var monthText = ("00" + (today.getMonth()+1)).slice(-2);
var dayText = ("00" + (today.getDate())).slice(-2);
var hoursText = ("00" + (today.getHours())).slice(-2);
var minsText = ("00" + (today.getMinutes())).slice(-2);
var currentTime = today.getFullYear() + '-'+monthText+'-'+dayText+'T'+hoursText+':'+minsText;
var deadline = document.getElementById("deadline");
deadline.setAttribute("min", currentTime);
deadline.value = currentTime;