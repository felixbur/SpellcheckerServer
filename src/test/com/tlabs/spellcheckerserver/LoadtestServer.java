package test.com.tlabs.spellcheckerserver;

import java.io.File;
import java.net.URLEncoder;
import java.util.Vector;

import com.felix.util.FileUtil;
import com.felix.util.HTTPConnection;
import com.felix.util.logging.SystemOutLogger;

public class LoadtestServer {
	public static final String SERVER = "https://exampledev.comx.labs-exit.de/SpellcheckerServer/CheckSpelling?dicDE=de_DE&dicCX=CX&userID=webtest&lang=&userId=webtest&sendQuery=search&q=";
	public static final File TESTFILE = new File(
			"WebContent/res/loadtestdata.txt");

	public LoadtestServer() {
	try {
//		  System.setProperty("http.proxyHost", "212.201.109.4");
//	        System.setProperty("http.proxyPort", "8080");
			  System.setProperty("https.proxyHost", "212.201.104.11");
		        System.setProperty("https.proxyPort", "8080");
			Vector<String> data = FileUtil.getFileLines(TESTFILE);
			int i=0;
			while (true) {
				for (String string : data) {
//					String test = URLEncoder.encode(string);
					String test = URLEncoder.encode("tets");
					String serverutl = SERVER + test;
					String result = HTTPConnection.getStringFromURL(serverutl,
							2000);
					System.out.println(i++ +" "+result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

	public static void main(String[] args) {
		new LoadtestServer();

	}

}
