package de.gtarc.server.pages.information;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import de.gtarc.server.entity.Task;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.pages.AbstractPage;
import de.gtarc.server.service.KMSService;

@WebServlet(value = "/listworkers")
public class ListWorkersPage extends AbstractPage {

	private static final long serialVersionUID = 8769567768744943314L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.print(this.getClass().getName()+"is called");
		KMSService db = null;
		try {
			db = KMSService.getInstance();
			StringWriter w = new StringWriter();
			Mustache m = new DefaultMustacheFactory().compile("templates/listWorkers.mustache");
			List<Worker> workers = db.getAllWorkers();
			Map<String, Object> context = new HashMap<>();
			context.put("worker", workers);
			m.execute(w, context).flush();

			super.doSend(request, response, w.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
}
