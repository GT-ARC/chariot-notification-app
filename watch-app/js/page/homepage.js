document.getElementById('logout-button').addEventListener('click', function() {
	Connectivity.logout();
});

document.getElementById('task-button').addEventListener('click', function() {
	Pages.change(Pages.PAGE_TASKLIST);
});

document.getElementById('user-button').addEventListener('click', function(){
	Pages.change(Pages.PAGE_USERINFORMATION);
});

document.getElementById('health-button').addEventListener('click', function(){
	Pages.change(Pages.PAGE_HEALTH);
});