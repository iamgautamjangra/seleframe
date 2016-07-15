package selenium.fonantrix.core.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selenium.fonantrix.core.util.ConfigurationMap;
import selenium.fonantrix.core.util.RequestUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * @author Fonantrix <qa@fonantrix.com>
 * @version 1.0
 *          <p>
 *          This class contains functions to perform File System Operations.
 *          </p>
 */
public class FileIO {

	private static final Logger logger = LoggerFactory.getLogger(FileIO.class
			.getName());
	public static ChannelSftp sftpChannel;
	public static Session session = null;

	/**
	 * Method to create connection with server.
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static void createConnection() throws JSchException, SftpException,
			IOException {

		JSch jsch = new JSch();
		// Unix box user name
		String user = ConfigurationMap.getProperty("serverUserName");
		// Unix box host name
		String host = ConfigurationMap.getProperty("serverHostNameorIP");
		// Unix box port
		Integer port = Integer.parseInt(ConfigurationMap
				.getProperty("serverPort"));
		// Unix box password
		String password = ConfigurationMap.getProperty("serverPassword");

		sftpChannel = null;

		try

		{

			Channel channel = null;

			// Authentication with PPK File
			// String privateKey = "C:\\secure";
			// jsch.addIdentity(privateKey, password);

			session = jsch.getSession(user, host, port);

			session.setConfig("StrictHostKeyChecking", "no");

			// Authentication without PPK File
			session.setPassword(password);
			session.setConfig("PreferredAuthentications",
					"publickey,keyboard-interactive,password");

			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;

		}

		catch (Exception e) {
			logger.error("Exception while creating connection and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}
	}

	/**
	 * Method to create directory on the server.
	 * 
	 * @param filePath
	 *            Server location where directory is to be created.
	 * @param fileName
	 *            Directory name.
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static void createDir(String filePath, String fileName)
			throws JSchException, SftpException, IOException {
		createConnection();

		String dirName = filePath + fileName;
		try {

			sftpChannel.mkdir(dirName);

		} catch (SftpException e) {
			logger.error("Exception while creating directory and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}

		finally {
			if (sftpChannel != null) {
				sftpChannel.exit();
			}
			session.disconnect();
		}
	}

	/**
	 * Method to fire command on Linux server.
	 * 
	 * @param command
	 *            Linux command.
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static void executeCommand(String command) throws JSchException,
			SftpException, IOException {
		createConnection();
		try {
			ChannelExec cmdchannel = (ChannelExec) session.openChannel("exec");
			cmdchannel.setCommand(command);
			cmdchannel.connect();
			cmdchannel.disconnect();

		} catch (Exception e) {
			logger.error("Exception while executing a command and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}

		finally {
			if (sftpChannel != null) {
				sftpChannel.exit();
			}
			session.disconnect();
		}
	}

	/**
	 * Method to delete directory on server.
	 * 
	 * @param filePath
	 *            Server location where directory is to be deleted from.
	 * @param fileName
	 *            Directory name.
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static void deleteDir(String filePath, String fileName)
			throws JSchException, SftpException, IOException {

		createConnection();
		String DirName = filePath + fileName;
		try {
			sftpChannel.rmdir(DirName);

		} catch (SftpException e) {
			logger.error("Exception while deleting directory and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}

		finally {
			if (sftpChannel != null) {
				sftpChannel.exit();
			}
			session.disconnect();
		}
	}

	/**
	 * Method to verify existence of file in server.
	 * 
	 * @param filePath
	 *            Server location where file existence is to be checked.
	 * @param fileName
	 *            File name to be checked.
	 * @param localPath
	 *            Temporary local path for copying file from server to local.
	 * @return True or false based on file existence.
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static boolean fileExistanceStatus(String filePath, String fileName,
			String localPath) throws JSchException, SftpException, IOException {

		createConnection();
		String DirName = filePath + fileName;
		try {
			// Copying file from server to local.
			sftpChannel.get(DirName, localPath);

			File dir = new File(localPath);
			File[] dir_contents = dir.listFiles();

			for (int i = 0; i < dir_contents.length; i++) {
				// checking file existence.
				if (dir_contents[i].getName().equals(fileName)) {

					File file = new File(localPath + "\\" + fileName);
					file.delete();
					return true;
				}
			}
			return false;
		} catch (SftpException e) {
			logger.error("Exception while searching a file on server and exception text is: "
					+ RequestUtil.stackTraceToString(e));
			return false;
		}

		finally

		{
			if (sftpChannel != null) {
				sftpChannel.exit();
			}
			session.disconnect();
		}
	}

	/**
	 * Method to copy file from local to server.
	 * <p>
	 * Method is automatically called inside CopyfileToRemote method.
	 * </p>
	 * 
	 * @param file
	 *            Local file path.
	 * @param outputDir
	 *            Server path where file is to be copied.
	 * @throws SftpException
	 * @throws IOException
	 */
	public static void putFile(File file, String outputDir)
			throws SftpException, IOException

	{
		FileInputStream fis = null;
		try

		{
			sftpChannel.cd(outputDir);
			fis = new FileInputStream(file);
			sftpChannel.put(fis, file.getName());
			fis.close();
		}

		finally {
			if (sftpChannel != null) {
				sftpChannel.exit();
			}
			session.disconnect();
		}

	}

	/**
	 * Method to copy file from local to server.
	 * 
	 * @param filePath
	 *            Local file path.
	 * @param outputDir
	 *            Server path where file is to be copied.
	 * @param fileName
	 *            File name.
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static void copyFileToRemote(String filePath, String outputDir,
			final String fileName) throws JSchException, SftpException,
			IOException

	{
		createConnection();
		File dir = new File(filePath);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(fileName);
			}
		});
		for (File file : files) {
			putFile(file, outputDir);
		}
	}

	/**
	 * Method to read content inside file which is on server.
	 * 
	 * @param filePath
	 *            File directory path on server.
	 * @param fileName
	 *            File name.
	 * @param localPath
	 *            Local temporary directory path for copying file.
	 * @return Content Inside the File.
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static String readExistingServerFile(String filePath,
			String fileName, String localPath) throws JSchException,
			SftpException, IOException {
		createConnection();
		String DirName = filePath + fileName;
		// Copying file from server to local.
		sftpChannel.get(DirName, localPath);
		// Reading file content line by line
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(
					localPath + "\\" + fileName)));
			StringBuilder string = new StringBuilder();
			File file = new File(localPath + "\\" + fileName);
			String line = in.readLine();
			while (line != null) {
				string.append(line);
				string.append("\n");
				line = in.readLine();
			}
			in.close();
			// deleting file on local
			file.delete();
			return string.toString();
		} catch (IOException e) {
			logger.error("Exception while reading content from file on server and exception text is: "
					+ RequestUtil.stackTraceToString(e));
			return "";
		} finally {
			if (sftpChannel != null) {
				sftpChannel.exit();
			}
			session.disconnect();
		}
	}

	/**
	 * Method to read content inside file which is on local.
	 * 
	 * @param localPath
	 *            Local directory path.
	 * @param fileName
	 *            File name.
	 * @return Content inside the file.
	 */
	public static String readExistingLocalFile(String localPath, String fileName) {

		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(
					localPath + "\\" + fileName)));
			StringBuilder string = new StringBuilder();
			String line = in.readLine();
			while (line != null) {
				string.append(line);
				string.append("\n");
				line = in.readLine();
			}
			in.close();
			return string.toString();
		} catch (IOException e) {
			logger.error("Exception while reading content from local file and exception text is: "
					+ RequestUtil.stackTraceToString(e));
			return "";
		}

	}

	/**
	 * Method to create file with given content.
	 * 
	 * @param filePath
	 *            Local file path including file name.
	 * @param content
	 *            Content which to be written in the file.
	 */
	public static void createFile(String filePath, String content) {
		try {
			File file = new File(filePath);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			logger.error("Exception while creating file and exception text is: "
					+ RequestUtil.stackTraceToString(e));
		}

	}

}
