package com.tlabs.spellcheckerserver.servlet;

import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.felix.util.FileUtil;
import com.felix.util.KeyValues;
import com.felix.util.logging.Log4JLogger;
import com.felix.util.logging.LoggerInterface;
import com.felix.webmaintenance.GlobalConfig;
import com.felix.webmaintenance.MaintainedServer;
import com.felix.webmaintenance.MaintenanceManager;
import com.tlabs.spellcheckerserver.HunspellWrapper;
import com.tlabs.spellcheckerserver.util.Constants;
import com.tlabs.spellcheckerserver.util.FilterLogEntries;
import com.tlabs.spellcheckerserver.util.UserVocabAdd;

public class InitListener implements ServletContextListener, MaintainedServer {
	private ServletContext _servletContext;
	private LoggerInterface _logger = null;
	private HunspellWrapper _hunspell = null;
	private FilterLogEntries _filterLogEntries = null;
	private KeyValues _config = null;

	public InitListener() {
	}

	public void contextInitialized(ServletContextEvent sce) {
		_servletContext = sce.getServletContext();
		init();
	}

	private void init() {
		Locale locale = Locale.GERMAN;
		try {
			// boolean withRss =
			String configFile = _servletContext.getInitParameter("configFile");
			String rootPath = _servletContext.getRealPath("/");
			System.setProperty("rootPath", rootPath);
			String appName = _servletContext.getServletContextName();
			_servletContext.setAttribute("appName", appName);
			_servletContext.setAttribute("version", Constants.Version);
			_servletContext.setAttribute("charEnc", Constants.CHAR_ENC);
			GlobalConfig global = new GlobalConfig(_servletContext, configFile);
			_logger = (LoggerInterface) new Log4JLogger(global.getLogger()
					.getName());
			_servletContext.setAttribute("logger", _logger);
			global.getLogger().debug(
					"Starting " + _servletContext.getServletContextName()
							+ " Server.");
			_config = GlobalConfig.getInstance();
			// _servletContext.setAttribute("logger", logger);
			_servletContext.setAttribute("config", _config);
			_servletContext.setAttribute("locale", locale);

			_hunspell = new HunspellWrapper(_logger, _config);
			_servletContext.setAttribute("hunspell", _hunspell);

			_servletContext.setAttribute("userVocabAdd", new UserVocabAdd(
					_config));
			_filterLogEntries = new FilterLogEntries(_config, _logger);
			_servletContext
					.setAttribute("_filterLogEntries", _filterLogEntries);

			MaintenanceManager maintenanceManger = new MaintenanceManager(
					global.getConfFilePath(), rootPath, _logger);
			maintenanceManger.setPreproFilter(
					global.getPathValue(MaintenanceManager.PREPRO_PATH), null);
			maintenanceManger.parseConfig();
			maintenanceManger.setMaintainedServer(this);
			_servletContext.setAttribute("maintenanceManager",
					maintenanceManger);

			System.out.println("Server "
					+ _servletContext.getServletContextName() + ", version "
					+ Constants.Version + " started");
			System.out.println("with base " + rootPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		_hunspell.destroy();
	}

	@Override
	public void reInitializeServer() {
		init();
	}

	@Override
	public void executeCommand(String command) {
		if (command.compareTo("exec filterLoglines") == 0) {
			try {
				Vector<String> loglines = _filterLogEntries.getLoglines();
				String loglineName = _config.getPathValue("filterLogfileLines");
				loglines.add(0, _filterLogEntries.getReport());
				FileUtil.writeFileContent(loglineName, loglines);
			} catch (Exception e) {
				_logger.error(e.getMessage());
			}
		} else if (command.compareTo("exec xxx") == 0) {

		}
	}

	@Override
	public String showInfo(String infoDescriptor) {
		return "version: " + Constants.Version;
	}

}
