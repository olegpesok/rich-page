package com.owow.rich.items;

public class Image {
	public String	src;
	public int	  height;
	public int	  width;
	public String	title;
	public Image(String title, String src, int height, int width) {
		this.title = title;
		this.src = src;
		this.height = height;
		this.width = width;
	}

	public String getView()
	{
		String template = "<div class=\"context-box-link context-box-full-bleed\" style=\" background-image: url('{0}');\">" +
		      "<div class=\"context-box-link-caption\">{1}" +
		      "</div>" +
		      "</div>";

		return String.format(template, src, title);
	}

	@Override
	public String toString() {
		return src;
	}
}
