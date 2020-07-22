package de.gtarc.server.pages.information;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.*;

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
import de.gtarc.server.pages.util.ErrorDiv;
import de.gtarc.server.pages.util.SuccessDiv;
import de.gtarc.server.service.KMSService;

@WebServlet(value = "/listtasks")
public class ListTasksPage extends AbstractPage {

	private static final long serialVersionUID = -4402639837425648977L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.print(this.getClass().getName()+"is called");
		KMSService db = null;
		try {
			db = KMSService.getInstance();
			StringWriter w = new StringWriter();
			Mustache m = new DefaultMustacheFactory().compile("templates/listTasks.mustache");
			List<Task> tasks = db.getTasks();
			Map<String, Object> context = new HashMap<>();
			context.put("task", tasks);

			m.execute(w, context).flush();
			super.doSend(request, response, w.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		KMSService db;
		System.out.println("taskdId" + request.getParameter("taskID"));
		try {
			db =  KMSService.getInstance();
			String taskID = request.getParameter("taskID");
			KMSService.getInstance().deleteTask(UUID.fromString(taskID));
			Mustache m = new DefaultMustacheFactory().compile("templates/listTasks.mustache");

			List<Task> tasks = db.getTasks();
			Map<String, Object> context = new HashMap<>();
			context.put("task", tasks);
			StringWriter w = new StringWriter();
			m.execute(w, context);

			List<ErrorDiv> errors = new ArrayList<>();
			List<SuccessDiv> success = new ArrayList<>();

			//render(request, response, errors, success, tasks);
			super.doSend(request, response, w.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void render(HttpServletRequest request, HttpServletResponse response, List<ErrorDiv> errors,
						List<SuccessDiv> successes, List<Task> tasks) throws IOException, URISyntaxException {
		StringWriter w = new StringWriter();
		Mustache m = new DefaultMustacheFactory().compile("templates/assignSkill.mustache");
		Map<String, Object> context = new HashMap<>();

		if (errors != null)
			context.put("error", errors);

		if (successes != null)
			context.put("success", successes);

		if (tasks != null) {
			context.put("task", tasks);

		}

		m.execute(w, context);

		super.doSend(request, response, w.toString());
	}
}
