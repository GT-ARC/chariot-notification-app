(function () {
	window.addEventListener("tizenhwkey", function (ev) {
		var activePopup = null,
			page = null,
			pageid = "";

		if (ev.keyName === "back") {
			Pages.back();
		}
	});
}());

var Pages = {
	PAGE_HOME: 'homePage.html',
	PAGE_LOGIN: 'loginPage.html',
	PAGE_TASKLIST: 'taskListPage.html',
	PAGE_TASKINFORMATION: 'taskInformation.html',
	PAGE_USERINFORMATION: 'userInformationPage.html',
	PAGE_PATTERN_LOGIN: 'loginPattern.html',
	PAGE_PATTERN_SIGNIN: 'signinPattern.html',
	PAGE_HEALTH: 'healthPage.html',
	
	back: function() {
		var activePopup = document.querySelector(".ui-popup-active");
		var page = document.getElementsByClassName("ui-page-active")[0];
		var pageid = page ? page.id : "";

		if (pageid === "home-page" && !activePopup
				&& Connectivity.login_status == Connectivity.STATUS_LOGIN_IN) {
			try {
				tizen.application.getCurrentApplication().hide();
			} catch (ignore) {
			}
		} else if (pageid === "login-page"){
			try {
				tizen.application.getCurrentApplication().exit();
			} catch (ignore) {
			}
		} else {
			window.history.back();
		}
	},
	
	change: function(page){
		console.log("Change page: " + page);
		switch(page){
		case this.PAGE_LOGIN:
		case this.PAGE_PATTERN_LOGIN:
		case this.PAGE_PATTERN_SIGNIN:
			if(Connectivity.login_status == Connectivity.STATUS_LOGIN_OUT){
				tau.changePage(page);
			}
			break;
		case this.PAGE_HOME:
		case this.PAGE_TASKLIST:
		case this.PAGE_TASKINFORMATION:
		case this.PAGE_USERINFORMATION:
		case this.PAGE_HEALTH:
			if(Connectivity.login_status == Connectivity.STATUS_LOGIN_IN){
				tau.changePage(page);
			}
			break;
		default:
		}
	}
}

window.setTimeout(function() {
	Pages.change(Pages.PAGE_LOGIN);
}, 1000);

(function() {
    var SCROLL_STEP = 40;       // distance of moving scroll for each rotary event

    document.addEventListener("pagebeforeshow", function pageScrollHandler(e) {
        var page = e.target;
        elScroller = page.querySelector(".ui-scroller");

        // rotary event handler
        rotaryEventHandler = function(e) {
            if (elScroller) {
                if (e.detail.direction === "CW") { // Right direction
                    elScroller.scrollTop += SCROLL_STEP;
                } else if (e.detail.direction === "CCW") { // Left direction
                    elScroller.scrollTop -= SCROLL_STEP;
                }
            }
        };

        // register rotary event.
        document.addEventListener("rotarydetent", rotaryEventHandler, false);

        // unregister rotary event
        page.addEventListener("pagebeforehide", function pageHideHanlder() {
            page.removeEventListener("pagebeforehide", pageHideHanlder, false);
            document.removeEventListener("rotarydetent", rotaryEventHandler, false);
        }, false);
    }, false);
}());