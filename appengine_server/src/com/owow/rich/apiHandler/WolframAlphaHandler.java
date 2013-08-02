package com.owow.rich.apiHandler;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class WolframAlphaHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {
		final String server = "http://api.wolframalpha.com/v2/query?appid=HJ6J59-3QTLP92L6P&input=";

		final JSONObject ret = new JSONObject();

		final org.w3c.dom.Document d = HtmlUtil.getDocumentFromUrl(server + title);
		if (d.getDocumentElement().getAttribute("success").contains("false") || !d.getDocumentElement().getAttribute("error").equals("false")) throw new Exception(
		      "failed");
		final DOMSource domSource = new DOMSource(d.getDocumentElement());
		final StringWriter writer = new StringWriter();
		final StreamResult result = new StreamResult(writer);
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		ret.put("data", writer.toString());

	   return new ApiResponse(ret, at);
	}

	@Override
   public ApiView getView(ApiResponse fromGetData) throws Exception {
	   // TODO Auto-generated method stub
	   return null;
   }

}
