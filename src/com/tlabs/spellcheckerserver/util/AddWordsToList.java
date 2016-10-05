package com.tlabs.spellcheckerserver.util;

import java.util.Collections;
import java.util.Vector;

import com.felix.util.FileUtil;
import com.felix.util.StringUtil;

public class AddWordsToList {

	public AddWordsToList() {
	}

	public static Vector<String> addFileWordsToList(String oldwordsFile,
			String newwordsFile) throws Exception {
		Vector<String> oldWords = FileUtil.getFileLines(oldwordsFile);
		Vector<String> newWords = FileUtil.getFileLines(newwordsFile);
		return addFromMultiWordStringsUniqueAndSort(oldWords, newWords);
	}

	public static Vector<String> addFileWordsToList(String oldwordsFile,
			Vector<String> newwords) throws Exception {
		Vector<String> oldWords = FileUtil.getFileLines(oldwordsFile);
		return addFromMultiWordStringsUniqueAndSort(oldWords, newwords);
	}
	public static Vector<String> addFromMultiWordStringsUniqueAndSort(
			Vector<String> oldWords, Vector<String> newWords) {
//		Vector<String> words = StringUtil.stringsToVector(newWords);
		return addUniqueAndSort(oldWords, newWords);
	}

	public static Vector<String> addUniqueAndSort(Vector<String> oldWords,
			Vector<String> newWords) {
		Vector<String> ret = oldWords;
		for (String string : newWords) {
			String noSpecialsLower = StringUtil.removeNonAlphanumericCharacters(
					string).toLowerCase();
			if (!StringUtil.isStringInVector(noSpecialsLower, oldWords)
					&& !StringUtil.isStringInVector(noSpecialsLower, ret)) {
				ret.add(noSpecialsLower);
			}
		}
		Collections.sort(ret);

		return ret;

	}

	public static void main(String[] args) {
		String oldWords = args[0];
		String newWords = args[1];
		String outFile = args[2];
		Vector<String> newList = new Vector<String>();
		try {
			newList = AddWordsToList.addFileWordsToList(oldWords, newWords);
			StringUtil.printOutVector(newList);
			FileUtil.writeFileContent(outFile, newList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
