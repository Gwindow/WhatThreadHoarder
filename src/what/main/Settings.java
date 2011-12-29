package what.main;

import java.util.prefs.Preferences;

public class Settings {
	private Preferences prefs;

	public Settings() {
		prefs = Preferences.userNodeForPackage(this.getClass());
	}
}
