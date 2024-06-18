package org.intellij.sdk.toolWindow.backend.model;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.impl.ClassLoaderUtil;

public class Database {

	private static volatile Database database;
	private Master master;

	private Database() {
		getObjectMapper();
	}

	private void getObjectMapper() {

		var objectMapper = new ObjectMapper();
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Database/Master.json");
			System.out.println("Input stream: " + inputStream);
			master = objectMapper.readValue(inputStream, Master.class);

		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Database getInstance() {
		var result = database;
		if (result != null) {
			return result;
		}
		synchronized (Database.class) {
			if (database == null) {
				database = new Database();
			}
			return database;
		}
	}

	public Master getMaster() {
		return master;
	}

	public void setMaster(Master master) {
		this.master = master;
	}
}