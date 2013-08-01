
<%@page import="com.gargoylesoftware.htmlunit.html.DomElement"%>
<%@page import="com.gargoylesoftware.htmlunit.html.DomNodeList"%>
<%@page import="com.gargoylesoftware.htmlunit.html.HtmlPage"%>
<%@page import="com.gargoylesoftware.htmlunit.WebClient"%>
<%@page import="org.xml.sax.InputSource"%>
<%@page import="javax.swing.text.html.HTMLDocument"%>
<%@page import="javax.xml.transform.*"%>
<%@page import="javax.xml.transform.dom.*"%>
<%@page import="javax.xml.transform.stream.*"%>


<%@ page import="org.json.*"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="javax.xml.parsers.*"%>

<%@ page language="java"
	contentType="application/json; charset=US-ASCII"
	pageEncoding="US-ASCII" import="java.io.*,java.net.URL,java.net.*"%>

<%
	
%>

<%!public JSONObject handleError(String error, String title, String charset)
			throws Exception {
		if (error.equals("nowiki")) {
			JSONObject errorType = getFromGoogle(title, charset);
			errorType.put("error", error);
			return errorType;

		}

		else {
			JSONObject jo = new JSONObject();
			jo.put("data", "[]");
			jo.put("type", "google");
			jo.put("error", error);
			return jo;
		}
	}%>

<%!public JSONObject getFromWiki(String title, String charset)
			throws Exception {
		JSONArray ret = new JSONArray();
		String wiki = "http://en.wikipedia.org/w/api.php?format=json&action=query&prop=revisions&rvprop=content&rvsection=0&rvparse=true&titles=";
		String data = getUrlSource(wiki + URLEncoder.encode(title, charset));

		JSONObject pages = new JSONObject(data).getJSONObject("query")
				.getJSONObject("pages");
		String[] pagesID = JSONObject.getNames(pages);
		if (pagesID[0].equals("-1")) {
			return handleError("nowiki", title, charset);
		}
		for (String s : pagesID) {
			JSONObject jo = new JSONObject();
			JSONObject currentPage = pages.getJSONObject(s);
			String[] whatIWant = new String[] { "title", "revisions" };
			for (String prop : whatIWant) {
				jo.put(prop, currentPage.get(prop));
			}
			ret.put(jo);
		}
		JSONObject jo = new JSONObject();
		jo.put("data", ret);
		jo.put("type", "wiki");
		return jo;

	}%>

<%!public JSONObject getFromQuora(String title, String charset)
			throws Exception {
		JSONArray ret = new JSONArray();
		String server = "http://www.quora.com/search?q=";
		String url = server + URLEncoder.encode(title, charset);
		try {
			DomNodeList<DomElement> divs = getDocumentFromUrl(url);
			for (int i = 0; i < divs.getLength(); i++) {
				DomElement div = divs.get(i);

				if (div.getAttributes()
						.getNamedItem("class")
						.getTextContent()
						.contains(
								"row feed_item query_result question_query_result")) {
					ret.put(div.getTextContent());
				}

			}
		} catch (Exception e) {
			ret.put(e.getCause());
			ret.put(e.getMessage());

		}
		JSONObject jo = new JSONObject();
		jo.put("data", ret);
		jo.put("type", "quora");
		return jo;

	}%>

<%!public JSONObject getFromGoogle(String search, String charset)
			throws Exception {
		JSONArray ret = new JSONArray();
		String google = "https://www.googleapis.com/customsearch/v1?key=AIzaSyCR2RdAX0PFHXargEsInerEc-RiFkYTWPE&cx=005640612292887937759:1reryqwjvhi&q=";

		String data = getUrlSource(google + URLEncoder.encode(search, charset));

		JSONObject j = new JSONObject(data);

		JSONObject jo = new JSONObject();
		try {
			JSONArray ja = j.getJSONArray("items");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jTemp = ja.getJSONObject(i);
				ret.put(jTemp.getString("htmlSnippet"));
			}
		} catch (JSONException jsone) {
			return handleError("no results", search, charset);
		}
		jo.put("type", "google");
		jo.put("data", ret);
		return jo;
	}%>

<%!public static String toString(Document doc) {
		try {
			StringWriter sw = new StringWriter();

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}%>
<%!public static DomNodeList<DomElement> getDocumentFromUrl(String url)
			throws Exception {
		try {
			final WebClient webClient = new WebClient();

			// Get the first page
			final HtmlPage page1 = webClient.getPage(url);

			return page1.getElementsByTagName("div");
		} catch (Exception e) {
			throw e;
		}
	}%>

<%!private static String getUrlSource(String url) throws IOException {
		URL yahoo = new URL(url);
		URLConnection yc = yahoo.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuilder a = new StringBuilder();
		while ((inputLine = in.readLine()) != null)
			a.append(inputLine);
		in.close();

		return a.toString();
	}%>
