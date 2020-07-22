package de.gtarc.server;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(urlPatterns = "/init", loadOnStartup = 1)
public class InitialServlet extends HttpServlet {

	@Override
	public void init(ServletConfig config) {
	}
//	public void init(ServletConfig config) {
//		WebServer.instance();
//	}
}
