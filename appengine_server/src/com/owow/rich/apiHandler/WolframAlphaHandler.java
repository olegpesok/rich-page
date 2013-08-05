package com.owow.rich.apiHandler;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class WolframAlphaHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String title, ApiType at) throws Exception {
		final String server = "http://api.wolframalpha.com/v2/query?appid=HJ6J59-3QTLP92L6P&input=";

		final JSONObject ret = new JSONObject();

		final org.w3c.dom.Document d = HtmlUtil.getDocumentFromUrl(server + title);
		if (d.getDocumentElement().getAttribute("success").contains("false") || !d.getDocumentElement().getAttribute("error").equals("false")) {
			Logger.getLogger("Wolfram").warning("failed with " + title);
			return null;
		}

		// parsing document to string
		final DOMSource domSource = new DOMSource(d.getDocumentElement());
		final StringWriter dataWriter = new StringWriter();
		final StreamResult result = new StreamResult(dataWriter);
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);

		ret.put("data", dataWriter.toString());

		return new ApiResponse(title, ret, at);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
