package imcrowd.io;
import imcrowd.io.imageIO.ImageLoader;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;


public class FileIcon extends FileView {
	ImageLoader imageLoader = ImageLoader.getInstance();
	
	public String getName(File f){
		return null;
	}
	
	public String getDescription(File f){
		return null;
	}
	
	public String getTypeDescription(File f){
		String extension = getExtensionName(f);
		if(extension.equals("sim"))
			return "SIM Configuration";
		else if (extension.equals("plt"))
			return "SIM Statistic";
		return "";
	}
	
	public Icon getIcon(File f){
		String extension = getExtensionName(f);
		if(extension.equals("sim"))
			return imageLoader.getGifImg(17);
		else if (extension.equals("plt")) {
			return imageLoader.getGifImg(16);
		}
		
		return null;
	}
	
	public Boolean isTraversable(File f){
		return null;
	}
	
	private String getExtensionName(File f){
		String extension = "";
		String fileName = f.getName();
		int index = fileName.lastIndexOf(".");
		
		if(index > 0 && index < fileName.length()-1){
			extension = fileName.substring(index+1).toLowerCase();
		}
		return extension;
		
	}
	
}
