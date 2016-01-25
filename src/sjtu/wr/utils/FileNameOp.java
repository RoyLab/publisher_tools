package sjtu.wr.utils;

public class FileNameOp {
	
	public static String makeDirName(String path)
	{
		char last = path.charAt(path.length()-1);
		if (last == '/' || last == '\\')
			return new String(path);
		else
			return path + '/';
	}
	
//	public static extractFileName(String fullPath);
//	
//	
//	public static boolean containString();
}
