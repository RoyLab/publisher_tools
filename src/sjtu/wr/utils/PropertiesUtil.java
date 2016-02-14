package sjtu.wr.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	
	public static PropertiesUtil getInstance()
	{
		return instance;
	}
	
	public static void CreateInstance(String path){
		instance = new PropertiesUtil(path);
	}
	
	private PropertiesUtil(String configFile)
	{
		prop = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(new File(configFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
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
