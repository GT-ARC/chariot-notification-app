package de.gtarc.server.pages.insertion;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import de.gtarc.server.entity.Worker;
//import de.gtarc.server.entity.relation.HasSkill;
import de.gtarc.server.pages.AbstractPage;
import de.gtarc.server.pages.util.ErrorDiv;
import de.gtarc.server.pages.util.SuccessDiv;
import de.gtarc.server.service.KMSService;

@WebServlet(value = "/assignSkill")
public class AssignSkillPage extends AbstractPage {

	private static final long serialVersionUID = -1458928478061965887L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String getID = request.getParameter("workerID");
		Worker worker = null;
		try {
			worker = KMSService.getInstance().getWorkerWithUsername(getID);
			List<ErrorDiv> errors = new ArrayList<>();
			if (worker == null) {
				errors.add(new ErrorDiv("The worker does not exist!: " + getID));
				render(request, response, errors, null, null);
			} else {
				render(request, response, null, null, worker);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		KMSService db;

		try {
			db =  KMSService.getInstance();
			String getID = request.getParameter("workerID");
			Worker worker = KMSService.getInstance().getWorkerWithUsername(getID);
			List<ErrorDiv> errors = new ArrayList<>();
			List<SuccessDiv> success = new ArrayList<>();

			if (worker == null) {
				errors.add(new ErrorDiv("The worker does not exist!: " + getID));
				render(request, response, errors, null, null);
			} else {
				String[] skills = request.getParameterValues("skill");
				for (String skl : skills) {
					Skill skill = db.getSkillWithId(skl);
					if (skill != null) {
						if (db.addSkill(worker.getUuid(),skill.getId())) {
							success.add(new SuccessDiv("The skill is assigned: " + skill.getId()));
						} else {
							errors.add(new ErrorDiv("The skill could not be assigned: " + skill.getId()));
						}
					} else {
						errors.add(new ErrorDiv("The skill does not exist!: " + skl));
					}
				}
				render(request, response, errors, success, worker);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void render(HttpServletRequest request, HttpServletResponse response, List<ErrorDiv> errors,
			List<SuccessDiv> successes, Worker worker) throws IOException, URISyntaxException {
		StringWriter w = new StringWriter();
		Mustache m = new DefaultMustacheFactory().compile("templates/assignSkill.mustache");
		Map<String, Object> context = new HashMap<>();

		if (errors != null)
			context.put("error", errors);

		if (successes != null)
			context.put("success", successes);

		if (worker != null) {
			context.put("worker", worker);
			List<Skill> skills = KMSService.getInstance().getSkills();
			context.put("skill", skills);
		}

		m.execute(w, context);

		super.doSend(request, response, w.toString());
	}
}
