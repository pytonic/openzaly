package com.akaxin.site.boot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akaxin.site.boot.config.ConfigHelper;
import com.akaxin.site.boot.config.ConfigKey;
import com.akaxin.site.storage.DataSourceManager;
import com.akaxin.site.storage.exception.UpgradeDatabaseException;
import com.akaxin.site.storage.sqlite.manager.DBConfig;
import com.akaxin.site.storage.sqlite.sql.SQLConst;

public class Helper {
	private static final Logger logger = LoggerFactory.getLogger(Helper.class);

	/**
	 * 
	 * @param args
	 * @return true:only print helper message false:start openzaly-server
	 */
	public static boolean startHelper(String[] args) {
		PrintWriter pw = null;
		try {
			Options options = new Options();
			options.addOption("h", false, "help message list");
			options.addOption("help", false, "help message list");
			options.addOption("upgrade", false, "upgrade openzaly server");
			DefaultParser posixParser = new DefaultParser();
			CommandLine commandLine = posixParser.parse(options, args);

			if (commandLine.hasOption("h") || commandLine.hasOption("help")) {
				pw = new PrintWriter(System.out);
				// 1.print logo
				showAkaxinBanner(pw);
				// 2.print Userage
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.printHelp("java [<name>=value] -jar openzaly-server.jar [-h|-help]", options, false);
				// 3.print helper message
				printHelperMessage(pw);
				return true;
			} else if (commandLine.hasOption("upgrade")) {
				pw = new PrintWriter(System.out);
				upgrade(pw);
				return true;
			}
			return false;
		} catch (ParseException e) {
			logger.error("print helper with startHelper error", e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		return true;
	}

	public static void showAkaxinBanner(PrintWriter pw) {
		BufferedReader buffer = null;
		try {
			InputStream inputStream = Helper.class.getResourceAsStream("/logo.txt");
			buffer = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				pw.println(line);
			}
			pw.flush();
			inputStream.close();
		} catch (Exception e) {
			logger.error("read helper message from file error", e);
		} finally {
			// 关闭buffer
			try {
				if (buffer != null) {
					buffer.close();
				}
			} catch (IOException e) {
				logger.error("buffer close error with IOException");
			}
		}
	}

	public static void buildEnvToSystemOut(PrintWriter pwriter) {
		pwriter.println();
		// openzaly version
		String ozVersion = ConfigHelper.getStringConfig(ConfigKey.SITE_VERSION);
		pwriter.println("openzaly-version : " + ozVersion);
		// os
		String osName = System.getProperty("os.name");
		String dataModel = System.getProperty("sun.arch.data.model"); // 32位 or 64位
		pwriter.println("OS Name : " + osName + " " + dataModel + "位");
		// java version
		String javaVersion = System.getProperty("java.version"); // 获取JDK版本
		pwriter.println("java-version : " + javaVersion);

		pwriter.println();
		pwriter.println("[OK] openzaly-server is starting...");
		pwriter.flush();
	}

	public static void startSuccess(PrintWriter pwriter) {
		pwriter.println("[OK] start openzaly-server successfully");
		pwriter.flush();
	}

	public static void startFail(PrintWriter pwriter) {
		pwriter.println("[Error] start openzaly-server failed, server exit...");
		pwriter.println();
		pwriter.flush();
	}

	public static void startFailWithError(PrintWriter pwriter, String errMessage) {
		pwriter.println("[Error] error message:" + errMessage);
		pwriter.println("[Error] start openzaly-server failed, server exit...");
		pwriter.println();
		pwriter.flush();
	}

	public static void printUpgradeWarn(PrintWriter pwriter) {
		pwriter.println("[Error] openzaly-server is an old version, you can execute following command to upgrade:");
		pwriter.println();
		pwriter.println("\t java -jar openzaly-server.jar -upgrade");
		pwriter.println();
		pwriter.flush();
	}

	private static void printHelperMessage(PrintWriter pw) {
		pw.println();
		pw.println("example:java -Dsite.port=2021 -jar openzaly-server.jar ");
		pw.println();
		pw.println("\t-Dsite.address \t\topenzaly Netty address default:0.0.0.0");
		pw.println("\t-Dsite.port \t\topenzaly Netty port default:2021");
		pw.println("\t-Dpluginapi.address \topenzaly Http address default: 0.0.0.0");
		pw.println("\t-Dpluginapi.port \topenzaly Http port default:8280");
		pw.println("\t-Dsite.admin.uic \topenzaly first uic for admin port default: \"000000\"");
		pw.println("\t-Dsite.baseDir \t\topenzaly openzaly-server root dir default:./");
		pw.println();
		pw.flush();
	}

	private static void upgrade(PrintWriter pw) {
		pw.println();
		pw.println("[INFO] start to upgrade openzaly-server...");
		try {
			String dbDir = ConfigHelper.getStringConfig(ConfigKey.SITE_BASE_DIR);
			String siteVersion = ConfigHelper.getStringConfig(ConfigKey.SITE_VERSION);
			DBConfig config = new DBConfig();
			config.setDbDir(dbDir);
			// 升级
			int dbUserVersion = DataSourceManager.upgrade(config);
			int needVersion = SQLConst.SITE_DB_VERSION_10;
			pw.println("[INFO] upgrade openzaly-server version : " + siteVersion);
			if (needVersion == dbUserVersion) {
				pw.println("[OK] upgrade database user-version : " + dbUserVersion);
				pw.println("[OK] upgrade openzaly-server finish ...");
			} else {
				pw.println("[ERROR] upgrade database user-version : " + dbUserVersion);
				pw.println("[ERROR] upgrade openzaly-server fail ...");
			}
		} catch (UpgradeDatabaseException e) {
			pw.println("[ERROR] upgrade openzaly-server error");
			logger.error("upgrade database error", e);
		}
		pw.println();
		pw.flush();
	}

}
