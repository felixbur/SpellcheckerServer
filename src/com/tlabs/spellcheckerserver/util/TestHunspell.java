package com.tlabs.spellcheckerserver.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.felix.util.FileUtil;
import com.felix.util.Util;
import com.felix.util.logging.LoggerInterface;
import com.felix.util.logging.SystemOutLogger;

import dk.dren.hunspell.Hunspell;
import dk.dren.hunspell.Hunspell.Dictionary;

public class TestHunspell {
	Hunspell _hunspell;
	LoggerInterface _logger;
	String _dicNames = "de_DE;cx";

	public TestHunspell() {
		_logger = new SystemOutLogger();
		initHunspell();
		checkSpelling("kühlschrank");

	}

	public static void main(String[] args) {
		new TestHunspell();
	}

	private void initHunspell() {
		try {
			_hunspell = null;
			_hunspell = Hunspell.getInstance();
			_logger.debug("HunSpell initialized --- loading dics");

			String[] dic = _dicNames.split(";");
			for (String d : dic) {
				Dictionary dd = _hunspell.getDictionary("WebContent/res/dict/"
						+ d + "/" + d);
				if (dd != null) {
					_logger.debug("Dictionary " + d + " loaded");
				} else {
					_logger.debug("Could not load Dictionary " + d);
				}
			}
		} catch (Exception e) {
			System.out.println("Could not initialize HunSpell");
			System.out.println(e.getMessage());
		}
	}

	private List<String> checkSpelling(String word) {
		List<String> ret = new ArrayList<String>();
		String[] dic = _dicNames.split(";");
		for (String d : dic) {
			String lang = "WebContent/res/dict/"+d+"/"+d;
			try {
				if (_hunspell.getDictionary(lang).misspelled(word)) {
					ret.addAll(_hunspell.getDictionary(lang).suggest(word));
				} else {
					return null;
				}
			} catch (Exception e) {
				Util.errorOut(e, _logger);
			}
			for (String s : ret) {
				_logger.debug(s);
			}
		}
		return ret;
	}

	private void addWordToDictionary(String word) {
		String dicName = "res/dict/de_DE/de_DE.dic";
		try {
			Vector fileLines = FileUtil.getFileLines(dicName);
			fileLines.add(word);
			FileUtil.writeFileContent(dicName, fileLines);
			initHunspell();
		} catch (Exception e) {
			Util.errorOut(e, _logger);
		}
	}

}
