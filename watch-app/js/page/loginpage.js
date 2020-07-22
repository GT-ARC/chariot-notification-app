document.getElementById('login_button').addEventListener('click', function() {
	Pages.change(Pages.PAGE_PATTERN_LOGIN);
});

document.getElementById('signin_button').addEventListener('click', function(){
	Pages.change(Pages.PAGE_PATTERN_SIGNIN);
});

HealthHandler.stop();