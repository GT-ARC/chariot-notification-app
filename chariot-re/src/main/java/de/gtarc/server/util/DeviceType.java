package de.gtarc.server.util;

import java.io.Serializable;

public enum DeviceType implements Serializable {
	SMARTWATCH("Smartwatch"), OTHER("Undefined");

	public static DeviceType value(String name) {
		if (SMARTWATCH.name.equalsIgnoreCase(name)) {
			return SMARTWATCH;
		} else {
			return OTHER;
		}
	}

	private String name;

	private DeviceType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
