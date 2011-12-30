package what.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The Class UserSettings.
 * 
 * //TODO description
 * 
 * @author Gwindow
 */
public class Settings {

	/** The prefs. */
	private static Preferences prefs;

	/** The set. */
	private static HashSet<String> set = new HashSet<String>();

	/**
	 * Instantiates a new user settings.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param monitoredForumIds
	 *            the monitored forum ids
	 * @param refreshRate
	 *            the refresh rate
	 */
	public Settings(String username, String password, ArrayList<Integer> monitoredForumIds, int refreshRate) {
		prefs = Preferences.userNodeForPackage(this.getClass());
		this.saveUsername(username);
		this.savePassword(password);
		this.saveMonitoredForumIds(monitoredForumIds);
		this.saveRefreshRate(refreshRate);
		set = loadSet();

	}

	/**
	 * Instantiates a new user settings.
	 */
	public Settings() {
		prefs = Preferences.userNodeForPackage(this.getClass());
		set = loadSet();
	}

	/**
	 * Load set.
	 * 
	 * @return the hash set
	 */
	private HashSet<String> loadSet() {
		HashSet<String> set = new HashSet<String>();
		String s = prefs.get("set", "");
		s = s.replace("[", "").replace("]", "").replaceAll("\\s+", "");
		StringTokenizer st = new StringTokenizer(s, ",");
		while (st.hasMoreTokens()) {
			set.add(st.nextToken());
		}
		return set;
	}

	/**
	 * Adds the to set.
	 * 
	 * @param s
	 *            the s
	 */
	public static void addToSet(String s) {
		set.add(s);
		prefs.put("set", set.toString());

	}

	/**
	 * Clear settings.
	 * 
	 * @throws BackingStoreException
	 *             the backing store exception
	 */
	public void clearSettings() throws BackingStoreException {
		prefs.remove("username");
		prefs.remove("password");
		prefs.remove("monitoredForumIds");
		prefs.remove("refreshRate");
	}

	/**
	 * Flush set.
	 */
	public void flushSet() {
		prefs.remove("set");
	}

	/**
	 * Checks for settings.
	 * 
	 * @return true, if successful
	 */
	public boolean hasSettings() {
		if ((this.getUsername().length() > 0) && (this.getPassword().length() > 0))
			return true;
		return false;

	}

	/**
	 * String to integer list.
	 * 
	 * @param s
	 *            the s
	 * @return the array list
	 */
	private ArrayList<Integer> stringToIntegerList(String s) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		s = s.replace("[", "").replace("]", "").replaceAll("\\s+", "");
		StringTokenizer st = new StringTokenizer(s, ",");
		while (st.hasMoreTokens()) {
			list.add(Integer.parseInt(st.nextToken()));
		}
		return list;
	}

	/**
	 * Gets the monitored forum ids.
	 * 
	 * @return the monitored forum ids
	 */
	public ArrayList<Integer> getMonitoredForumIds() {
		String s = prefs.get("monitoredForumIds", "");
		return stringToIntegerList(s);
	}

	/**
	 * Save monitored forum ids.
	 * 
	 * @param monitoredForumIds
	 *            the monitored forum ids
	 */
	public void saveMonitoredForumIds(ArrayList<Integer> monitoredForumIds) {
		prefs.put("monitoredForumIds", monitoredForumIds.toString());
	}

	/**
	 * Gets the username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return prefs.get("username", "");
	}

	/**
	 * Save username.
	 * 
	 * @param username
	 *            the username
	 */
	public void saveUsername(String username) {
		prefs.put("username", username);
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return prefs.get("password", "");
	}

	/**
	 * Save password.
	 * 
	 * @param password
	 *            the password
	 */
	public void savePassword(String password) {
		prefs.put("password", password);
	}

	/**
	 * Gets the refresh rate.
	 * 
	 * @return the refresh rate
	 */
	public int getRefreshRate() {
		return prefs.getInt("refreshRate", 15);
	}

	/**
	 * Save refresh rate.
	 * 
	 * @param refreshRate
	 *            the refresh rate
	 */
	public void saveRefreshRate(int refreshRate) {
		prefs.putInt("refreshRate", refreshRate);
	}

	/**
	 * Gets the sets the.
	 * 
	 * @return the sets the
	 */
	public static HashSet<String> getSet() {
		return set;
	}

}
