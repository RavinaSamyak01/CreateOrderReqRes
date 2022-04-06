package ConnectReqRespProject.ConnectReqResp;

import java.util.Calendar;

import javax.management.relation.RelationNotification;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class ReqResTime {

	public static StringBuilder msg = new StringBuilder();
	public static Logger logger;

	public static void renameFiles(String dirPath) {
		try {
			File directoryPath = new File(dirPath);
			String[] files = directoryPath.list();
			for (String file : files) {

				File inFile = new File(dirPath + file);
				Path path = Paths.get(inFile.getAbsolutePath());
				BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
				FileTime creationTime = attributes.lastModifiedTime();
				String dateStr = creationTime.toString();
				String prefix = dateStr.substring(5, 7) + dateStr.substring(8, 10) + dateStr.substring(0, 4) + "-"
						+ dateStr.substring(11, 13) + dateStr.substring(14, 16) + "-";

				Path fileToMovePath = Paths.get(dirPath + file);
				Path targetPath = Paths.get(dirPath + prefix + file);
				Files.move(fileToMovePath, targetPath);
				// System.out.println(dateStr+"\t"+dirPath+file+" File rename to
				// "+dirPath+prefix+file);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	@Test
	public static void orderReqRes() throws Exception {
		// TODO Auto-generated method stub
		String methodName = "";
		String Request = "";
		String TT = "", ST = "", ET = "";
		String inLine = "";
		int ttInx = -1, stInx = -1, etInx = -1;
		Calendar eventDate = Calendar.getInstance();
		int year = eventDate.get(Calendar.YEAR);
		String ed = "";
		String et = "";
		String eRt = "";

		String[] lineSplits = new String[250];
		String[] lineDetails = new String[250];
		BufferedReader br = null;
		BufferedWriter bw = null;
		// File outFile = new File("Applicationserver_0228to03_05.csv");
		boolean renameFilesOnly = true;
		String baseFolderName = "C:\\Ravina\\ConnectLogFiles\\Logs\\";
		// String baseFolderNameNew = "C:\\MyProjects\\Temp\\LogFiles\\";

		File FdirectoryPath = new File(baseFolderName);
		String[] Folders = FdirectoryPath.list();
		int FLen = Folders.length;
		System.out.println(FLen);

		for (String Folder : Folders) {
			String SubFolder = baseFolderName + Folder + "\\";
			System.out.println(SubFolder);
			File outFile = new File(Folder + "_CreateOrderReqRes.csv");
			try {
				if (renameFilesOnly) {
					renameFiles(SubFolder);
					// System.exit(0);
				}
				File directoryPath = new File(SubFolder);
				String[] files = directoryPath.list();

				for (String file : files) {

					File inFile = new File(SubFolder + file);

					System.out.println("Processing..." + inFile.getAbsolutePath());

					br = new BufferedReader(new FileReader(inFile));
					bw = new BufferedWriter(new FileWriter(outFile, true));

					while ((inLine = br.readLine()) != null) {
//					System.out.println(inLine);
						if (inLine.indexOf("::") > -1) {
							lineSplits = inLine.split("::");
							lineDetails = lineSplits[0].split(" ");
							methodName = lineDetails[0].trim();

							lineDetails = lineSplits[1].split(",");
							Request = lineDetails[0].trim();
							// -Again split the line
							lineSplits = inLine.split("::");
							lineDetails = lineSplits[1].split(",");

							if (methodName.contains("nglOrderAPI_createOrder_2")
									&& Request.contains("------------Request-----------")) {
								methodName = "nglOrderAPI_createOrder_2";
								for (String lineDetail : lineDetails) {
									if (lineDetail.contains("TT[")) {
										ttInx = lineDetail.indexOf("TT[");
										TT = lineDetail.substring(ttInx + 3, lineDetail.length() - 1).trim();
									} else if (lineDetail.startsWith("ST[")) {
										stInx = lineDetail.indexOf("ST[");
										ST = lineDetail.substring(stInx + 3, lineDetail.length() - 1);
										String[] splitDateTime = ST.split(" ");
										String[] splitDate = splitDateTime[0].split("/");
										String[] splitTime = splitDateTime[1].split(":");
										double secs = Double.parseDouble(splitTime[2]);
										int seconds = (int) secs;
										int milliSeconds = (int) ((secs - seconds) * 1000);
										eventDate.set(Calendar.DATE, Integer.parseInt(splitDate[1]));
										eventDate.set(Calendar.MONTH, Integer.parseInt(splitDate[0]) - 1);
										eventDate.set(Calendar.YEAR, year);
										eventDate.set(Calendar.HOUR, Integer.parseInt(splitTime[0]));
										eventDate.set(Calendar.MINUTE, Integer.parseInt(splitTime[1]));
										eventDate.set(Calendar.SECOND, seconds);
										eventDate.set(Calendar.MILLISECOND, milliSeconds);
										ed = splitDate[0] + "/" + splitDate[1] + "/" + year;
										et = splitTime[0] + ":" + splitTime[1] + ":" + seconds + "." + milliSeconds;
										/*
										 * ed = (eventDate.get(Calendar.MONTH) + 1) + "/" + eventDate.get(Calendar.DATE)
										 * + "/" + eventDate.get(Calendar.YEAR); et = eventDate.get(Calendar.HOUR) + ":"
										 * + eventDate.get(Calendar.MINUTE) + ":" + eventDate.get(Calendar.SECOND) + "."
										 * + eventDate.get(Calendar.MILLISECOND);
										 */
									} else if (lineDetail.startsWith("ET[")) {
										etInx = lineDetail.indexOf("ET[");
										ET = lineDetail.substring(etInx + 3, lineDetail.length() - 1);
										String[] splitTime = ET.split(":");
										double secs = Double.parseDouble(splitTime[2]);
										int seconds = (int) secs;
										int milliSeconds = (int) ((secs - seconds) * 1000);
										eventDate.set(Calendar.HOUR, Integer.parseInt(splitTime[0]));
										eventDate.set(Calendar.MINUTE, Integer.parseInt(splitTime[1]));
										eventDate.set(Calendar.SECOND, seconds);
										eventDate.set(Calendar.MILLISECOND, milliSeconds);
										// eRt = splitTime[0] + ":" + splitTime[1] + ":" + seconds + "." + milliSeconds;
										eRt = splitTime[0] + ":" + splitTime[1] + ":" + seconds + "." + milliSeconds;

										/*
										 * ed = (eventDate.get(Calendar.MONTH) + 1) + "/" + eventDate.get(Calendar.DATE)
										 * + "/" + eventDate.get(Calendar.YEAR); et = eventDate.get(Calendar.HOUR) + ":"
										 * + eventDate.get(Calendar.MINUTE) + ":" + eventDate.get(Calendar.SECOND) + "."
										 * + eventDate.get(Calendar.MILLISECOND);
										 */
									}
								}

								double ttValue = Double.parseDouble(TT);
								if (ttValue > 5000.0) {
									System.out.println(file + "\t" + inLine);
								}
								bw.write(ed + ", " + et + ", " + eRt + "," + methodName.trim() + "," + Math.abs(ttValue)
										+ "\r\n");
								// System.out.println(ed+" "+et+","+methodName.trim() + "," + Math.abs(ttValue)
								// );
							}

//						System.out.println();
						}
					}
					br.close();

					bw.close();

				}
			} catch (Exception e) {
				System.out.println(e.toString());
				System.out.println("lineSplits " + lineSplits.length + "\tLineDetails " + lineDetails.length);
				System.out.println(inLine);
				System.out.println(methodName + "\t ::TT::" + TT + "\t::ST::" + ST);
			}

			// --Send Email
			String subject = "Selenium Automation Script: Create Order Request/Response Time";
			String File = ".\\NetAgent server_CreateOrderReqRes.csv";
			try {
				msg.append("Please check attached file for details");
				// asharma@samyak.com,sdas@samyak.com,pgandhi@samyak.com,byagnik@samyak.com,pdoshi@samyak.com
				Email.sendMail("ravina.prajapati@samyak.com,asharma@samyak.com,parth.doshi@samyak.com", subject,
						msg.toString(), File);
			} catch (Exception ex) {
				Logger.getLogger(RelationNotification.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

	}

}
