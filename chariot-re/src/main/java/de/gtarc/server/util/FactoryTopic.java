package de.gtarc.server.util;

public enum FactoryTopic {

	HUMAN_SENDING("human-sending"), HUMAN_RECEIVE("human"), SERVER_TASK_RECEIVE(
			"server-task-receive"), TASK_DISPATCHER_NOTIFY("task-dispatcher-notify");

	public static FactoryTopic typeOf(String topic) {
		if (topic == null)
			return null;
		if (topic.contains("human-sending")) {
			return HUMAN_SENDING;
		} else if (topic.contains("human")) {
			return HUMAN_RECEIVE;
		} else if (topic.contains("server-task-receive")) {
			return SERVER_TASK_RECEIVE;
		} else {
			return null;
		}
	}

	private String topic;

	private FactoryTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public String getTopic(String prefix) {
		return prefix + "-" + topic;
	}

	public String getPrefix(String topic) {
		if (topic == null || !topic.contains(topic)) {
			return null;
		}

		return topic.substring(0, topic.lastIndexOf(topic));
	}
}
