package com.tlabs.spellcheckerserver.util;

import java.util.Iterator;
import java.util.Vector;

import org.apache.jasper.tagplugins.jstl.core.ForEach;

import com.felix.util.FileUtil;
import com.felix.util.KeyValues;
import com.felix.util.StringUtil;
import com.felix.util.Util;
import com.felix.util.logging.LoggerInterface;

public class FilterLogEntries {
	KeyValues _config;
	LoggerInterface _logger;

	public FilterLogEntries(KeyValues config, LoggerInterface logger) {
		_config = config;
		_logger = logger;
	}

	public String getReport() {
		String ret = "";
		try {
			Vector<LogDay> logdays = new Vector<FilterLogEntries.LogDay>();
			Vector<String> allLogs = getAllLogLines();
			Vector<String> filteredLogs = filterLoglines(allLogs);
			String actDate = "";
			boolean first = true;
			LogDay actDay = new LogDay();
			Vector<String> tmpdayvec = new Vector<String>();
			for (Iterator iterator = filteredLogs.iterator(); iterator
					.hasNext();) {
				String string = (String) iterator.next();
				String[] strings = StringUtil.stringToArray(string);
				String date = strings[0];
				if (first) {
					first = false;
					actDate = date;
					actDay.day = date;
				}
				if (date.compareTo(actDate) == 0) {
					tmpdayvec.add(string);
				} else {
					logdays.add(actDay.close(tmpdayvec));
					tmpdayvec.removeAllElements();
					actDate = date;
					actDay.day = date;
				}
			}
			logdays.add(actDay.close(tmpdayvec));

			for (LogDay logday : logdays) {
				ret += logday.report() + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error(e.getMessage());
		}

		return ret;
	}

	private Vector<String> getAllLogLines() throws Exception {
		String logFilePath = _config.getPathValue("logFile");
		return FileUtil.getFileLines(logFilePath);

	}

	public Vector<String> getLoglines() {
		try {
			Vector<String> loglines = getAllLogLines();
			return getLogLinesFromVector(loglines);
		} catch (Exception e) {
			Util.errorOut(e, _logger);
		}
		return null;
	}

	private Vector<String> filterLoglines(Vector<String> loglines) {
		Vector<String> retVec = new Vector<String>();
		for (String string : loglines) {
			if (string.indexOf("query: ") > 0) {
				retVec.add(string);
			}
		}
		return retVec;
	}

	private Vector<String> getLogLinesFromVector(Vector<String> loglines)
			throws Exception {
		Vector<String> resultVec = new Vector<String>();
		for (String string : loglines) {
			int questionIndexStart = string.indexOf("question: ");
			int answerIndexStart = string.indexOf(", response: ");
			int answerIndexStop = string.indexOf(", time:");
			if (questionIndexStart > -1
					&& answerIndexStart > questionIndexStart
					&& answerIndexStop > answerIndexStart) {
				String question = string.substring(questionIndexStart + 10,
						answerIndexStart);
				question = StringUtil
						.removeNonAlphanumericCharactersExcludingHyphen(question);
				String answer = string.substring(answerIndexStart + 12,
						answerIndexStop);
				answer = StringUtil.removeNonAlphanumericCharactersExcludingHyphen(answer);
				if (question.compareToIgnoreCase(answer) != 0) {
					String notContained = StringUtil
							.getStringsNotContainedString(question, answer);
					if (!StringUtil.isEmpty(notContained.trim())) {
						String[] strings = StringUtil.stringToArray(string);
						String date = strings[0];
						resultVec.add(date+"; "+question + "; " + answer + "; "
								+ notContained);
					}
				}
			}
		}
		return resultVec;
	}

	private class LogDay {
		String day;
		int lognum = 0;
		int lognum_corrected = 0;
		float percCorrected = 0;
		Vector<String> loglines_all;
		Vector<String> loglines_corrected;

		public LogDay close(Vector<String> logs) throws Exception {
			LogDay retDay = new LogDay();
			loglines_all = logs;
			retDay.lognum = loglines_all.size();
			loglines_corrected = getLogLinesFromVector(loglines_all);
			retDay.lognum_corrected = loglines_corrected.size();
			loglines_all = null;
			loglines_corrected = null;
			retDay.percCorrected = ((float) retDay.lognum_corrected / (float) retDay.lognum)
					* (float) 100;
			retDay.day = day;
			return retDay;
		}

		public String report() {
			return day + " all: " + lognum + " corrected: " + lognum_corrected
					+ " (" + percCorrected + ")";
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
