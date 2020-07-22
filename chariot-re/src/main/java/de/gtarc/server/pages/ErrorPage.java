package de.gtarc.server.pages;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

@WebServlet(urlPatterns = { "/error" })
public class ErrorPage extends AbstractPage {

	private static final long serialVersionUID = -4113983434114673232L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringWriter w = new StringWriter();
		Mustache m = new DefaultMustacheFactory().compile("templates/error.mustache");
		m.execute(w, new Page()).flush();

		super.doSend(request, response, w.toString());
	}

	private class Page {

	}
}
