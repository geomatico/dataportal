/**
 * 
 */
package org.dataportal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import co.geomati.netcdf.Converter;
import co.geomati.netcdf.IConverter;
import co.geomati.netcdf.NCGlobalAttributes;

/**
 * @author Micho Garcia
 * 
 */
public class ConverterServlet extends HttpServlet implements DataportalCodes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String TEMP_DIR = "temp.dir";
	private static final String ID = "id";
	private static final String NC_EXTENSION = ".nc";
	private static final Object CONVERTER = "converter";
	private static final String UPLOAD_DIRECTORY = "upload";

	// upload settings
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	private static final String SUCCESS = "success";
	private static final String MESSAGE = "msg";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/xml");

		InputStream StreamConvertersXML = Converter.class.getClassLoader()
				.getResourceAsStream("co/geomati/netcdf/converters.xml");
		String convertersXML = IOUtils.toString(StreamConvertersXML);
		PrintWriter out = resp.getWriter();
		out.print(convertersXML);
		StreamConvertersXML.close();
		out.close();

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		JSONObject jsonResponse = new JSONObject();
		StringWriter stringJSON = new StringWriter();
		String id = null;

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		if (!ServletFileUpload.isMultipartContent(request)) {
			jsonResponse.put(SUCCESS, false);
			jsonResponse.put(MESSAGE,
					"Error: Form must has enctype=multipart/form-data."); // TODO
																			// Localize
			writer.print(stringJSON.toString());
			writer.flush();
			writer.close();
			return;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		factory.setRepository(new File(Config.get(TEMP_DIR)));

		ServletFileUpload upload = new ServletFileUpload(factory);

		upload.setFileSizeMax(MAX_FILE_SIZE);
		upload.setSizeMax(MAX_REQUEST_SIZE);

		String uploadPath = Config.get(TEMP_DIR)
				+ File.separator + UPLOAD_DIRECTORY;

		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			List<FileItem> formItems = upload.parseRequest(request);
			NCGlobalAttributes globalAttributes = new NCGlobalAttributes();
			IConverter converter = null;
			ArrayList<String> files = new ArrayList<String>();
			String fileName, filePath, institution = null;

			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					String value = item.getString();
					String name = item.getFieldName();
					if (!item.isFormField()) {
						fileName = new File(item.getName()).getName();
						filePath = uploadPath + File.separator
								+ fileName;
						File storeFile = new File(filePath);
						files.add(fileName.substring(0, fileName.lastIndexOf('.')));

						item.write(storeFile);
					} else if (name.equals(CONVERTER)) {
						institution = value.toLowerCase();
						converter  = Converter.getConverterToUse(value);
					} else if (name.equals(ID)){
						id = UUID.randomUUID().toString();
						globalAttributes.addAtribute(name, id);
					} else {
						globalAttributes.addAtribute(name, value);
					}
				}

				Converter.setTempSavePath(Config.get(TEMP_DIR));
				Converter.setGlobalAttributes(globalAttributes);
				converter.doConversion(files.toArray(new String[files.size()]), uploadPath);
				
				copyToThreddsCatalogFolder(files.toArray(new String[files.size()]), institution);
				FileUtils.forceDelete(uploadDir);
			}
			jsonResponse.put(SUCCESS, true);
			jsonResponse.put(MESSAGE, id);
		} catch (Exception ex) {
			jsonResponse.put(SUCCESS, false);
			jsonResponse.put(MESSAGE, ex.getMessage());
		}
		jsonResponse.write(stringJSON);
		writer.print(stringJSON.toString());
		writer.flush();
		writer.close();
	}

	private void copyToThreddsCatalogFolder(String[] files, String institution) throws IOException {
		for (String file : files) {
			File tmpFile = new File(Config.get(TEMP_DIR) + File.separator + file + NC_EXTENSION);
			FileUtils.copyFileToDirectory(tmpFile, new File(Config.get("thredds.dir") + File.separator + institution));
			tmpFile.delete();
		}
	}
}
