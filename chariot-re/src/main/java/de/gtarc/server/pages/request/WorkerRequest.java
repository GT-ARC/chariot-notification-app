package de.gtarc.server.pages.request;

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

import de.gtarc.server.entity.Skill;
import de.gtarc.server.pages.AbstractPage;
import de.gtarc.server.service.KMSService;

@WebServlet(value = "/workerrequest")
public class WorkerRequest extends AbstractPage {

	private static final long serialVersionUID = 8769567768744943314L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		KMSService db = null;
		try {
			db = KMSService.getInstance();
			StringWriter w = new StringWriter();
			Mustache m = new DefaultMustacheFactory().compile("templates/workerRequest.mustache");
			List<Skill> skills = db.getSkills();
			Map<String, Object> context = new HashMap<>();
			context.put("skill", skills);
			m.execute(w, context).flush();

			super.doSend(request, response, w.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
