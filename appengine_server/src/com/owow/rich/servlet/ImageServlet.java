package com.owow.rich.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import com.owow.rich.image.ImageRetriver;

public class ImageServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 625551238608853721L;

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {

		final String method = req.getParameter("m");
		String query = req.getParameter("q");

		// delete this {
		try {
			ImageRetriver.getImages(query);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		resp.setContentType("application/json");
		// resp.getWriter().write(s.toString());
	}
}
