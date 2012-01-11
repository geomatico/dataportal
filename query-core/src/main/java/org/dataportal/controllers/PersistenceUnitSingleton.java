package org.dataportal.controllers;

public class PersistenceUnitSingleton {

	private static String persistenceUnit = "dataportal";

	public static String getPersistenceUnit() {
		return persistenceUnit;
	}

	public static void setPersistenceUnit(String persistenceUnit) {
		PersistenceUnitSingleton.persistenceUnit = persistenceUnit;
	}

}
