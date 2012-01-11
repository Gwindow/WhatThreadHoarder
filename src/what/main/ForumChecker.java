package what.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import api.forum.section.Section;
import api.forum.thread.Thread;

/**
 * The Class ForumChecker.
 * 
 * //TODO description
 * 
 * @author Gwindow
 */
public class ForumChecker implements Runnable {
	private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
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
		System.out.println(getTime());
		for (int i = 0; i < monitoredForumIds.size(); i++) {
			Section s = Section.sectionFromFirstPage(monitoredForumIds.get(i));
			System.out.println(s.getResponse().getForumName());
			for (int j = 0; j < s.getResponse().getThreads().size(); j++) {
				if (!Settings.getSet().contains(s.getResponse().getThreads().get(j).getTopicId().toString())) {
					if (!s.getResponse().getThreads().get(j).isRead()) {
						System.out.println(s.getResponse().getThreads().get(j).getTitle());
						Thread t = Thread.threadFromFirstPage(s.getResponse().getThreads().get(j).getTopicId().intValue());
						t.subscribe();
						Settings.addToSet(s.getResponse().getThreads().get(j).getTopicId().toString());
					}
				}
			}
		}
		System.out.println("\nSleeping\n");
	}

	private String getTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}
}
