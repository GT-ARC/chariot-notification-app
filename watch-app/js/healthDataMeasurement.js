var HealthHandler = {
	interval: 5000,
	
	STATUS_ZERO: 100,
	STATUS_VALID: 101,
	STATUS_SEND: 102,
	STATUS_STOP: 103,
	
	STATUS_WORK_UNDEFINED: "undefined",
	STATUS_WORK_REST: "resting",
	STATUS_WORK_DESKTOP: "desktop",
	STATUS_WORK_EASY: "easy",
	STATUS_WORK_HARD: "hard",
	
	STATUS_FEEL_UNDEFINED: "undefined",
	STATUS_FEEL_GOOD: "good",
	STATUS_FEEL_HAPPY: "happy",
	STATUS_FEEL_STRESS: "stress",
	STATUS_FEEL_BAD: "bad",
	
	readingStatus: 103,
	lastStepStatus: "non",
	lastHeartRate: 0,
	lastMeasuredTime: "",
	lastWorkStatus: "undefined",
	lastFeelStatus: "undefined",
	
	MAX_READ_COUNT: 10,
	sumRead: 0,
	countRead: 0,
	
	start: function() {
		showPopup(domFeelPopup);
		
		HealthHandler.readingStatus = HealthHandler.STATUS_ZERO;
		
		tizen.humanactivitymonitor.stop('HRM');
		tizen.humanactivitymonitor.start('PEDOMETER');
		setTimeout(this.onTimer, this.interval);
	},
	stop: function() {
		HealthHandler.readingStatus = HealthHandler.STATUS_STOP;
		
		tizen.humanactivitymonitor.stop('HRM');
		tizen.humanactivitymonitor.stop('PEDOMETER');
	},
	onTimer: function() {
		if(HealthHandler.readingStatus !== HealthHandler.STATUS_STOP){
			HealthHandler.readingStatus = HealthHandler.STATUS_ZERO;
			
			tizen.humanactivitymonitor.start('HRM', HealthHandler.onChanged, function(err){
				console.log(err);
			});
		}
	},
	onChanged: function(hrmInfo) {
		var heartRate = hrmInfo.heartRate;
		
		tizen.humanactivitymonitor.getHumanActivityData('PEDOMETER', function(step){
			HealthHandler.lastStepStatus = step.stepStatus;
		});
		if(HealthHandler.readingStatus === HealthHandler.STATUS_ZERO){
			if(heartRate > 0){
				HealthHandler.countRead += 1;
				HealthHandler.sumRead += heartRate;
			}
			
			if(HealthHandler.countRead >= HealthHandler.MAX_READ_COUNT){
				HealthHandler.readingStatus = HealthHandler.STATUS_VALID;
				heartRate = Math.floor(HealthHandler.sumRead / HealthHandler.countRead);
				HealthHandler.countRead = 0;
				HealthHandler.sumRead = 0;
			}
		}
		
		if(HealthHandler.readingStatus === HealthHandler.STATUS_VALID){
			tizen.humanactivitymonitor.stop('HRM');
			setTimeout(HealthHandler.onTimer, HealthHandler.interval);
			
			var d = new Date();
			HealthHandler.readingStatus = HealthHandler.STATUS_SEND;
			HealthHandler.lastHeartRate = heartRate;
			HealthHandler.lastMeasuredTime = ("0" + d.getDate()).slice(-2) + "-" + ("0"+(d.getMonth()+1)).slice(-2) + "-" +
		    d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2) + 
		    ":" + ("0" + d.getSeconds()).slice(-2);
			var data = HealthDataTemplate.toJson(HealthHandler.lastMeasuredTime, HealthHandler.lastHeartRate,
					HealthHandler.lastStepStatus, HealthHandler.lastFeelStatus, HealthHandler.lastWorkStatus);
			HealthHandler.fire(data);
			
			
		}
	},
	addListener: function(fn) {
		HealthHandler.listeners.push(fn);
	},
	fire: function(hr) {
		HealthHandler.dataBuffer.push(hr);
		var scope = window;
		HealthHandler.listeners.forEach(function(item) {
			if(item != null)
				item.call(scope, hr);
		});
	},
	listeners: [],
	dataBuffer: []
}


/*
 * Health popup
 */
var domFeelGoodButton = document.getElementById('health-feel-good');
var domFeelHappyButton = document.getElementById('health-feel-happy');
var domFeelBadButton = document.getElementById('health-feel-bad');
var domFeelStressButton = document.getElementById('health-feel-stress');

var domWorkRestButton = document.getElementById('health-work-rest');
var domWorkDesktopButton = document.getElementById('health-work-desktop');
var domWorkEasyButton = document.getElementById('health-work-easy');
var domWorkHardButton = document.getElementById('health-work-hard');

var domFeelPopup = document.getElementById('feel-popup');
var domWorkPopup = document.getElementById('work-popup');

function closeFeelPopup(){
	showPopup(domWorkPopup);
}

function closeWorkPopup(){
	closePopup(domWorkPopup);
}

domFeelGoodButton.addEventListener('click', function(){
	HealthHandler.lastFeelStatus = HealthHandler.STATUS_FEEL_GOOD;
	closeFeelPopup();
});
domFeelBadButton.addEventListener('click', function(){
	HealthHandler.lastFeelStatus = HealthHandler.STATUS_FEEL_BAD;
	closeFeelPopup();
});
domFeelStressButton.addEventListener('click', function(){
	HealthHandler.lastFeelStatus = HealthHandler.STATUS_FEEL_STRESS;
	closeFeelPopup();
});
domFeelHappyButton.addEventListener('click', function(){
	HealthHandler.lastFeelStatus = HealthHandler.STATUS_FEEL_HAPPY;
	closeFeelPopup();
});

domWorkRestButton.addEventListener('click', function(){
	HealthHandler.lastWorkStatus = HealthHandler.STATUS_WORK_REST;
	closeWorkPopup();
});
domWorkEasyButton.addEventListener('click', function(){
	HealthHandler.lastWorkStatus = HealthHandler.STATUS_WORK_EASY;
	closeWorkPopup();
});
domWorkHardButton.addEventListener('click', function(){
	HealthHandler.lastWorkStatus = HealthHandler.STATUS_WORK_HARD;
	closeWorkPopup();
});
domWorkDesktopButton.addEventListener('click', function(){
	HealthHandler.lastWorkStatus = HealthHandler.STATUS_WORK_DESKTOP;
	closeWorkPopup();
});
