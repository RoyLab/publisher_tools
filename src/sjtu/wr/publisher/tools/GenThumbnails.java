package sjtu.wr.publisher.tools;
import java.io.File;
import java.io.IOException;

import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

class MinRename extends Rename{

	public String apply(String name, ThumbnailParameter arg1) {
		int ptr = name.lastIndexOf('.');
		return "min." + name.substring(0, ptr+1) + "GIF";
	}
	
}

public class GenThumbnails {

    public static final int width = 400;							//设置缩略图宽和高
    public static final int height = 350;
    
    public static final String READABLE_FORMAT = "jpg|jpeg|cgm|png|gif|bmp";
    
    public static void converter(String sourcePath, String destPath, String format) throws IOException{
    	//形成固定大小的缩略图
    	Thumbnails.of(sourcePath)   
        .size(width,height)   
        .keepAspectRatio(false)   
        .toFile(destPath);
    }
    
    public static void genThumbnails(File files[], File output) throws IOException{
    	Thumbnails.of(files)
        .size(640, 480)
        .keepAspectRatio(false)
        .outputFormat("gif")
        .toFiles(output, new MinRename());
    }
}
