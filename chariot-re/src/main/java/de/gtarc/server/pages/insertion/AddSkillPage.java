package de.gtarc.server.pages.insertion;

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
import de.gtarc.server.pages.AbstractPage;
import de.gtarc.server.pages.util.ErrorDiv;
import de.gtarc.server.pages.util.SuccessDiv;
import de.gtarc.server.service.KMSService;

@WebServlet(value = "/addskill")
public class AddSkillPage extends AbstractPage {

	private static final long serialVersionUID = -4402639837425648977L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		render(request, response, null, null);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String name = request.getParameter("name");
		String description = request.getParameter("desc");

		KMSService db = null;
		try {
			db = KMSService.getInstance();
			Skill exist = db.getSkillWithId(name);
			if (exist != null) {
				List<ErrorDiv> errors = new ArrayList<>();
				errors.add(new ErrorDiv("The skill already exist!"));
				render(request, response, errors, null);
				return;
			}
			Skill newSkill = new Skill(UUID.randomUUID().toString(),name, description);
			boolean result = db.addSkill(newSkill);

		if (result) {
			List<SuccessDiv> successes = new ArrayList<>();
			successes.add(new SuccessDiv("The skill with id " + newSkill.getId() + " is added to the database!"));
			render(request, response, null, successes);
		} else {
			List<ErrorDiv> errors = new ArrayList<>();
			errors.add(new ErrorDiv("The skill with id " + newSkill.getId() + " could not be added to the database!"));
			render(request, response, errors, null);
		}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void render(HttpServletRequest request, HttpServletResponse response, List<ErrorDiv> errors,
			List<SuccessDiv> successes) throws IOException {
		StringWriter w = new StringWriter();
		Mustache m = new DefaultMustacheFactory().compile("templates/addSkill.mustache");
		Map<String, Object> context = new HashMap<>();

		if (errors != null)
			context.put("error", errors);

		if (successes != null)
			context.put("success", successes);

		m.execute(w, context);

		super.doSend(request, response, w.toString());
	}
}
