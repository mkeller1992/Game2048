package ch.bfh.game2048.persistence;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Config {

	public final static String PROPERTIES_FILE_NAME = "2048.properties";
	FileBasedConfigurationBuilder<FileBasedConfiguration> builder;
	public Configuration conf;

	private static Config instance = new Config();

	private Config() {

		conf = load(PROPERTIES_FILE_NAME);
	
	}

	public void setProperty(String key, Object attribute) {
		conf.setProperty(key, attribute);
	}

	public String getPropertyAsString(String propertyKey) {
		return conf.getString(propertyKey);		
	}

	public Integer getPropertyAsInt(String propertyKey) {
		return conf.getInt(propertyKey);		
	}

	public ObservableList<String> getPropertyAsObservableList(String keyValue) {
		return FXCollections.observableArrayList(conf.getStringArray(keyValue));
	}

	public Configuration load(String fileName) {
		Parameters params = new Parameters();
		builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
						.setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
						.setBasePath("src/main/resources/config")
						.setFileName(fileName));
		
		System.out.println(builder.getFileHandler().getPath());
		try {
			return builder.getConfiguration();

		} catch (ConfigurationException cex) {

			System.out.println(cex);
			// loading of the configuration file failed
		}
		return null;
	}

	public void write() {
		try {
			builder.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static Config getInstance() {
		if (instance == null) {
			synchronized (Config.class) {
				if (instance == null)
					instance = new Config();
			}
		}
		return instance;
	}

}
