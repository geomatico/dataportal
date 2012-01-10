package test.dataportal.functional;

public class QueryTest extends AbstractFunctionalTest {

	@Override
	protected String getService() {
		return "search";
	}

	public void testQuery() throws Exception {
		String ret = callService(new String[] { "lang", "bboxes", "start_date",
				"end_date", "variables", "text", "start", "limit", "sort",
				"dir" }, new String[] { "es", "", "", "", "", "", "1", "2",
				"title", "ASC" });
		System.err.println(ret);
	}
}
