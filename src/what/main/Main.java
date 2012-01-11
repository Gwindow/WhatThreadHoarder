package what.main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;

import api.forum.forumsections.ForumSections;
import api.index.Index;
import api.soup.MySoup;
import api.util.CouldNotLoadException;

/**
 * The Class Main.
 * 
 * //TODO description
 * 
 * @author Gwindow
 */
public class Main {

	/** The Constant SITE. */
	private final static String SITE = "http://what.cd/";

	/** The executor. */
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	/** The user settings. */
	private Settings settings = new Settings();

	/** The scanner. */
	private Scanner scanner = new Scanner(System.in);

	/** The password. */
	private String username, password;;

	/** The monitored forum ids. */
	private ArrayList<Integer> monitoredForumIds = new ArrayList<Integer>();

	/** The refresh rate. */
	private int refreshRate;

	/** The df. */
	private DecimalFormat df = new DecimalFormat("#.00");

	/**
	 * Instantiates a new main.
	 * 
	 * @param args
	 *            the args
	 */
	public Main(String... args) {
		MySoup.setSite(SITE);
		if (args.length > 0) {
			if (args[0].trim().equalsIgnoreCase("-h") || args[0].trim().equalsIgnoreCase("-help")) {
				showHelp();
				System.exit(0);

			}
			if (args[0].trim().equalsIgnoreCase("-c") || args[0].trim().equalsIgnoreCase("-clear")) {
				try {
					settings.clearSettings();
					System.out.println("Settings cleared");
				} catch (BackingStoreException e) {
					System.err.println("Could not clear settings");
				}
				System.exit(0);

			}
			if (args[0].trim().equalsIgnoreCase("-f") || args[0].trim().equalsIgnoreCase("-flush")) {
				settings.flushSet();
				System.out.println("Thread list flushed");
				System.exit(0);
			}
			if (args[0].trim().equalsIgnoreCase("-p") || args[0].trim().equalsIgnoreCase("-print")) {
				System.out.println(Settings.getSet().toString());
				System.exit(0);
			}
			if (args[0].trim().equalsIgnoreCase("-r") || args[0].trim().equalsIgnoreCase("-run")) {
				configureSettings();
				startForumCheckerThread();
			}
			if (args[0].trim().equalsIgnoreCase("-s") || args[0].trim().equalsIgnoreCase("-stats")) {
				if (!settings.hasSettings()) {
					login(false);
				} else {
					login(true);
				}
				showUserStats();
				System.exit(0);
			}

		} else {
			configureSettings();
			startForumCheckerThread();
		}

	}

	/**
	 * Start forum checker thread.
	 */
	private void startForumCheckerThread() {
		executor.scheduleAtFixedRate(new ForumChecker(), 0, settings.getRefreshRate(), TimeUnit.MINUTES);

	}

	/**
	 * Configure settings.
	 */
	private void configureSettings() {
		if (!settings.hasSettings()) {
			login(false);
			chooseForumSections();
			chooseRefereshRate();
			saveSettings();
		} else {
			try {
				settings = new Settings();
				login(true);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Could not load settings");
				System.exit(0);
			}
		}
	}

	/**
	 * Login.
	 * 
	 * @param auto
	 *            the auto
	 */
	private void login(boolean auto) {
		if (auto) {
			try {
				MySoup.login("login.php", settings.getUsername(), settings.getPassword());
				System.out.println("Logged in \n");
			} catch (CouldNotLoadException e) {
				System.err.println("Could not login");
			}
		} else {
			while (true) {
				try {
					System.out.println("Enter username:");
					username = scanner.nextLine();
					System.out.println("Enter password:");
					password = scanner.nextLine();
					break;
				} catch (Exception e) {
					System.err.println("Error parsing input");
				}
			}
			try {
				MySoup.login("login.php", username, password);
			} catch (CouldNotLoadException e) {
				System.err.println("Could not login");
				System.exit(0);
			}
			System.out.println("\nLogged in\n");
		}
	}

	/**
	 * Choose forum sections.
	 */
	private void chooseForumSections() {
		ForumSections fs = MySoup.loadForumSections();
		fs.loadForumsList();
		for (int i = 0; i < fs.getForumsList().size(); i++) {
			System.out.println(fs.getForumsList().get(i).getForumName() + " - " + fs.getForumsList().get(i).getForumId());
		}
		while (true) {
			try {
				System.out
						.println("\nEnter a ids of forum sections you wish to monitor, seperated by commas. For example: 7,20,16");
				String ids = scanner.nextLine();
				StringTokenizer tokenizer = new StringTokenizer(ids, ",");
				while (tokenizer.hasMoreTokens()) {
					monitoredForumIds.add(Integer.parseInt(tokenizer.nextToken().trim()));
				}
				break;
			} catch (NumberFormatException e) {
				System.err.println("Error parsing input");
			}
		}
	}

	/**
	 * Choose referesh rate.
	 */
	private void chooseRefereshRate() {
		while (true) {
			try {
				System.out.println("How often should the forums be checked (in minutes)? Minimum is 15 minutes. For example: 30");
				refreshRate = scanner.nextInt();
				if (refreshRate < 15) {
					System.err.println("Minimum rate is 15 minutes");
				} else {
					break;
				}
			} catch (Exception e) {
				System.err.println("Error parsing input");
			}
		}
		System.out.println();
	}

	/**
	 * Save settings.
	 */
	private void saveSettings() {
		try {
			settings.savePassword(password);
			settings.saveUsername(username);
			settings.saveRefreshRate(refreshRate);
			settings.saveMonitoredForumIds(monitoredForumIds);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not save settings");
			System.exit(0);
		}
	}

	/**
	 * Show user stats.
	 */
	private void showUserStats() {
		Index index = MySoup.getIndex();
		System.out.println(index.getResponse().getUsername());
		System.out.println("Uploaded: " + toGBString(index.getResponse().getUserstats().getUploaded().doubleValue()) + " GB");
		System.out.println("Downloaded: " + toGBString(index.getResponse().getUserstats().getDownloaded().doubleValue()) + " GB");
		System.out.println("Ratio: " + index.getResponse().getUserstats().getRatio());
		System.out.println("Required Ratio: " + index.getResponse().getUserstats().getRequiredratio());
		System.out.println("Buffer: " + toGBString(index.getResponse().getUserstats().getBuffer().doubleValue()) + " GB");
		System.out.println("");

	}

	/**
	 * To gb string.
	 * 
	 * @param d
	 *            the d
	 * @return the string
	 */
	private String toGBString(Double d) {

		return df.format((d / Math.pow(1024, 3)));
	}

	/**
	 * Show help.
	 */
	private void showHelp() {
		System.out.println("WhatThreadHoarder - version 0.1 \n");
		System.out.println("List of options\n");
		System.out.println("-c clear \t clears the user settings");
		System.out.println("-f flush \t flushes the list of thread ids, don't use this if you don't know what you are doing");
		System.out.println("-p print \t prints the list of saved threads, not for the faint of the heart");
		System.out.println("-r run \t\t run program, if no option is included this is done by default");
		System.out.println("-s stats \t show simple user stats");
		System.out.println("-h help \t show the help menu");
		System.out.println();

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		new Main(args);
	}
}
