var hostaddress = window.location.host;
var socketadress = "/websocket";

var webSocket;
var txtAreaEcho;
window.onload = function() {
	 txtAreaEcho = document.getElementById("txtAreaEcho");
};

function doOpenConnection(){
	var base = "ws://" + hostaddress + socketadress;
	webSocket = new WebSocket(base);
	
	webSocket.onopen = function(msgEvent) { 
		txtAreaEcho.value += "Connected ... \n";
	};
	
	webSocket.onmessage = function(msgEvent){ 
		txtAreaEcho.value += "Server : " + msgEvent.data + "\n";
	};
	
	webSocket.onclose = function(msgEvent) {
		txtAreaEcho.value += "Disconnect ... \n";
	};
	
	webSocket.onerror = function(msgEvent) { 
		txtAreaEcho.value += "Error ... \n";
	};
	
	document.getElementById("ConnectionButton").value= "Reconnect";
};