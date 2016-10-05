package com.tlabs.spellcheckerserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.felix.util.KeyValues;
import com.felix.util.StringUtil;
import com.felix.util.logging.Log4JLogger;
import com.felix.util.logging.LoggerInterface;
import com.tlabs.spellcheckerserver.HunspellWrapper;
import com.tlabs.spellcheckerserver.util.Constants;

public class CheckSpelling extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	

	// to be called e.g.
	// http://localhost:8080/TextParserServer/ParseText?text=punto%20ab%20baujahr%202005%20maximal%2050.000%20kilometer%20mindestens%20500%20euro&formatOutput=true
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		req.setCharacterEncoding(Constants.CHAR_ENC);
		resp.setCharacterEncoding(Constants.CHAR_ENC);
        resp.setContentType("text/html; charset="+Constants.CHAR_ENC);

		String errMsg = "";
		LoggerInterface logger = (Log4JLogger) getServletContext().getAttribute(
				"logger");
		String question = req.getParameter("q");
		String dicDe = "";
		if (req.getParameter("dicDEAT") != null)
			dicDe = req.getParameter("dicDEAT");
		String dicCx = "";
		if (req.getParameter("dicCX") != null)
			dicCx = req.getParameter("dicCX");
		String dicCXDE = "";
		if (req.getParameter("dicCXDE") != null)
			dicCXDE = req.getParameter("dicCXDE");
		String targetPage = req.getParameter("targetPage");
		String userID = req.getParameter("userID");
		// String dictionaries = dicCx+";"+dicDe;
		String dictionaries = "";
		if(StringUtil.isFilled(dicDe) && StringUtil.isFilled(dicCx)) {
			dictionaries = "de_DEAT;CX";
		} else if (StringUtil.isFilled(dicCXDE)) {
			dictionaries = "DE_CX";
		} else if (StringUtil.isFilled(dicDe)) {
			dictionaries = "de_DEAT";
		} else if (StringUtil.isFilled(dicCx)) {
			dictionaries = "CX";
		} 

		logger.debug("q: " + question + ", dics: " + dictionaries);
		KeyValues config = (KeyValues) getServletContext().getAttribute(
				"config");
		long now = 0;
		String retString = "";
		long timeUsed = 0;
		boolean ignoreWhitespace = config.getBool("ignoreWhitespace");
		try { 
			now = System.currentTimeMillis();
			HunspellWrapper hunspell = (HunspellWrapper) getServletContext()
					.getAttribute("hunspell");
			String lastChar = question.substring(question.length()-1, question.length());
			retString = hunspell.getFirstSuggestions(question, dictionaries);
			if(!lastChar.matches("[a-zA-Z0-9]")){
				retString += lastChar;
			}					
			if(ignoreWhitespace) {
				String questionTest = question.replaceAll(" ", "");
				String test = retString.replaceAll(" ", "");
				if(questionTest.compareTo(test)==0) {
					retString=question;
				}
			} else {
				String questionTest = StringUtil.removeNonAlphanumericCharacters(question).toLowerCase();
				String test = StringUtil.removeNonAlphanumericCharacters(retString).toLowerCase();
				if(questionTest.compareTo(test)==0) {
					retString=question;
				}				
			}
			timeUsed = System.currentTimeMillis() - now;
			logger.info("query: " +  req.getQueryString() +", question: "+question+ ", response: " + retString + ", time: "+timeUsed);
		} catch (Exception e) {
			e.printStackTrace();
			retString += e.getMessage();
		}
		if (StringUtil.isFilled(targetPage)) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					targetPage);
			req.setAttribute("result", retString);
			req.setAttribute("query", question);
			req.setAttribute("time", timeUsed);
			rd.forward(req, resp);
		} else {
			PrintWriter out = resp.getWriter();
			out.println(retString);
		}
	}
}
