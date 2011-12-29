package what.main;

import java.util.ArrayList;

import api.forum.section.Section;

public class ForumChecker implements Runnable {
	private ArrayList<Integer> monitoredForumIds;

	public ForumChecker() {
		try {
			monitoredForumIds = UserSettings.userSettingsFromSave().getMonitoredForumIds();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		for (int i = 0; i < monitoredForumIds.size(); i++) {
			Section s = Section.sectionFromFirstPage(monitoredForumIds.get(i));
			System.out.println(s.getResponse().getForumName() + " - " + s.getNumberOfUnreadThreads() + " unread threads");
		}
	}
}
