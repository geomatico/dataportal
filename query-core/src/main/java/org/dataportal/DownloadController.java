/**
 * 
 */
package org.dataportal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Class to control the files download. Use a Cached Thread Pool to download
 * files concurrently
 * 
 * @author Micho Garcia
 * 
 */
public class DownloadController {

	private static Logger logger = Logger.getLogger(DownloadController.class);

	private static String pathFile;

	/**
	 * Fill the attribute pathfile with the information extracted from
	 * properties
	 */
	public static void createPathFile() {
		
		// TODO Control user name to create tmp dir

		Properties queryCoreProp = new Properties();
		try {
			queryCoreProp.load(QueryController.class
					.getResourceAsStream("/query-core.properties"));
			pathFile = queryCoreProp.getProperty("pathTmp");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Method to download files from a url's array.
	 * 
	 * @param ArrayList with url's
	 * @return Message
	 * @throws Exception
	 */
	public static String downloadDatasets(ArrayList<String> urlsRequest)
			throws Exception {

		createPathFile();

		int urls = urlsRequest.size();

		ExecutorService threadsPool = Executors.newCachedThreadPool();
		int future = 0;
		Future futures[] = new Future[urls];

		for (String url : urlsRequest) {
			String name = StringUtils.substringAfterLast(url, "/");

			DownloadCallable hiloDescarga = new DownloadCallable(url, name,
					pathFile);
			futures[future] = threadsPool.submit(hiloDescarga);
			future += 1;

			Thread.sleep(2000);
		}

		for (int i = 0; i < urls; i++) {
			String tarea = (String) futures[i].get();
			logger.info("FINALIZADA DESCARGA: " + tarea);
		}
		// TODO cambiar mensaje
		return "FINALIZADO";
	}

}
