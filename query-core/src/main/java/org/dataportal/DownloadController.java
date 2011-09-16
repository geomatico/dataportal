/**
 * 
 */
package org.dataportal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
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
    private String tempDir;

    /**
     * Constructor. Reads tempDir from properties file. 
     */
    public DownloadController() {
        Properties queryCoreProp = new Properties();
        try {
            queryCoreProp.load(QueryController.class.getResourceAsStream("/query-core.properties"));
            this.tempDir = queryCoreProp.getProperty("pathTmp");
        } catch (IOException e) {
            logger.error(e.getMessage());
            this.tempDir = "";            
        }
    }
    
	/**
	 * Create the pathfile with the information extracted from properties and
	 * create the directory if not exits
	 * 
	 * @param userName
	 *            User name from session (String)
	 */
	public String createPathFile(String userName) {

		String pathFile = this.tempDir + "/" + userName;
		File personalDirectory = new File(pathFile);
		boolean exits = personalDirectory.exists();
		if (!exits) {
			boolean createDir = personalDirectory.mkdir();
			if (!createDir)
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

		// TODO cambiar esto por una clase mensaje de dataportal
		String response = "";

		String pathFile = createPathFile(userName);
		if (pathFile.equals("")) {
			response = "No se ha podido crear el directorio";
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

			String nameFile = userName + "_" + Utils.extractDateSystem();
			String filePathName = compressFiles(pathFile, nameFile);
			// TODO cambiar mensaje
			logger.debug("FILE to download: " + filePathName);
			response = StringUtils.substringAfterLast(filePathName, "/");
		}

		return response;
	}

    /**
     * Returns an inputStream to download the compressed file
     * 
     * @param fileName
     *            Name of compressed file
     * @param userName
     *            User name that generated the file
     * @return
     * @throws FileNotFoundException
     */
    public InputStream getFileContents(String fileName, String userName) throws FileNotFoundException {
        File file = new File(this.tempDir + "/" + userName + "/" + fileName);
        return new FileInputStream(file);
    }

    /**
     * Returns the downloadable file size
     * 
     * @param fileName
     *            Name of compressed file
     * @param userName
     *            User name that generated the file
     * @return
     * @throws FileNotFoundException
     */
    public long getFileSize(String fileName, String userName) throws FileNotFoundException {
        File file = new File(this.tempDir + "/" + userName + "/" + fileName);
        if (file.exists() && file.isFile())
            return file.length();
        else
            return 0;
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
    private String compressFiles(String pathDir, String nameFile)
			throws IOException {

		String filePathName = pathDir + "/" + nameFile
						+ ".zip";
		OutputStream os = new FileOutputStream(filePathName);
		ZipArchiveOutputStream zipOs = new ZipArchiveOutputStream(os);

		File directory = new File(pathDir);
		String extensions[] = { "nc" };
		@SuppressWarnings("unchecked")
		Iterator<File> itFiles = FileUtils.iterateFiles(directory, extensions,
				false);

		while (itFiles.hasNext()) {
			File fl = itFiles.next();
			ArchiveEntry archFl = zipOs.createArchiveEntry(fl, fl.getName());
			zipOs.putArchiveEntry(archFl);
			zipOs.write(FileUtils.readFileToByteArray(fl));
			zipOs.flush();
			zipOs.closeArchiveEntry();
		}

		zipOs.finish();
		zipOs.close();

		os.close();
		
		return filePathName;
	}

}
