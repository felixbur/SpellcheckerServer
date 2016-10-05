package com.tlabs.spellcheckerserver;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.felix.util.FileUtil;
import com.felix.util.KeyValues;
import com.felix.util.StringUtil;
import com.felix.util.Util;
import com.felix.util.logging.LoggerInterface;
import com.tlabs.rootvole.InputString;
import com.tlabs.rootvole.MultiStringWord;

import dk.dren.hunspell.Hunspell;
import dk.dren.hunspell.Hunspell.Dictionary;

public class HunspellWrapper {
	private static final int _MAXTRIWORDLEVENSHTEIN = 3;
	private static final int _MAXBIWORDLEVENSHTEIN = 2;
	private Hunspell _hunspell;
	private LoggerInterface _logger;
	private KeyValues _config;
	private String _logString = "";
	private String _langFiles = "CX;DE_CX;de_DE;de_DEAT";
	private String _dictionaries = "";
	private Vector<String> _multiWordUserVocab = null;
	private int _userGeneralMinDistance = 0, _maxLevenshtein = 10;

	private Dictionary CX = null, DE_CX = null, de_DE = null, de_DEAT = null;

	public HunspellWrapper(LoggerInterface _logger, KeyValues config) {
		this._logger = _logger;
		_config = config;
		_userGeneralMinDistance = _config.getInt("userGeneralVocabMinDistance");
		_maxLevenshtein = _config.getInt("maxLevenshtein");
		_dictionaries = _config.getString("dictionaries");
		String userDicPath = _config.getPathValue("userDicPath");
		;
		try {
			_multiWordUserVocab = StringUtil.getMultiwords(FileUtil
					.getFileLines(userDicPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		initHunspell();
	}

	private void initHunspell() {
		try {
			_hunspell = null;
			_hunspell = Hunspell.getInstance();
			_logger.debug("HunSpell initialized --- loading _langFiles");
			CX = _hunspell.getDictionary(_config.getPathValue("dicFilePath")
					+ "CX/CX");
			DE_CX = _hunspell.getDictionary(_config.getPathValue("dicFilePath")
					+ "DE_CX/DE_CX");
			de_DE = _hunspell.getDictionary(_config.getPathValue("dicFilePath")
					+ "de_DE/de_DE");
			de_DEAT = _hunspell.getDictionary(_config
					.getPathValue("dicFilePath") + "de_DEAT/de_DEAT");
		} catch (Exception e) {
			System.out.println("Could not initialize HunSpell");
			Util.errorOut(e, _logger);
		}
	}

	public boolean reloadUserDic() {
		try {
			CX.destroy();
			CX = _hunspell.getDictionary(_config.getPathValue("dicFilePath")
					+ "CX/CX");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return true;
	}

	public String getFirstSuggestions(String phrase, String dictionaries) {
		_logString = "";
		_dictionaries = dictionaries;
		phrase = StringUtil
				.removeNonAlphanumericCharactersExcludingHyphen(phrase);

		String retS = "";
		String[] stringArray = new String[0];

		if (_config.getBool("withMultiwordReco")) {
			InputString inputString = new InputString(phrase, null);
			// for (Iterator iterator =
			// inputString.getCombinations(3).iterator(); iterator
			// .hasNext();) {
			// MultiStringWord string = (MultiStringWord) iterator.next();
			// System.out.println(string.toString());
			// }
			inputString = checkTriWords(inputString);
			for (Iterator iterator = inputString.getReplacedWords().iterator(); iterator
					.hasNext();) {
				String string = (String) iterator.next();
				retS += string + " ";
			}
		} else {
			stringArray = StringUtil.stringToArray(phrase);
			for (int i = 0; i < stringArray.length; i++) {
				String string = stringArray[i];
				string = getFirstSuggestion(string);
				retS += string + " ";
			}
		}
		_logger.debug("req: " + phrase + "; resp:  " + _logString);
		return retS.trim();
	}

	public String getFirstUserVocabSuggestion(String word) {
		String ret = word;
		try {
			if (CX.misspelled(word)) {
				List<String> suggestions = CX.suggest(word);
				if (suggestions.size() > 0)
					ret = suggestions.get(0);
				else
					ret = "";
			}

		} catch (Exception e) {
			Util.errorOut(e, _logger);
		}
		return ret;

	}

	public String getFirstSuggestion(String word) {
		List<String> ls = checkSpelling(word);
		String retS = word;
		if (ls == null || ls.isEmpty()) {
			_logString += "(" + word + "); ";
			return word;
		}
		try {
			retS = (String) (ls).get(0);
			_logString += "(req: " + word + ", resp:  " + ls.toString() + ")";
		} catch (Exception e) {
			e.printStackTrace();
			_logger.debug(e.getMessage());
		}
		return retS;
	}

	public List<String> checkSpelling(String word) {
		List<String> ret = new ArrayList<String>(1);
		String[] dic = _dictionaries.split(";");
		if (dic.length == 1) {
			if (dic[0].compareTo("CX") == 0) {
				try {
					if (CX.misspelled(word))
						ret = CX.suggest(word);
				} catch (Exception e) {
					Util.errorOut(e, _logger);
				}
			} else if (dic[0].compareTo("de_DE") == 0) {
				try {
					if (de_DE.misspelled(word))
						ret = de_DE.suggest(word);
				} catch (Exception e) {
					Util.errorOut(e, _logger);
				}
			} else if (dic[0].compareTo("DE_CX") == 0) {
				try {
					if (DE_CX.misspelled(word))
						ret = DE_CX.suggest(word);
				} catch (Exception e) {
					Util.errorOut(e, _logger);
				}
			} else if (dic[0].compareTo("de_DEAT") == 0) {
				try {
					if (de_DEAT.misspelled(word))
						ret = de_DEAT.suggest(word);
				} catch (Exception e) {
					Util.errorOut(e, _logger);
				}
			}

		} else if (dic.length == 2) {
			try {
				ret.add(checkSingleWord(word));

			} catch (Exception e) {
				Util.errorOut(e, _logger);
			}
		}

		return ret;
	}

	private String checkSingleWord(String word) {
		String userSuggest = word, generalSuggest = word;
		if (CX.misspelled(word)) {
			List<String> suggestions = CX.suggest(word);
			if (suggestions.size() > 0)
				userSuggest = suggestions.get(0);
			else
				userSuggest = "";
		}
		if (de_DEAT.misspelled(word)) {
			List<String> suggestions = de_DEAT.suggest(word);
			if (suggestions.size() > 0)
				generalSuggest = suggestions.get(0);
			else
				generalSuggest = "";
		}
		int levenUser = StringUtil.levenshteinDistance(userSuggest, word);
		int levenGeneral = StringUtil.levenshteinDistance(generalSuggest, word);
		if (levenUser > _maxLevenshtein && levenGeneral > _maxLevenshtein)
			return "";
		if (levenUser <= levenGeneral + _userGeneralMinDistance) {
			return userSuggest;
		} else {
			return generalSuggest;
		}

	}

	public void addWordToDictionary(String word) {
		String dicName = _config.getPathValue("dicFilePath")
				+ "/de_DE/de_DE.dic";
		try {
			Vector<String> fileLines = FileUtil.getFileLines(dicName);
			fileLines.add(word);
			FileUtil.writeFileContent(dicName, fileLines);
			initHunspell();
		} catch (Exception e) {
			Util.errorOut(e, _logger);
		}
	}

	public void destroy() {
		try {
			String dics = "de_DE;de_DEAT;DE_CX;CX";
			if (_hunspell != null) {
				String[] dic = dics.split(";");
				for (String d : dic) {
					_hunspell.destroyDictionary(_config
							.getPathValue("dicFilePath") + d + "/" + d);
				}
			}
			_hunspell = null;
		} catch (Exception e) {
			System.out.println("Could not destroy HunSpell");
			Util.errorOut(e, _logger);
		}
		System.out.println("destroy HunSpell");
		if (_logger != null)
			_logger.debug("_hunspell unloaded");
	}

	private void checkWordsBetween(InputString inputString, int start, int end) {
		Vector<MultiStringWord> words = inputString.filterMultistrings(1,
				start, end);
		for (Iterator iterator2 = words.iterator(); iterator2.hasNext();) {
			MultiStringWord word = (MultiStringWord) iterator2.next();
			String compare = checkSingleWord(word.get_word());
			// if (StringUtil.levenshteinDistance(word.get_word(),
			// compare) < 2) {
			// // System.out.println(word.toString());
			word.set_replacement(compare);
			// } else {
			// word.set_replaced();
			// }
		}

	}

	private void checkBiwordsBetween(InputString inputString, int start, int end) {
		Vector<MultiStringWord> biwords = inputString.filterMultistrings(2,
				start, end - 1);
		int lastChecked = start;
		for (Iterator iterator2 = biwords.iterator(); iterator2.hasNext();) {
			MultiStringWord biWord = (MultiStringWord) iterator2.next();
			String compare = getFirstUserVocabSuggestion(biWord.get_word());
			if (StringUtil.levenshteinDistance(biWord.get_word(), compare) < _MAXBIWORDLEVENSHTEIN) {
				biWord.set_replacement(compare);
				checkWordsBetween(inputString, lastChecked,
						biWord.get_offset() - 1);
				lastChecked = biWord.get_end();
			}
		}
		if (biwords.size() > 0) {
			if (lastChecked < end) {
				checkWordsBetween(inputString, lastChecked, end);
			} else if (lastChecked <= end) {
				checkWordsBetween(inputString, end, end);
			}
		} else {
			checkWordsBetween(inputString, start, end);
		}

	}

	private InputString checkTriWords(InputString inputString) {
		inputString.getCombinations(3);
		Vector<MultiStringWord> triwords = inputString.filterMultistrings(3, 0,
				inputString.getWordNum());
		int lastChecked = 0;
		;
		for (int triWordIndex = 0; triWordIndex < triwords.size(); triWordIndex++) {
			MultiStringWord triWord = (MultiStringWord) triwords
					.elementAt(triWordIndex);
			String compare = getFirstUserVocabSuggestion(triWord.get_word());
			if (StringUtil.levenshteinDistance(triWord.get_word(), compare) < _MAXTRIWORDLEVENSHTEIN) {
				// System.out.println(triWord.toString());
				// replace the string
				triWord.set_replacement(compare);
				// check the strings before
				checkBiwordsBetween(inputString, 0, triWord.get_offset() - 1);
				// ignore triwords that are part of the replaced
				triWordIndex += 2;
				// set last checked
				lastChecked = triWord.get_end();
			}
		}
		if (lastChecked < inputString.getWordNum()) {
			checkBiwordsBetween(inputString, lastChecked,
					inputString.getWordNum());
		}
		return inputString;
	}
}
