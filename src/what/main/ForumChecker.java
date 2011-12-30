package what.main;

import java.util.ArrayList;

import api.forum.section.Section;

/**
 * The Class ForumChecker.
 * 
 * //TODO description
 * 
 * @author Gwindow
 */
public class ForumChecker implements Runnable {

	/** The monitored forum ids. */
	private ArrayList<Integer> monitoredForumIds;

	/**
	 * Instantiates a new forum checker.
	 */
	public ForumChecker() {
		monitoredForumIds = new Settings().getMonitoredForumIds();
	}

	/* (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() */
	@Override
	public void run() {
		for (int i = 0; i < monitoredForumIds.size(); i++) {
			Section s = Section.sectionFromFirstPage(monitoredForumIds.get(i));
			System.out.println(s.getResponse().getForumName());
			System.out.println("Number of unread threads: " + s.getNumberOfUnreadThreads() + "\n");
			for (int j = 0; j < s.getResponse().getThreads().size(); j++) {
				if (!Settings.getSet().contains(s.getResponse().getThreads().get(j).getTopicId().toString())) {
					if (!s.getResponse().getThreads().get(j).isRead()) {
						System.out.println(s.getResponse().getThreads().get(j).getTitle());
						s.getResponse().getThreads().get(j).subscribe();
						Settings.addToSet(s.getResponse().getThreads().get(j).getTopicId().toString());
					}
				}
			}
			System.out.println("\n");
		}
	}
}
