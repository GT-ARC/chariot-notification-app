var enterusername = "username";
var enterpassword = "password";
var patternstate = enterusername;

var usernamepattern = new PatternLock("#loginusernamepattern", {
	onPattern: function(pattern) {
		onPattern();
	}
});

var passwordpattern = new PatternLock("#loginpasswordpattern", {
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
		domRetryButton.style.display = "inline";
		domPasswordPattern.style.display = "none";
		Connectivity.loginRequest(usernamepattern.getPattern() + "", "" + passwordpattern.getPattern());
	}
}

var domUsernamePattern = document.getElementById("loginusernamepattern");
var domPasswordPattern = document.getElementById("loginpasswordpattern");
var domRetryButton = document.getElementById("patternretry");

function retryLogin(){
	patternstate = enterusername;
	usernamepattern.clear();
	passwordpattern.clear();
	
	domUsernamePattern.style.display = "block";
	domPasswordPattern.style.display = "none";
	domRetryButton.style.display = "none";
	
	alert("Enter username");
}

retryLogin();