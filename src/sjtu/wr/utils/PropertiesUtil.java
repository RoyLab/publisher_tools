package sjtu.wr.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	
	public static PropertiesUtil getInstance()
	{
		if (instance == null)
			instance = new PropertiesUtil("/sjtu/wr/publisher/config.properties");
		return instance;
	}
	
	private PropertiesUtil(String configFile)
	{
		prop = new Properties();
		InputStream in= this.getClass().getResourceAsStream(configFile);
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getValue(String key){
		
		return (String)prop.get(key);
	}
	
	private Properties prop = null;
	private static PropertiesUtil instance = null;
}
