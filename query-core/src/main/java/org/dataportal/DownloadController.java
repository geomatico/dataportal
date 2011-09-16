/**
 * 
 */
package org.dataportal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dataportal.utils.Utils;

/**
 * Class to control the files download. Use a Cached Thread Pool to download
 * files concurrently
 * 
 * @author Micho Garcia
 * 
 */
public class DownloadController {

	private static Logger logger = Logger.getLogger(DownloadController.class);

	/**
	 * Create the pathfile with the information extracted from properties and
	 * create the directory if not exits
	 * 
	 * @param userName
	 *            User name from session (String)
	 */
	public String createPathFile(String userName) {

		String pathFile = "";
		Properties queryCoreProp = new Properties();
		try {
			queryCoreProp.load(QueryController.class
					.getResourceAsStream("/query-core.properties"));
			pathFile = queryCoreProp.getProperty("pathTmp") + "/" + userName;
			File personalDirectory = new File(pathFile);
			boolean exits = personalDirectory.exists();
			if (!exits) {
				boolean createDir = personalDirectory.mkdir();
				if (!createDir)
					pathFile = "";
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
			pathFile = "";
		}

		return pathFile;
	}

	/**
	 * Method to download files from a url's array.
	 * 
	 * @param urlsRequest
	 *            ArrayList with url's (ArrayList)
	 * @return Message
	 * @throws Exception
	 */
	public String downloadDatasets(ArrayList<String> urlsRequest,
			String userName) throws Exception {

		// TODO cambiar esto por un mensaje de dataportal
		String response = "";

		String pathFile = this.createPathFile(userName);
		if (pathFile.equals("")) {
			response = "Imposible realizar la descarga";
			logger.error("No se ha podido crear el directorio");
		} else {

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
				logger.info("DOWNLOADING FINISH: " + tarea);
			}

			String nameFile = userName + Utils.extractDateSystem();
			compressFiles(pathFile, nameFile);
			// TODO cambiar mensaje
			response = "FINALIZADO";
		}

		return response;
	}

	/**
	 * 
	 * Compress in tar format files in directory
	 * 
	 * @param pathDir
	 *            directory path (String)
	 * @param nameFile
	 *            archive name (String)
	 * @throws IOException
	 */
	private static void compressFiles(String pathDir, String nameFile)
			throws IOException {

		OutputStream os = new FileOutputStream(pathDir + "/" + nameFile
				+ ".tar");
		TarArchiveOutputStream tarOs = new TarArchiveOutputStream(os);

		File directory = new File(pathDir);
		String extensions[] = { "nc" };
		@SuppressWarnings("unchecked")
		Iterator<File> itFiles = FileUtils.iterateFiles(directory, extensions,
				false);

		while (itFiles.hasNext()) {
			File fl = itFiles.next();
			ArchiveEntry archFl = tarOs.createArchiveEntry(fl, fl.getName());
			tarOs.putArchiveEntry(archFl);
			tarOs.write(FileUtils.readFileToByteArray(fl));
			tarOs.flush();
			tarOs.closeArchiveEntry();
		}

		tarOs.finish();
		tarOs.close();

		os.close();
	}

}
