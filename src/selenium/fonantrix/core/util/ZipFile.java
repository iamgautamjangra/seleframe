package selenium.fonantrix.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to create zip file.
 *          </p>
 */
public class ZipFile {
	private static final Logger logger = LoggerFactory.getLogger(ZipFile.class
			.getName());

	/**
	 * Method to create zip file.
	 * 
	 * @param sourceFiles
	 *            Files to be included in zip file.
	 * @param destinationZipFile
	 *            Location of zip file including zip file name where it is to be
	 *            stored.
	 * @param outputDirectory
	 *            Location of the file to be zipped.
	 * @return Returns true.
	 */
	public static boolean makeZipFile(final String[] sourceFiles,
			final String destinationZipFile, String outputDirectory) {

		byte[] buffer = new byte[1024];

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(
					destinationZipFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(
					fileOutputStream);
			for (int i = 0; i < sourceFiles.length; i++) {
				if (sourceFiles[i] != null) {
					zipOutputStream.putNextEntry(new ZipEntry(new File(
							outputDirectory + sourceFiles[i]).getName()));
					File file = new File(outputDirectory + sourceFiles[i]);
					FileInputStream in = new FileInputStream(file);
					int len;
					while ((len = in.read(buffer)) > 0) {
						zipOutputStream.write(buffer, 0, len);
					}
					in.close();
					zipOutputStream.closeEntry();

				}
			}
			zipOutputStream.close();
		} catch (IOException e) {
			logger.error("Exception while creating the zip file and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}

		return true;

	}
}
