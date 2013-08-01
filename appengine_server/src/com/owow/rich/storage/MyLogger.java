package com.owow.rich.storage;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyLogger {
	private static final Logger	log	= Logger.getLogger("Shalti");

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	      throws IOException {

		log.info("An informational message.");

		log.warning("A warning message.");

		log.severe("An error message.");
	}

}
