package de.gtarc.server.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

public class Serializer {

	public Serializer() {

	}

	public byte[] serialize(Serializable obj) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(obj);
			byte[] answer = bout.toByteArray();

			out.close();
			bout.close();

			return answer;
		} catch (IOException e) {
			return null;
		}
	}

	public Object deserialize(byte[] obj) {
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(obj);
			ObjectInputStream in = new ObjectInputStream(bin);
			Object answer = in.readObject();

			in.close();
			bin.close();

			return answer;
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}

	public Object clone(Serializable obj) {
		byte[] bytes = serialize(obj);
		if (bytes != null)
			return deserialize(bytes);
		else
			return null;
	}
}
