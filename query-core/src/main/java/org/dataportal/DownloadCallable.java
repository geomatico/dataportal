/**
 * 
 */
package org.dataportal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * 
 * Create task to download files. Used to download files using a Cached Thread
 * Pool from java.utils.concurrent
 * 
 * @author Micho Garcia
 * 
 */
public class DownloadCallable implements Callable<String> {

	private static Logger logger = Logger.getLogger(DownloadCallable.class);

	private URL url;
	private String pathFile;
	private String name;

	
	/**
	 * Class constructor. Receive parameters to assign
	 * 
	 * @param String with url to download file
	 * @param String the file name to be assigned
	 * @param String with the path file to be downloaded
	 * @throws MalformedURLException
	 */
	public DownloadCallable(String url, String name, String pathFile)
			throws MalformedURLException {
		this.url = new URL(url);
		this.pathFile = pathFile;
		this.name = name + "_" + System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public String call() throws FileNotFoundException, IOException{
		File downFile = new File(pathFile + "/" + name);
		logger.debug("FILEPATH: " + pathFile + "/" + name);

		try {
			OutputStream ouFileDown = new FileOutputStream(downFile);
			InputStream isURL = url.openStream();

			IOUtils.copy(isURL, ouFileDown);

			ouFileDown.close();
			isURL.close();

		} catch (FileNotFoundException fnE) {
			logger.error("File not found: " + fnE.getMessage());
			throw fnE;
		} catch (IOException ioE) {
			logger.error("IOException: " + ioE.getMessage());
			throw ioE;
		} 
		logger.info("Finaliza descarga: " + name);
		return downFile.getAbsolutePath();
	}

}
