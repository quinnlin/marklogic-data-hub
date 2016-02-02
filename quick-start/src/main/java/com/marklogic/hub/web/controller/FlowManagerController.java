package com.marklogic.hub.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marklogic.hub.flow.Flow;
import com.marklogic.hub.service.FlowManagerService;

@Controller
@RequestMapping("/flows")
public class FlowManagerController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FlowManagerController.class);
	
	@Autowired
	private FlowManagerService flowManagerService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Flow> getFlows(Model model) {
		return flowManagerService.getFlows();
	}
	
	@RequestMapping(value = "/{flowName}", method = RequestMethod.GET)
	@ResponseBody
	public Flow getFlow(@PathVariable String flowName) {
		return flowManagerService.getFlow(flowName);
	}
	
	@RequestMapping(value = "/{flowName}/install", method = RequestMethod.POST)
	public void installFlow(@PathVariable String flowName) {
		final Flow flow = flowManagerService.getFlow(flowName);
		flowManagerService.installFlow(flow);
	}
	
	@RequestMapping(value = "/{flowName}/uninstall", method = RequestMethod.POST)
	public void uninstallFlow(@PathVariable String flowName) {
		flowManagerService.uninstallFlow(flowName);
	}
	
	@RequestMapping(value = "/{flowName}/run", method = RequestMethod.POST)
	public void runFlow(@PathVariable String flowName) {
		final Flow flow = flowManagerService.getFlow(flowName);
		//TODO update and move BATCH SIZE TO a constant or config - confirm desired behavior
		flowManagerService.runFlow(flow, 100);
	}
	
	@RequestMapping(value = "/runInParallel", method = RequestMethod.POST)
	public void runFlowsInParallel(HttpServletRequest request) {
		String[] flowNames = request.getParameterValues("flowName");
		List<Flow> flows = new ArrayList<Flow>();
		for (String flowName: flowNames) {
			final Flow flow = flowManagerService.getFlow(flowName);
			flows.add(flow);
		}
		flowManagerService.runFlowsInParallel(flows.toArray(new Flow[flows.size()]));
	}

}
