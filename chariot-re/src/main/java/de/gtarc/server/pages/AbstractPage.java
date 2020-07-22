package de.gtarc.server.pages;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public abstract class AbstractPage extends HttpServlet {

	public final void doSend(HttpServletRequest request, HttpServletResponse response, String content)
			throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		Mustache m = new DefaultMustacheFactory().compile("templates/layout.mustache");
		Page page = new Page(content);
		m.execute(out, page);
	}

	private class Page {
		private String content;

		public Page(String content) {
			setContent(content);
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
}
