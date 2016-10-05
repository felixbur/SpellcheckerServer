package test.com.tlabs.spellcheckerserver;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.felix.util.FileUtil;
import com.felix.util.HTTPConnection;
import com.felix.util.KeyValue;
import com.felix.util.KeyValues;
import com.felix.util.logging.Log4JLogger;
import com.felix.util.logging.LoggerInterface;
import com.felix.util.logging.SystemOutLogger;
import com.tlabs.spellcheckerserver.HunspellWrapper;

//import junit.swingui.TestRunner;
@RunWith(Parameterized.class)
public class SpellcheckerTest {

	// TestQueries _testqueries = new TestQueries();
	// static Locale _locale = Locale.UK;
	// public static final String SERVER = "http://localhost:8080/SpellcheckerServer/CheckSpelling?dicCXDE=CXDE&userID=webtest&lang=&userId=webtest&sendQuery=search&q=";
//	public static final String SERVER = "http://felixbur.no-ip.org/SpellcheckerServer/CheckSpelling?dicDEAT=de_DEAT&dicCX=CX&userID=webtest&lang=&userId=webtest&sendQuery=search&q=";
	public static final String SERVER = "https://exampledev.comx.labs-exit.de/SpellcheckerServer/CheckSpelling?dicDEAT=de_DEAT&dicCX=CX&userID=webtest&lang=&userId=webtest&sendQuery=search&q=";
	static Locale _locale = Locale.GERMANY;
	public final static String CHAR_ENC = "UTF-8";
	public final static String CONFIG_PATH = "WebContent/WEB-INF/config/config.txt";
	String result_default;
	String result_real;
	final String logConfigFile = "WebContent/WEB-INF/config/logConfig.xml";
	final String keyValueSeparator = "=";
	static File testqueries_file_de = new File("WebContent/res/testqueries_de");
	KeyValue[] _queriesAndAnswers;
	String _query, _expectedResult;
	HunspellWrapper _hunspellWrapper = null;

	public HunspellWrapper getHunspellWrapper() {
		return _hunspellWrapper;
	}

	public SpellcheckerTest(String query, String expectedResult) {
		_query = query;
		_expectedResult = expectedResult;
	}

	@Parameters
	public static Collection<Object[]> data() {
		KeyValues kvs = null;
		try {
			kvs = new KeyValues(testqueries_file_de, "=", "UTF8");
			String[][] queries = new String[kvs.getSize()][2];
			int ind = 0;
			for (KeyValue kv : kvs.getKeyValues()) {
				queries[ind][0] = kv.getKey();
				queries[ind][1] = kv.getValue();
				ind++;
			}

			return Arrays.asList((Object[][]) queries);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Before
	public void setUp() throws Exception {
		initHunspellWrapper();
		  System.setProperty("https.proxyHost", "212.201.104.11");
	       System.setProperty("https.proxyPort", "8080");
			  System.setProperty("http.proxyHost", "212.201.104.11");
		       System.setProperty("http.proxyPort", "8080");


	}

	@Test
	public void testIt() {
		String test = URLEncoder.encode(_query);
		String serverutl = SERVER	+ test;
		String result = HTTPConnection.getStringFromURL(serverutl,
				new SystemOutLogger(), 2000);
		String log = "result; " + _query + "; " + _expectedResult + "; "
				+ result;
		try {
			if (_expectedResult.toLowerCase().compareTo(
					result.trim().toLowerCase()) != 0)
				FileUtil.appendFileContent(new File("myOut2.txt"), log, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(_expectedResult.toLowerCase(), result.trim().toLowerCase());
	}

	private void initHunspellWrapper() {
		try {
			LoggerInterface _logger = new Log4JLogger("SpellcheckerTest",
					logConfigFile);
			KeyValues config = new KeyValues(CONFIG_PATH);
			config.setPathBase("./WebContent");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Testing a single string or a list of string given as file lines
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final String TESTSENTENCE_DE = "khlschrank";
		SpellcheckerTest awt = new SpellcheckerTest(null, null);
		awt.initHunspellWrapper();
		if (args.length == 1) {
			try {
				Vector<String> queries = FileUtil
						.getFileLines(args[0], "UTF-8");
				for (String line : queries) {
					line = line.replace("?", "");
					// line = line.toLowerCase();
					// String result = awt.getHunspellWrapper()
					// .getFirstSuggestion(line);

					String serverutl = SERVER+ line;
					String result = HTTPConnection.getStringFromURL(serverutl,
							new SystemOutLogger(), 2000);
					System.out.println(line + ";" + result);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("searching...");
			String result = awt.getHunspellWrapper().getFirstSuggestion(
					TESTSENTENCE_DE);
			System.out.println(result);
		}
	}
}
