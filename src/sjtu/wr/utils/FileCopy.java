package sjtu.wr.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileCopy {
	
	public static void nioTransferCopy(File source, File target) throws IOException {  
	    FileChannel in = null;  
	    FileChannel out = null;  
	    FileInputStream inStream = null;  
	    FileOutputStream outStream = null;  
	    try {  
	        inStream = new FileInputStream(source);  
	        outStream = new FileOutputStream(target);  
	        in = inStream.getChannel();  
	        out = outStream.getChannel();  
	        in.transferTo(0, in.size(), out);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {
	    	inStream.close();
	    	in.close();
	    	outStream.close();
	    	out.close(); 
	    }  
	}
	
	public static void copyFiles(File []files, String dir) throws IOException{
		for (File f: files){
			File target = new File(dir+f.getName());
			nioTransferCopy(f, target);
		}
	}
	
}
