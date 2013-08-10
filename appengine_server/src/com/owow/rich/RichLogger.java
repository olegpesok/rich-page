package com.owow.rich;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RichLogger {
	public static final Logger log = Logger.getLogger("Rich");

	public static void logException(String msg, Exception ex) {
	   log.log(Level.SEVERE, msg, ex);
	   
   }
}
