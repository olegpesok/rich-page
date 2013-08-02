package com.owow.rich.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.owow.rich.Manager;

@SuppressWarnings("serial")
public class PageProccessServletQueuer extends HttpServlet {

	private Manager	          manager	= new Manager();
	private static final Logger	log	= Logger.getLogger("Rich");

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {

		String url = req.getParameter("url");
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/PageProc").param("url", url));
		return;

	}
}
