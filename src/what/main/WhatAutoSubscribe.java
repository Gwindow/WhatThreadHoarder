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

public class WhatAutoSubscribe {

	private final static String SITE = "http://what.cd/";
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private UserSettings userSettings;

	public WhatAutoSubscribe() {
		MySoup.setSite(SITE);

		configureSettings();
		startForumCheckerThread();

	}

	private void startForumCheckerThread() {
		executor.scheduleAtFixedRate(new ForumChecker(), 0, 60, TimeUnit.SECONDS);
	}

	private void configureSettings() {
		if (!checkForSettingsFile()) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter username:");
			String username = scanner.nextLine();

			System.out.println("Enter password:");
			String password = scanner.nextLine();

			try {
				MySoup.login("login.php", username, password);
			} catch (CouldNotLoadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Logged in \n");

			ForumSections fs = MySoup.loadForumSections();
			fs.loadForumsList();
			for (int i = 0; i < fs.getForumsList().size(); i++) {
				System.out.println(fs.getForumsList().get(i).getForumName() + " - " + fs.getForumsList().get(i).getForumId());
			}
			System.out.println("Enter a ids of forum sections you wish to monitor, seperated by commas. For example: 7,20,16");
			String ids = scanner.nextLine();
			ArrayList<Integer> list = new ArrayList<Integer>();
			StringTokenizer tokenizer = new StringTokenizer(ids, ",");
			while (tokenizer.hasMoreTokens()) {
				list.add(Integer.parseInt(tokenizer.nextToken().trim()));
			}
			userSettings = new UserSettings(username, password, list);
			try {
				userSettings.saveSettings();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				userSettings = UserSettings.userSettingsFromSave();
				login();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private boolean checkForSettingsFile() {
		File f = new File("settings");
		if (f.exists())
			return true;
		return false;
	}

	private void login() {
		try {
			MySoup.login("login.php", userSettings.getUsername(), userSettings.getPassword());
			System.out.println("Logged in \n");
		} catch (CouldNotLoadException e) {
			System.err.println("Could not login");
		}
	}

	public static void main(String[] args) {
		new WhatAutoSubscribe();
		System.out.println("Done");
	}

}
