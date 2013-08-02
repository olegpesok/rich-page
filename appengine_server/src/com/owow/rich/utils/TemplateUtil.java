package com.owow.rich.utils;

import java.io.File;

import com.google.appengine.labs.repackaged.com.google.common.annotations.VisibleForTesting;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

public class TemplateUtil {

	@VisibleForTesting
	public static String BASEPATH = "templates/";

	public static String getHtml(String templateFile, SoyMapData data) {
		String fullFilePath = BASEPATH + templateFile;
		SoyFileSet sfs = new SoyFileSet.Builder().add(new File(fullFilePath)).build();
		SoyTofu tofu = sfs.compileToTofu();
		return tofu.newRenderer("examples.simple.helloWorld")
		      .setData(data)
		      .render();
	}
}