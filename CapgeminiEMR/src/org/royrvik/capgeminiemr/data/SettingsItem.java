package org.royrvik.capgeminiemr.data;

/**
 * Convenience class used by CurrentSetupActivity to show the items
 * currently in SharedPreferences.
 */

public class SettingsItem {

    private String key, value;

    public SettingsItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
