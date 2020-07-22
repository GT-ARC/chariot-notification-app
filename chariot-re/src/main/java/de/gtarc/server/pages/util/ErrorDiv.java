package de.gtarc.server.pages.util;

public class ErrorDiv {
	private String text;

	public ErrorDiv(String text) {
		setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
