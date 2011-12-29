package what.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class UserSettings implements Serializable {
	private static final long serialVersionUID = 1347263442299771179L;

	private String username, password;
	private ArrayList<Integer> monitoredForumIds;

	/**
	 * @param username
	 * @param password
	 * @param monitoredForumIds
	 */
	public UserSettings(String username, String password, ArrayList<Integer> monitoredForumIds) {
		super();
		this.username = username;
		this.password = password;
		this.monitoredForumIds = monitoredForumIds;
	}

	public ArrayList<Integer> getMonitoredForumIds() {
		return monitoredForumIds;
	}

	public void setMonitoredForumIds(ArrayList<Integer> monitoredForumIds) {
		this.monitoredForumIds = monitoredForumIds;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void saveSettings() throws IOException {
		FileOutputStream f_out = new FileOutputStream("settings");
		ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
		obj_out.writeObject(this);
	}

	public static UserSettings userSettingsFromSave() throws Exception {
		FileInputStream f_in = new FileInputStream("settings");
		ObjectInputStream obj_in = new ObjectInputStream(f_in);
		Object obj = obj_in.readObject();
		if (obj instanceof UserSettings)
			return (UserSettings) obj;
		return null;
	}

	/* (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return "UserSettings [username=" + username + ", password=" + password + ", monitoredForumIds=" + monitoredForumIds + "]";
	}
}
