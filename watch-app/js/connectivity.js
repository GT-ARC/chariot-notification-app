/**
 * This class handles all the connectivity features
 */
var Connectivity = {
	STATUS_LOGIN_IN: "logged_in",
	STATUS_LOGIN_OUT: "logged_out",
	STATUS_LOGIN_WAIT_RESPONSE: "login_wait_response",
	STATUS_CONNECTION_ESTABLISHED: "connection_established",
	STATUS_CONNECTION_NONE: "connection_no_connection",
	STATUS_CONNECTION_WAITING: "connection_waiting",
	
	WS_HOST_ADDRESS: "192.168.2.104:10001",
	WS_SOCKET_ADDRESS: "/websocket",
	
	login_status: null,
	connection_status: null,
	
	websocket: null,
	username: "",
	password: "",
	uuid: "",
	
	loginRequest: function(username, password) {
		console.log("username",username, "password", password)
		this.username = username;
		this.password = password;
		this.login_status = this.STATUS_LOGIN_WAIT_RESPONSE;
		
		if(this.uuid === ""){
			this.uuid = create_UUID();
		}
		
		var loginRequest = LoginTemplate.toJson(username, password,
				LoginTemplate.VALUE_TYPE_SMARTWATCH, this.uuid);
		
		this.sendMessage(loginRequest);
	},
	
	logout: function() {
		this.username = "";
		this.password = "";
		this.login_status = this.STATUS_LOGIN_OUT;
		
		var logoutMessage = LogoutTemplate.toJson();
		this.sendMessage(logoutMessage);
		
		resetUserData();
		HealthHandler.stop();
		Pages.change(Pages.PAGE_LOGIN);
	},
	
	openConnection: function(){
		var webSocketURL = "ws://" + this.WS_HOST_ADDRESS + this.WS_SOCKET_ADDRESS;
		
		this.connection_status = this.STATUS_CONNECTION_WAITING;
		this.websocket = new WebSocket(webSocketURL);
		
		if(this.websocket == null){
			console.log("No connection to the " + webSocketURL);
			this.connection_status = this.STATUS_CONNECTION_NONE;
			return;
		}
		
		this.websocket.onopen = function(msgEvent){
			console.log("Connection opened.");
			Connectivity.connection_status = Connectivity.STATUS_CONNECTION_ESTABLISHED;
			
			// Auto relogin
			if(Connectivity.login_status == Connectivity.STATUS_LOGIN_IN){
				showAlert("Reconnected. Relogin 2 second later!");
				setTimeout(function(){
					Connectivity.loginRequest(Connectivity.username, Connectivity.password);					
				}, 2000);
			}
		};

		this.websocket.onmessage = function(msgEvent){
			try {
				console.log("trying to decode");
				console.log(msgEvent);
				var text = msgEvent.data;
				var jsonObj = JSON.parse(text);
				Connectivity.receiveMessage(jsonObj);
			} catch (e) {
				console.log(e);
			}
			
		};

		this.websocket.onclose = function(msgEvent) {
			if(Connectivity.connection_status === Connectivity.STATUS_CONNECTION_ESTABLISHED){
				showAlert("Connection lost!");
			}
			Connectivity.connection_status = Connectivity.STATUS_CONNECTION_NONE;
			console.log('Socket is closed.');
			console.log('Reconnect will be attempted in 1 second.');
			setTimeout(Connectivity.openConnection(), 1000);
		};

		this.websocket.onerror = function(msgEvent) {
			Connectivity.connection_status = Connectivity.STATUS_CONNECTION_NONE;
			console.log('Socket Error!! ');
		};
	},
	
	sendMessage: function(json){
		console.log("Log: send message " + JSON.stringify(json));
		if(Connectivity.connection_status !== Connectivity.STATUS_CONNECTION_ESTABLISHED){
			showAler("No connection. The message could not be sent!");
			return false;
		}
		
		var jsonString = JSON.stringify(json);
		this.websocket.send(jsonString);
		return true;
	},
	
	receiveMessage: function(json) {
		console.log("Log: message income!");
		console.log(json);
		
		header = json[Templates.TAG_HEADER];
		
		switch(header){
		case ResponseTemplate.VALUE_HEADER:
			if(this.login_status == this.STATUS_LOGIN_WAIT_RESPONSE){
				if(json[ResponseTemplate.TAG_CODE] == ResponseTemplate.CODE_LOGIN_SUCCESSFULL){
					this.login_status = this.STATUS_LOGIN_IN;
					Pages.change(Pages.PAGE_HOME);
					
					//Get worker information
					var infoRequestType = InformationRequestTemplate.VALUE_TYPE_WORKER;
					var workerInfoRequest = InformationRequestTemplate.toJson(infoRequestType, this.username);
					this.sendMessage(workerInfoRequest);
				} else if(json[ResponseTemplate.TAG_CODE] == ResponseTemplate.CODE_LOGIN_FAILED) {
					this.login_status = this.STATUS_LOGIN_OUT;
				}
			}
			showAlert(json[ResponseTemplate.TAG_DETAIL]);
			break;
		case TaskInformationTemplate.VALUE_HEADER:
			tasklist.addTask(json);
			break;
		case WorkerInformationTemplate.VALUE_HEADER:
			workerInformation.worker_id = json[WorkerInformationTemplate.TAG_ID];
			workerInformation.worker_name = json[WorkerInformationTemplate.TAG_NAME];
			workerInformation.worker_surname = json[WorkerInformationTemplate.TAG_SURNAME];
			workerInformation.worker_birthdate = json[WorkerInformationTemplate.TAG_BIRTHDATE];
			workerInformation.worker_skills = json[WorkerInformationTemplate.TAG_SKILLS];
			workerInformation.worker_authorisations = json[WorkerInformationTemplate.TAG_AUTHS];
			break;
		}
	}
}

Connectivity.login_status = Connectivity.STATUS_LOGIN_OUT;
Connectivity.connection_status = Connectivity.STATUS_CONNECTION_NONE;
Connectivity.openConnection();

function create_UUID(){
	var dt = new Date().getTime();
	var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    	var r = (dt + Math.random()*16)%16 | 0;
        dt = Math.floor(dt/16);
        return (c=='x' ? r :(r&0x3|0x8)).toString(16);
	});
	return uuid;
}