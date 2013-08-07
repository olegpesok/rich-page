package com.owow.rich.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.template.soy.data.SoyMapData;
import com.owow.rich.Manager;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Storage;
import com.owow.rich.utils.TemplateUtil;

public class AdminPage extends HttpServlet {
	/**
	 *
	 */
	private static final long	serialVersionUID	= 1061726062728111706L;

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {
		// URL startingUrl = new URL(req.getParameter("url"));
		String act = req.getParameter("act");
		if (act.equals("viewAll")) {
	      doGetViewAll(req, resp);
      } else if (act.equals("Add")) {
	      doGetAdd(req, resp);
      } else if (act.equals("view")) {
	      doGetView(req, resp);
      }

		// resp.getWriter().write("OK - " + damn);
	}

	private void doGetView(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String ngram = req.getParameter("q");
		Manager m = new Manager();

		resp.setContentType("text/html");
		SoyMapData smd = new SoyMapData();

		PropertyContainer entity = m.storage.loadPropertyContainer(new WebPage("", "", ""), ngram);
		if (entity != null)
		{
			ApiResponse ar = ApiResponse.getApiResponseFromEntity(entity);
			smd.put("json", ar.json == null ? " " : ar.json.toString());
			smd.put("m", ar.myType.nickname);
			smd.put("ngram", ngram);
			smd.put("serv", entity.getProperty(Storage.HOST_KEY));
			smd.put("view", ar.view.getView());
		}
		resp.getWriter().write(TemplateUtil.getHtml("set.soy", smd));
	}

	private void doGetAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String ngram = req.getParameter("ngram");
		ApiType at = ApiType.create(req.getParameter("m"));
		String view = req.getParameter("view");
		String serv = req.getParameter("serv");
		JSONObject json = null;
		String preJson = req.getParameter("json");
		if (preJson != null) {
	      try {
	      	json = new JSONObject(req.getParameter("json"));
	      } catch (JSONException e) {}
      }
		Manager m = new Manager();
		m.storage.saveApiResponse(new WebPage(null, null, serv), ngram, new ApiResponse(ngram, json, view, at));
		resp.sendRedirect("./AdminPage?act=viewAll");
	}
	int	perPage	= 50;

	private void doGetViewAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String s_page = req.getParameter("page");
		int page = s_page == null ? 0 : Integer.parseInt(s_page) - 1;
		int offset = page * perPage;
		int length = perPage;
		Manager m = new Manager();
		List<Entity> apis = m.storage.queryTheDB(offset, length);

		String format = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
		String html = "";
		for (Entity ent : apis)
		{
			ApiResponse ar = ApiResponse.getApiResponseFromEntity(ent);
			if (ar != null) {
	         html += String.format(format,
	               ent.getKey().toString(),
	               ar.title,
	               ar.json,
	               ar.view == null ? "" : ar.view.getView(),
	               ar.myType == null ? "" : ar.myType.nickname);
         }
		}

		resp.setContentType("text/html");

		resp.getWriter().write(TemplateUtil.getHtml("view.soy", new SoyMapData("p", html)));

	}
}
