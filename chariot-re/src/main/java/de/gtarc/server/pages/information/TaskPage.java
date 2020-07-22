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

import de.gtarc.server.entity.Skill;
import de.gtarc.server.entity.Task;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.pages.AbstractPage;
import de.gtarc.server.service.KMSService;

@WebServlet(value = "/skill")
public class TaskPage extends AbstractPage {

	private static final long serialVersionUID = 8769567768744943314L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.print(this.getClass().getName()+"is called");
		KMSService db = null;
		try {
			db = KMSService.getInstance();
			String getID = request.getParameter("id");
			if (getID == null || getID.equals("")) {
				response.sendRedirect("/error");
				return;
			}

			Skill skill = db.getSkillById(getID);
			if (skill == null) {
				response.sendRedirect("/error");
				return;
			}

			StringWriter w = new StringWriter();
			Mustache m = new DefaultMustacheFactory().compile("templates/skill.mustache");
			Map<String, Object> context = new HashMap<>();

			List<Skill> skills = Arrays.asList(skill);
			context.put("skill", skills);

			List<Worker> workers = db.getWorkerBySkillIds(skills);
			context.put("worker", workers);

			m.execute(w, context).flush();

			super.doSend(request, response, w.toString());

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
}
