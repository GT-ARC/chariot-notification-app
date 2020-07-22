package de.gtarc.server.util;

import java.util.ArrayList;

import javax.websocket.Session;

public class DeviceSessionMap {

	public class Tuple {
		private String userUuid;
		private String deviceUuid;
		private DeviceType type;
		private Session session;

		public String getUserUuid() {
			return userUuid;
		}

		public void setUserUuid(String userUuid) {
			this.userUuid = userUuid;
		}

		public String getDeviceUuid() {
			return deviceUuid;
		}

		public void setDeviceUuid(String deviceUuid) {
			this.deviceUuid = deviceUuid;
		}

		public DeviceType getType() {
			return type;
		}

		public void setType(DeviceType type) {
			this.type = type;
		}

		public Session getSession() {
			return session;
		}

		public void setSession(Session session) {
			this.session = session;
		}

		public Tuple(String userUuid, String deviceUuid, DeviceType type, Session session) {
			this.userUuid = userUuid;
			this.deviceUuid = deviceUuid;
			this.type = type;
			this.session = session;
		}
	}

	private ArrayList<Tuple> sessions;

	public DeviceSessionMap() {
		sessions = new ArrayList<>();
	}

	public void register(String userUuid, String deviceUuid, DeviceType type, Session session) {
		System.out.println("register " + userUuid + " " + deviceUuid + " " + type);
		sessions.add(new Tuple(userUuid, deviceUuid, type, session));
	}

	public void unregister(String userUuid) {
		ArrayList<Tuple> removed = new ArrayList<>();
		sessions.forEach(tuple -> {
			if (tuple.getUserUuid().equals(userUuid))
				removed.add(tuple);
		});
		removed.forEach(tuple -> sessions.remove(tuple));
	}

	public void unregister(String userUuid, DeviceType type) {
		ArrayList<Tuple> removed = new ArrayList<>();
		sessions.forEach(tuple -> {
			if (tuple.getUserUuid().equals(userUuid) && tuple.getType().equals(type))
				removed.add(tuple);
		});
		removed.forEach(tuple -> sessions.remove(tuple));
	}

	public void unregister(Session session) {
		ArrayList<Tuple> removed = new ArrayList<>();
		sessions.forEach(tuple -> {
			if (tuple.getSession().getId().equals(session.getId()))
				removed.add(tuple);
		});
		removed.forEach(tuple -> sessions.remove(tuple));
	}

	public Session getSession(String deviceUuid) {
		for (Tuple tuple : sessions) {
			if (tuple.getDeviceUuid().equals(deviceUuid))
				return tuple.getSession();
		}
		return null;
	}

	public Session getSession(String userUuid, DeviceType type, String deviceUuid) {
		for (Tuple tuple : sessions) {
			if (tuple.getUserUuid().equals(userUuid) && tuple.getType().equals(type)
					&& tuple.getDeviceUuid().equals(deviceUuid))
				return tuple.getSession();
		}
		return null;
	}

	public String getUserUuid(Session session) {
		for (Tuple tuple : sessions)
			if (tuple.getSession().getId().equals(session.getId()))
				return tuple.getUserUuid();
		return null;
	}

	public DeviceType getDeviceType(Session session) {
		for (Tuple tuple : sessions)
			if (tuple.getSession().getId().equals(session.getId())) {
				return tuple.getType();
			}
		return null;
	}

	public String getDeviceUuid(Session session) {
		for (Tuple tuple : sessions) {
			if (tuple.getSession().getId().equals(session.getId())) {
				return tuple.getDeviceUuid();
			}
		}
		return null;
	}

	public Iterable<Tuple> iterator() {
		return sessions;
	}
}