package what.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import api.forum.forumsections.ForumSections;
import api.soup.MySoup;
import api.util.CouldNotLoadException;

public class Main {
	private final static String SITE = "http://what.cd/";
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private UserSettings userSettings;
	private Scanner scanner = new Scanner(System.in);
	private String username, password;;
	private ArrayList<Integer> monitoredForumIds = new ArrayList<Integer>();
	private int refreshRate;

	public Main() {
		MySoup.setSite(SITE);

		configureSettings();
		startForumCheckerThread();

	}

	private void startForumCheckerThread() {
		executor.scheduleAtFixedRate(new ForumChecker(), 0, userSettings.getRefreshRate(), TimeUnit.MINUTES);

	}

	private void configureSettings() {
		if (!checkForSettingsFile()) {
			login(false);
			chooseForumSections();
			chooseRefereshRate();
			saveSettings();
		} else {
			try {
				userSettings = UserSettings.userSettingsFromSave();
				login(true);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Could not load settings");
				System.exit(0);
			}

		}

	}

	private boolean checkForSettingsFile() {
		File f = new File("settings");
		if (f.exists())
			return true;
		return false;
	}

	private void login(boolean auto) {
		if (auto) {
			try {
				MySoup.login("login.php", userSettings.getUsername(), userSettings.getPassword());
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				MySoup.login("login.php", username, password);
			} catch (CouldNotLoadException e) {
				System.err.println("Could not login");
				System.exit(0);
			}

			System.out.println("Logged in \n");

		}
	}

	private void chooseForumSections() {
		ForumSections fs = MySoup.loadForumSections();
		fs.loadForumsList();
		for (int i = 0; i < fs.getForumsList().size(); i++) {
			System.out.println(fs.getForumsList().get(i).getForumName() + " - " + fs.getForumsList().get(i).getForumId());
		}
		while (true) {
			try {
				System.out
						.println("Enter a ids of forum sections you wish to monitor, seperated by commas. For example: 7,20,16");
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

	}

	private void saveSettings() {
		userSettings = new UserSettings(username, password, monitoredForumIds, refreshRate);
		try {
			userSettings.saveSettings();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not save settings");
			System.exit(0);
		}
	}

	private void showHelp() {
		System.out.println("WhatThreadHoarder usage\n");
		System.out.println("List of options\n");

	}

	public static void main(String[] args) {
		if (args[0].trim().equalsIgnoreCase("-h") || args[0].trim().equalsIgnoreCase("-help")) {

		}
		new Main();
	}

}
