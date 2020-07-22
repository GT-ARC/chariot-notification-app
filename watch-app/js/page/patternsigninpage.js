var enterusername = "username";
var enterpassword = "password";
var enterpasswordagain = "passwordagain";
var patternstate = enterusername;

var firstpassword;

var domUsernamePattern = document.getElementById("usernamepattern");
var domPasswordPattern = document.getElementById("passwordpattern");
var domEnterData = document.getElementById("signin_enter");

document.getElementById("signin_button_enter").addEventListener('click', function() {
	domEnterData.style.display = "none";
	domUsernamePattern.style.display = "block";
	
	alert("Enter username");
});

var usernamepattern = new PatternLock("#usernamepattern", {
	onPattern: function(pattern) {
		onPattern();
	}
});

var passwordpattern = new PatternLock("#passwordpattern", {
	onPattern: function(pattern) {
		onPattern();
	}
});

function onPattern(){
	if(patternstate === enterusername){
		domUsernamePattern.style.display = "none";
		domPasswordPattern.style.display = "block";
		patternstate = enterpassword;
		alert("Enter password");
	} else if(patternstate === enterpassword) {
		firstpassword = "" + passwordpattern.getPattern();
		passwordpattern.clear();
		patternstate = enterpasswordagain;
		alert("Enter your password again!");
	} else if(patternstate === enterpasswordagain){
		if(!(firstpassword === passwordpattern.getPattern())){
			passwordpattern.clear();
			patternstate = enterpassword;
			alert("Password mismatch, please retry!");
			return;
		}
		
		var username = "" + usernamepattern.getPattern();
		var password = firstpassword;
		var name = document.getElementById("signin_name").value;
		var surname = document.getElementById("signin_surname").value;
		var birthdate = document.getElementById("signin_birthdate").value;
		var skills = [];
		
		var registerRequest = WorkerRequestTemplate.toJson(username, password, name, surname, birthdate, skills); 
		domPasswordPattern.style.display = "none";
		
		Connectivity.sendMessage(registerRequest);
		Connectivity.loginRequest(username, password);
	}
}

function retry(){
	patternstate = enterusername;
	usernamepattern.clear();
	passwordpattern.clear();
	
	domUsernamePattern.style.display = "none";
	domPasswordPattern.style.display = "none";
	domEnterData.style.display = "block";
	
	alert("Enter your data");
}

retry();