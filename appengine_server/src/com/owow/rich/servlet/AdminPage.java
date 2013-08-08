package com.owow.rich.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.Text;
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
			try {
				doGetAdd(req, resp);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
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

	private void doGetAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException, EntityNotFoundException {
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
		m.storage.saveApiResponse(new WebPage(null, null, serv), ngram, new ApiResponse(ngram, json, view, at), true, true);

		resp.sendRedirect("./AdminPage?act=viewAll");
	}
	int	perPage	= 100;

	private void doGetViewAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String s_page = req.getParameter("page");
		int page = s_page == null ? 0 : Integer.parseInt(s_page) - 1;
		int offset = page * perPage;
		int length = perPage;
		Manager m = new Manager();
		List<Entity> apis = m.storage.queryTheDB(offset, length);

		String html = "";
		int index = 0;
		for (Entity ent : apis)
		{
			html += getHtmlViewOfPC("item-" + index, ent, ent.getKey().getName());
			index++;
		}

		resp.setContentType("text/html");

		resp.getWriter().write(TemplateUtil.getHtml("view.soy", new SoyMapData("p", html, "page", page + 1)));
	}

	private String getHtmlViewOfPC(String id, PropertyContainer ent, String name) {
		Set<Entry<String, Object>> map = ent.getProperties().entrySet();

		if (map.size() == 0) return "<li><input type=\"checkbox\" id=\"" + id + "\" disabled=\"disabled\" /><label for=\"" + id + "\">" + name
		      + "(empty)</label><ul></ul></li>";
		String starting = "<li><input type=\"checkbox\" id=\"" + id + "\" /><label for=\"" + id + "\">" + name + "</label><ul>";
		String otherContainers = "";
		String theRest = "";
		int index = 0;
		for (Entry<String, Object> entry : map) {
			if (entry.getValue() instanceof PropertyContainer)
			{
				otherContainers += getHtmlViewOfPC(id + "-" + index, (PropertyContainer) entry.getValue(), entry.getKey());
				index++;
			}
			else
			{
				Object oval = entry.getValue();
				String value = oval == null ? "null" : oval instanceof Text ? ((Text) oval).getValue() : oval.toString();
				theRest += "<li>" + encodeHTML("● " + entry.getKey() + " : " + value) + "</li>";
			}
		}
		if (ent instanceof Entity)
		{// AdminPage?act=view&q=Shalti
			theRest += "<li><a href='AdminPage?act=view&q=" + ((Entity) ent).getKey().getName() + "'>Edit</a></li>";
		}

		return starting + otherContainers + theRest + "</ul></li>";
	}

	public static String encodeHTML(String s)
	{
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>')
			{
				out.append("&#" + (int) c + ";");
			}
			else
			{
				out.append(c);
			}
		}
		return out.toString();
	}
}
