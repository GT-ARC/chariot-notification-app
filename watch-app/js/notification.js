/* This is old method but new one can only be used with Tizen OS 4.0 and above. */
/*YOU HAVE TO HAVE A WATCH ON YOUR WRIST IN ORDER FOR THE NOTIFICATION TO BE SHOWN*/
var NotificationStrings = {
		NEW_TASK_INCOME : "New task income!",
		CONNECNTION_LOST : "Connection lost!"
}

alertPopup = document.getElementById('alert-popup');

function showAlert(message){
	console.log("Log: (show alert) " + message);
	alertPopup.querySelector('#message').innerHTML = message;
	tau.openPopup(alertPopup);
	alertPopup.addEventListener('click', function onClick() {
		tau.closePopup(alertPopup);
	});
}

function showAlert(message, fn){
	console.log("Log: (show alert) " + message);
	alertPopup.querySelector('#message').innerHTML = message;
	tau.openPopup(alertPopup);
	var onclickFn = function() {
		alertPopup.removeEventListener('click', onclickFn);
		tau.closePopup(alertPopup);
		fn.call(window);
	};
	alertPopup.addEventListener('click', onclickFn);
}

function showPopup(popup){
	tau.openPopup(popup);
}

function closePopup(popup){
	tau.closePopup(popup);
}

function postNotification(type, notificationContent) {
	
	var notificationSample;
	var notificationP;
	console.log("Inside notification function! Notification CONTENT:" + notificationContent);
	try {
	//------------------------------------------------------------------------------------------------------------------	
		/* NOTIFICATION PROPERTY DEFINITION */
		notificationP = {
			content : notificationContent,
			iconPath : "./icon.png",
			soundPath: 'sounds/Beep-Once.wav',
			vibration : true,
			ledColor : "#FFFF00",
			ledOnPeriod : 1000,
			ledOffPeriod : 500
		};
	
		/* Defines type specific notificaion object */
		
		switch(type) {
	    case "connection":
	    		notificationSample = new tizen.StatusNotification("CONNECTIVITY", "CONNECTION INFO", notificationP);
	        break;
	    case "newTask":
	    		notificationSample = new tizen.StatusNotification("TASKS", "NEW TASK", notificationP);
	        break;
	    case "authorised":
	    		notificationSample = new tizen.StatusNotification("AUTHORISATIONS", "AUTH INFO", notificationP);
	        break;
	    default:
	    		notificationSample = new tizen.StatusNotification("TEST", "TEST NOTIFICATION", notificationP);
	}
		/* Post notification */
		tizen.notification.post(notificationSample);
	//------------------------------------------------------------------------------------------------------------------
	} catch (err) {
    	console.log(err.name + ": " + err.message);
	}
}