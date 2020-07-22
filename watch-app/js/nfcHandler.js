var adapter = tizen.nfc.getDefaultAdapter();
var modeListenerId = 0, aseListenerId = 0, transListenerId = 0;
var data;

if (adapter.powered) {
	console.log("NFC is already enabled");
}
else {
	adapter.cardEmulationMode = "ALWAYS_ON";
}
		
		 
modeListenerId = adapter.addCardEmulationModeChangeListener(function(mode) {
if (mode === "ALWAYS_ON") {
	//ready
}
});
				
var onSuccessCB = {onattach : function(nfcTag) {
	console.log("NFC Tag's type is " + nfcTag.type);
}, ondetach : function() {
	console.log("NFC Tag is detached");
}};
adapter.setTagListener(onSuccessCB);
		
		
		
aseListenerId = adapter.addActiveSecureElementChangeListener(function(seType)
{
	console.log("Active secure element is " + seType);
});
		
function onDetected(appletId, datas)
{
		 console.log("NFC secure element transaction detected. Application: " + appletId + ". Protocol data: " + datas);
};

transListenerId = adapter.addTransactionEventListener("UICC", onDetected);
		
	adapter.removeActiveSecureElementChangeListener(aseListenerId);
	adapter.removeTransactionEventListener(transListenerId);
	adapter.removeCardEmulationModeChangeListener(modeListenerId);
	adapter.cardEmulationMode = "OFF";

	// add eventListener for tizenhwkey
	document.addEventListener('tizenhwkey', function(e) {
		if(e.keyName == "back") {
			try {
				tizen.application.getCurrentApplication().exit();
			} catch (error) {
				console.error("getCurrentApplication(): " + error.message);
			}
		}
	});
