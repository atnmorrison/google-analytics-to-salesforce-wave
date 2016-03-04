/**
 * This document is a part of the source code and related artifacts
 * for GA2SA, an open source code for Google Analytics to 
 * Salesforce Analytics integration.
 *
 * Copyright © 2015 Cervello Inc.,
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package controllers;

import java.util.Map;

import models.DashboardJob;
import models.JobStatus;
import models.dao.DashboardJobDAO;
import models.dao.SalesforceAnalyticsProfileDAO;
import models.dao.filters.BaseFilter;
import models.dao.filters.BaseFilter.OrderType;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.MimeTypes;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.actors.DashboardBGJob;
import com.ga2sa.scheduler.BackgroundJob;
import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;
import com.ga2sa.validators.Validator;

/**
 * @author SLegostaev
 *
 */
@Access
public class DashboardJobController extends Controller {
	
	private static final String NOT_FOUND = "Dashboard not found";
	
	public static Result create() {
		JsonNode requestData = request().body().asJson();
		DashboardJob job = Json.fromJson(requestData, DashboardJob.class);
		job.setSalesforceAnalyticsProfile(SalesforceAnalyticsProfileDAO.getProfileById(Long.valueOf(requestData.get("salesforceProfile").textValue())));
		job.setUser(ApplicationSecurity.getCurrentUser());
		job.setStatus(JobStatus.PENDING);
		job.setMessages("Job has pending status");
		
		Map<String, String> validateResult = Validator.validate(job);
		if (validateResult.isEmpty()) {
			try {
				DashboardJobDAO.save(job);
				startBGjob(job);
				return ok(Json.toJson(job)).as(MimeTypes.JAVASCRIPT());
			} catch (Exception e) {
				Logger.debug(e.getMessage());
				validateResult.put("error", e.getMessage());
			}
		}
		return badRequest(Json.toJson(validateResult)).as(MimeTypes.JAVASCRIPT());
	}

	private static void startBGjob(DashboardJob job) {
		ActorRef backgroundJob = Akka.system().actorOf(Props.create(BackgroundJob.class));
		Akka.system().scheduler().scheduleOnce(FiniteDuration.Zero(), backgroundJob, new DashboardBGJob(job), Akka.system().dispatcher(), ActorRef.noSender());
	}
	
	public static Result cancel(Long id) throws Exception {
		if (id != null)  {
			DashboardJob job = DashboardJobDAO.findById(id);
			if (job != null) {
				if (job.getStatus().equals(JobStatus.PENDING) == false) return badRequest("Job already completed.");
				job.setStatus(JobStatus.CANCELED);
				job.setMessages("Job has been canceled.");
				DashboardJobDAO.update(job);
				return ok(Json.toJson(job));
			}
		}
		return notFound(NOT_FOUND);
	}
	
	public static Result delete(Long id) {
		if (id != null)  {
			DashboardJob job = DashboardJobDAO.findById(id);
			if (job != null) {
				if (job.getStatus().equals(JobStatus.PENDING)) return badRequest("Job has pending status now and can not be deleted.");
				DashboardJobDAO.delete(job);
				return ok();
			}
		}
		return notFound(NOT_FOUND);
	}
	
	public static Result jobs(Integer count, Integer page, String orderBy, String orderType) {
		BaseFilter<DashboardJob> filter = new BaseFilter<DashboardJob>(count, page, orderBy, orderType == null ? null : OrderType.valueOf(orderType), DashboardJob.class);
		return ok(Json.toJson(DashboardJobDAO.getAllByFilter(filter))).as(MimeTypes.JAVASCRIPT());
	}
}
