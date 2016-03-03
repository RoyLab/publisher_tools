package sjtu.wr.publisher.tools;

public class Main {
	
	public static final String DEFAULT_OUTPUTDIR = "./build";
	
	public static String inputDirName = null;
	public static String outputDirName = null;
	public static String projectName = null;
	public static String resDirName = null;

	public static void main(String[] args) {
		
		int end = args.length;
		for (int ptr = 0; ptr < end; ptr++)
		{
			switch(args[ptr])
			{
			case "-i":
				inputDirName = args[++ptr];
				break;
			case "-o":
				outputDirName = args[++ptr];
				break;
			case "-n":
				projectName = args[++ptr];
				break;
			case "-r":
				resDirName = args[++ptr];
				break;	
			default:
				System.err.println("unexpected parameter: " + args[ptr]);
				return;
			}
		}
		
		if (inputDirName == null)
		{
			System.err.println("Source files not specified, use -i to set.");
			return;
		}
		
		if (outputDirName == null)
		{
			outputDirName = DEFAULT_OUTPUTDIR;
		}
		
		if (projectName == null)
		{
			System.err.println("Project name not specified, use -n to set.");
			return;
		}
		
		if (resDirName == null)
		{
			resDirName = "./resources/";
		}
		
		System.out.println("源文件目录: " + inputDirName);
		System.out.println("输出目录: " + outputDirName);
		System.out.println("项目名称: " + projectName);
		
		TaskManager tm = new TaskManager();
		try {
			tm.operateTask(inputDirName, outputDirName, projectName, resDirName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
