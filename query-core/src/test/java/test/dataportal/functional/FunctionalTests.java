package test.dataportal.functional;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FunctionalTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(LoginTest.class);
		suite.addTestSuite(LocaleTest.class);
		suite.addTestSuite(QueryTest.class);
		suite.addTestSuite(DownloadTest.class);

		return suite;
	}
}
