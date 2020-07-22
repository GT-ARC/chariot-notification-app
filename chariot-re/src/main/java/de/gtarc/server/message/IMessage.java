package de.gtarc.server.message;

import de.dailab.jiactng.agentcore.knowledge.IFact;

public interface IMessage extends IFact {
	static final String TAG_HEADER = "header";

	String getHeader();
}
