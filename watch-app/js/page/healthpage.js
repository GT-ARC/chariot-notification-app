var domStartButton = document.getElementById('health-start-button');
var domSendButton = document.getElementById('health-send-button');
var domTimeInfo = document.getElementById('health-info-time');
var domHrInfo = document.getElementById('health-info-heartrate');
var domStepInfo = document.getElementById('health-info-stepstatus');
var domFeelInfo = document.getElementById('health-info-feelstatus');
var domWorkInfo = document.getElementById('health-info-workstatus');
var domWorkNFeelButton = document.getElementById('health-worknfeel-button');

var domHealthDiv = document.getElementById('health-page-content');

var healthEventListener = function(healthData){
	console.log(healthData);
	domTimeInfo.innerHTML = "" + healthData[HealthDataTemplate.TAG_TIME];
	domStepInfo.innerHTML = "" + healthData[HealthDataTemplate.TAG_STEP_STATUS];
	domHrInfo.innerHTML = "" + healthData[HealthDataTemplate.TAG_HEART_RATE];
	domFeelInfo.innerHTML = "" + HealthHandler.lastFeelStatus;
	domWorkInfo.innerHTML = "" + HealthHandler.lastWorkStatus;
	domSendButton.innerHTML = HealthHandler.dataBuffer.length + " SEND";
};

domStartButton.addEventListener('click', function(){
	if(HealthHandler.readingStatus === HealthHandler.STATUS_STOP){
		//showPopup(domFeelPopup);
		HealthHandler.addListener(healthEventListener);
		HealthHandler.start();
		domStartButton.innerHTML = "STOP";
	} else {
		HealthHandler.stop();
		domStartButton.innerHTML = "START";
	}
});

domSendButton.addEventListener('click', function() {
	if(Connectivity.connection_status !== Connectivity.STATUS_CONNECTION_ESTABLISHED
			|| HealthHandler.dataBuffer === []){
		alert("Health data could not be sent!");
	}
	else {
		var tempArray = HealthHandler.dataBuffer;
		HealthHandler.dataBuffer = [];
		
		tempArray.forEach(function(item) {
			Connectivity.sendMessage(item);
		});
	}
	domSendButton.innerHTML = HealthHandler.dataBuffer.length + " SEND";
});

domWorkNFeelButton.addEventListener('click', function(){
	showPopup(domFeelPopup);
});

if(HealthHandler.readingStatus === HealthHandler.STATUS_STOP){
	domStartButton.innerHTML = "START";
} else {
	domStartButton.innerHTML = "STOP";
}

domTimeInfo.innerHTML = "" + HealthHandler.lastMeasuredTime;
domHrInfo.innerHTML = "" + HealthHandler.lastHeartRate;
domStepInfo.innerHTML = "" + HealthHandler.lastStepStatus;
domFeelInfo.innerHTML = "" + HealthHandler.lastFeelStatus;
domWorkInfo.innerHTML = "" + HealthHandler.lastWorkStatus;
domSendButton.innerHTML = HealthHandler.dataBuffer.length + " SEND";