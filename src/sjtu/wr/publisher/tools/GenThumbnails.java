package sjtu.wr.publisher.tools;
import java.io.IOException;
import net.coobird.thumbnailator.Thumbnails;

public class GenThumbnails {

    public static final int width = 400;							//设置缩略图宽和高
    public static final int height = 350;
    
    public static void converter(String sourcePath, String destPath, String format) throws IOException{
    	//形成固定大小的缩略图
    	
    	
    	Thumbnails.of(sourcePath)   
        .size(width,height)   
        .keepAspectRatio(false)   
        .toFile(destPath);
    }

    
    public static void main1(String[] args) {
        try {
        	String sourcePath = "./res/png.png";
        	String destPath = "./res/png.gif";
        	
        		
            converter(sourcePath, 	//源文件地址
            		          destPath,     //生成缩略图存放地址
            		          "png"			//源图片格式，缩略图统一转换成400*350的gif
            		          );
            
        	Thumbnails.of(sourcePath)   
            .size(width,height)   
            .keepAspectRatio(false)   
            .toFile(destPath);
            
            
            System.out.println("done!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
