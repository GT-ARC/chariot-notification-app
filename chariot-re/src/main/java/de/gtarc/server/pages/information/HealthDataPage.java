package de.gtarc.server.pages.information;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.gtarc.server.entity.HealthData;
import de.gtarc.server.entity.Worker;
import de.gtarc.server.pages.AbstractPage;
import de.gtarc.server.service.KMSService;

@WebServlet(value = "/health_data")
public class HealthDataPage extends AbstractPage {

	private static final long serialVersionUID = 1913572000051997223L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.print(this.getClass().getName()+"is called");
		KMSService db = null;
		try {
			db = KMSService.getInstance();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		String getID = request.getParameter("workerID");
		if (getID == null || getID.equals("")) {
			response.sendRedirect("/error");
			return;
		}

		Worker worker = db.getWorkerWithUsername(getID);
		if (worker == null) {
			response.sendRedirect("/error");
			return;
		}

		String dateTime = new SimpleDateFormat("yyyy_MM_dd:HH_mm_ss").format(new Date());

		String reportName = "Health_" + worker.getWorkerID() + "_" + worker.getSurname() + "_" + dateTime + ".csv";

		response.setContentType("text/csv");
		response.setHeader("Content-disposition", "attachment; " + "filename=" + reportName);

		ArrayList<String> rows = new ArrayList<>();
		rows.add("Measurement Time, Heart Rate (bpm), Step Status, Feel Status, Work Status");
		rows.add("\n");

		HealthData[] healthDatas = db.getHealthData(worker);

		for (HealthData data : healthDatas) {
			rows.add(data.getTime() + ", " + data.getHeartRate() + ", " + data.getStepStatus() + ", "
					+ data.getFeelStatus() + ", " + data.getWorkStatus());
			rows.add("\n");
		}

		Iterator<String> iter = rows.iterator();
		while (iter.hasNext()) {
			String outputString = iter.next();
			response.getOutputStream().print(outputString);
		}

		response.getOutputStream().flush();
	}
}
