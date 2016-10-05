package com.tlabs.spellcheckerserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.felix.util.Constants;
import com.felix.util.FileUtil;
import com.felix.util.KeyValues;
import com.felix.util.StringUtil;
import com.felix.util.logging.Log4JLogger;
import com.felix.util.logging.LoggerInterface;
import com.felix.webmaintenance.ConfigFile;
import com.tlabs.spellcheckerserver.HunspellWrapper;
import com.tlabs.spellcheckerserver.util.AddWordsToList;
import com.tlabs.spellcheckerserver.util.UserVocabAdd;

public class AddUserVocabServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding(com.felix.util.Constants.CHAR_ENC);
		resp.setCharacterEncoding(Constants.CHAR_ENC);
		String command = req.getParameter("command");
		String file = req.getParameter("file");
		String contents = req.getParameter("content");

		String targetPage = req.getParameter("targetPage");
		String answerString = "";
		KeyValues config = (KeyValues) getServletContext().getAttribute(
				"config");
		HunspellWrapper hunspell = (HunspellWrapper) getServletContext()
				.getAttribute("hunspell");
		LoggerInterface logger = (Log4JLogger) getServletContext()
				.getAttribute("logger");

		String userDicPath = config.getPathValue("userDicPath");
		UserVocabAdd userVocabAdd = (UserVocabAdd) getServletContext()
				.getAttribute("userVocabAdd");
		if (file != null) {
			logger.info("request to add new words: " + contents.replace("\n", ", "));
			answerString = config.getString("msg_AllDone");
			try {
				if (userVocabAdd.getActFile() == null) {
					userVocabAdd.setActFile(file);
				}
				userVocabAdd.getActFile().writeToDisk(contents);
				Vector<String> newWords = StringUtil.stringToVectorNewlineSeparated(contents);
				Vector<String> newList = AddWordsToList.addFileWordsToList(
						userDicPath, newWords);
				newList.remove(0);
				newList.add(0, String.valueOf(newList.size()));
				FileUtil.writeFileContent(userDicPath, newList);
				hunspell.reloadUserDic();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (StringUtil.isFilled(targetPage)) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					targetPage);
			req.setAttribute("result", answerString);
			rd.forward(req, resp);
		} else {
			PrintWriter out = resp.getWriter();
			out.println("no target page");
		}

	}
}
