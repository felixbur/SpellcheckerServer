package com.tlabs.spellcheckerserver.util;

import com.felix.util.KeyValues;
import com.felix.webmaintenance.ConfigFile;

public class UserVocabAdd {
	private ConfigFile _actFile;
	private KeyValues _config = null;

	public UserVocabAdd(KeyValues config) {
		_config = config;
	}

	public void setActFile(String name) {
		_actFile = new ConfigFile(_config.getPathValue("tempDir") + "/" + name,
				name);
	}

	public ConfigFile getActFile() {
		return _actFile;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
