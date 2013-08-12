package com.owow.rich.items;

public class Image {
	public String	src;
	public int	  height;
	public int	  width;
	public String	title;
	public String	subtitle;
	public Image(String title, String subtitle, String src, int height, int width) {
		this.title = title;
		this.subtitle = subtitle;
		this.src = src;
		this.height = height;
		this.width = width;
	}

	// context-box-link-caption
	public String getView(String containerClasses, String titleClasses, String subtitleClasses)
	{
		titleClasses = titleClasses == null ? "link-caption" : titleClasses;
		containerClasses = containerClasses == null ? "link full-bleed" : containerClasses;
		subtitleClasses = subtitleClasses == null ? "info-position caption" : subtitleClasses;
		String template = "<div class=\"" + containerClasses + "\" style=\" background-image: url('{0}');\">" +
		      "<div class=\"" + titleClasses + "\">{1}</div>" +
		      (subtitle == null ? "" : "<div class=\"" + subtitleClasses + "\">{2}</div>") +
		      "</div>";
		return String.format(template, src, title, subtitle);
	}
	@Override
	public String toString() {
		return src;
	}
}
