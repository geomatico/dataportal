package org.dataportal;

import org.dataportal.datasources.Mail;

public class SystemSingleton {

	private static String persistenceUnit = "dataportal";

	public static String getPersistenceUnit() {
		return persistenceUnit;
	}

	public static void setPersistenceUnit(String persistenceUnit) {
		SystemSingleton.persistenceUnit = persistenceUnit;
	}

	private static Mail mail = new Mail();

	public static void setMail(Mail mail) {
		SystemSingleton.mail = mail;
	}

	public static Mail getMail() {
		return mail;
	}

}
