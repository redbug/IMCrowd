package imcrowd.io;
import java.io.File;

import javax.swing.filechooser.FileFilter;


public class PropertiesFileFilter extends FileFilter{
	String ext;
	
	public PropertiesFileFilter(String ext){
		this.ext = ext;
	}
	
	public boolean accept(File file){
		if(file.isDirectory())
			return true;
		
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		
		if(index > 0 && index < fileName.length()-1){ 
			String extension = fileName.substring(index+1).toLowerCase();
			if(extension.equals(ext))
				return true;
		}
		return false;
	}
	
	public String getDescription(){
		if(ext.equals("sim"))
			return "IMCrowd Configuration(*.sim)";
		else if (ext.equals("plt"))
			return "GNUplot(*.plt)";
		return "";
	}
}
