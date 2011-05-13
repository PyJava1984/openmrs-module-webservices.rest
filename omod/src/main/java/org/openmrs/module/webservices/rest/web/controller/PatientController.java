package org.openmrs.module.webservices.rest.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.PatientResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for REST web service access to the Patient resource. Supports CRUD on the resource
 * itself, and listing and addition of some subresources.
 */
@Controller
@RequestMapping(value = "/rest/patient")
public class PatientController {
	
	private PatientResource getPatientResource() {
		return Context.getService(RestService.class).getResource(PatientResource.class);
	}
	
	/**
	 * @param patient
	 * @param request
	 * @return
	 * @throws Exception
	 * @should get a default representation of a patient
	 * @should get a full representation of a patient
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object getPatient(@PathVariable("uuid") String uuid, WebRequest request) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource resource = getPatientResource();
		return resource.retrieve(uuid, context);
	}
	
	/**
	 * @param post
	 * @param request
	 * @return
	 * @throws Exception
	 * @should create a new patient
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createPatient(@RequestBody SimpleObject post, WebRequest request) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		return getPatientResource().create(post, context);
	}
	
	/**
	 * @param patient
	 * @param post
	 * @param request
	 * @return
	 * @throws Exception
	 * @should change a property on a patient
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object updatePatient(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post,
	                            WebRequest request) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource resource = getPatientResource();
		resource.update(uuid, post, context);
		return resource.retrieve(uuid, context); // <-- TODO think about this line
	}
	
	/**
	 * @param patient
	 * @param reason
	 * @param request
	 * @throws Exception
	 * @should void a patient
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@ResponseBody
	public Object voidPatient(@PathVariable("uuid") String uuid,
	                        @RequestParam(value = "reason", defaultValue = "web service call") String reason,
	                        WebRequest request,
	                        HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		getPatientResource().delete(uuid, reason, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param patient
	 * @param request
	 * @throws Exception
	 * @should fail to purge a patient with dependent data
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "purge=true")
	@ResponseBody
	public Object purgePatient(@PathVariable("uuid") String uuid, WebRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
        getPatientResource().purge(uuid, context);
        return RestUtil.noContent(response);
	}
	
	/**
	 * @should return a list of names
	 */
	@RequestMapping(value = "/{uuid}/names", method = RequestMethod.GET)
	@ResponseBody
	public Object getNames(@PathVariable("uuid") String uuid, WebRequest request) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		return getPatientResource().listSubResource(uuid, "names", context.getRepresentation());
	}
	
}
