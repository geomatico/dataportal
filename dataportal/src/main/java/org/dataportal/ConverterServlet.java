/**
 * 
 */
package org.dataportal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import co.geomati.netcdf.Converter;

/**
 * @author Micho Garcia
 * 
 */
public class ConverterServlet extends HttpServlet implements DataportalCodes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

		String converters = req.getParameter("converters");

		if (converters != null) {
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
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject jsonResponse = new JSONObject();
		StringWriter stringJSON = new StringWriter();

		response.setCharacterEncoding("UTF-8"); 
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		if (!ServletFileUpload.isMultipartContent(request)) {
			jsonResponse.put(SUCCESS, false);
			jsonResponse.put(MESSAGE, "Error: Form must has enctype=multipart/form-data."); //TODO Localize
			writer.print(stringJSON.toString());
			writer.flush();
			writer.close();
			return;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);

		upload.setFileSizeMax(MAX_FILE_SIZE);
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// How can I saves file into /tmp system folder?
		String uploadPath = System.getProperty("java.io.tmpdir")
				+ File.separator + UPLOAD_DIRECTORY;

		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		
		try {
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator
								+ fileName;
						File storeFile = new File(filePath);

						item.write(storeFile);
					}
				}
			}
			jsonResponse.put(SUCCESS, true);
			jsonResponse.put(MESSAGE, "All OK!");			
		} catch (Exception ex) {
			jsonResponse.put(SUCCESS, false);
			jsonResponse.put(MESSAGE, ex.getMessage());
		}		
		jsonResponse.write(stringJSON);
		writer.print(stringJSON.toString());
		writer.flush();
		writer.close();
	}
}
