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
import de.gtarc.server.pages.AbstractPage;
import de.gtarc.server.service.KMSService;

@WebServlet(value = "/task")
public class SkillPage extends AbstractPage {

	private static final long serialVersionUID = 8769567768744943314L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.print(this.getClass().getName()+"is called");
		KMSService db = null;
		try {
			db = KMSService.getInstance();
			String getID = request.getParameter("taskID");
			if (getID == null || getID.equals("")) {
				response.sendRedirect("/error");
				return;
			}
			Task task = db.getTaskWithId(getID);
			if (task == null) {
				response.sendRedirect("/error");
				return;
			}

			StringWriter w = new StringWriter();
			Mustache m = new DefaultMustacheFactory().compile("templates/task.mustache");
			Map<String, Object> context = new HashMap<>();
			List<Skill> skills = db.getRequiredSkills(task);
			context.put("skill", skills);

			List<Task> tasks = Arrays.asList(task);
			context.put("task", tasks);

			m.execute(w, context).flush();

			super.doSend(request, response, w.toString());


		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
