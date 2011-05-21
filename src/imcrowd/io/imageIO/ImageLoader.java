package imcrowd.io.imageIO;

import java.io.File;

import javax.swing.ImageIcon;


public class ImageLoader {
	private static ImageLoader imageLoader = new ImageLoader();
	final int GIF_IMAGE_NUM = 22;				
	final int PNG_IMAGE_NUM = 14;
	final int DANCER_IMAGE_NUM = 7;
	
	ImageIcon[] img;							
	ImageIcon[] pngImg;
	ImageIcon[] dancerImg;
	public static ImageIcon crosshairImg;
	public static ImageIcon selectedImg;
	public static ImageIcon middleFingerImg;
	public static ImageIcon agitatorImg;
	public static ImageIcon chasingImg;
	public static ImageIcon nervousImg;
	public static ImageIcon evadingImg;
	public static ImageIcon deadImg;
	public static ImageIcon	vandalismImg;
	public static ImageIcon scaryImg;
	
	private ImageLoader(){
		img = new ImageIcon[GIF_IMAGE_NUM];
		pngImg = new ImageIcon[PNG_IMAGE_NUM];
		dancerImg = new ImageIcon[DANCER_IMAGE_NUM];
		
		try{
			File source;
			for(int i=0; i<img.length; i++){
				source = new File("res/"+i+".gif");
				img[i] = new ImageIcon(source.toURI().toURL());
				//img[i] = new ImageIcon(ClassLoader.getSystemResource("res/"+i+".gif"));
			}
			
			for(int i=0;i< pngImg.length;i++) {
				source = new File("res/"+i+".png");
				pngImg[i] = new ImageIcon(source.toURI().toURL());
				//pngImg[i] = new ImageIcon(ClassLoader.getSystemResource("res/"+i+".png"));
			}
			
			for(int i=0;i< dancerImg.length;i++) {
				source = new File("res/dancer"+i+".gif");
				dancerImg[i] = new ImageIcon(source.toURI().toURL());
			}
			
			source = new File("res/crosshair1.gif");
			crosshairImg = new ImageIcon(source.toURI().toURL());
			
			source = new File("res/arrw03_33a.gif");
			selectedImg = new ImageIcon(source.toURI().toURL());
			
			source = new File("res/middleFinger2.gif");
			middleFingerImg = new ImageIcon(source.toURI().toURL());
			
			source = new File("res/agitator.gif");
			agitatorImg = new ImageIcon(source.toURI().toURL());
			
			source = new File("res/chasing.gif");
			chasingImg = new ImageIcon(source.toURI().toURL());
			
			source = new File("res/nervous.png");
			nervousImg = new ImageIcon(source.toURI().toURL());
			
			source = new File("res/evading.gif");
			evadingImg = new ImageIcon(source.toURI().toURL());
			
			//source = new File("res/dead.png");
			source = new File("res/rip.gif");
			deadImg = new ImageIcon(source.toURI().toURL());
			
			source = new File("res/vandalism.gif");
			vandalismImg = new ImageIcon(source.toURI().toURL());
			
			source = new File("res/scary.gif");
			scaryImg = new ImageIcon(source.toURI().toURL());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}

		
/*		for IMNET Bundle		
		try {
			URI source;
			
			for(int i=0; i<img.length; i++){
				source = new URI("http://fk.cs.nccu.edu.tw/~l314/res/"+i+".gif");
				img[i] = new ImageIcon(source.toURL());
				//img[i] = new ImageIcon(ClassLoader.getSystemResource("res/"+i+".gif"));
			}
			for(int i=0;i< pngImg.length;i++) {
				source = new URI("http://fk.cs.nccu.edu.tw/~l314/res/"+i+".png");
				pngImg[i] = new ImageIcon(source.toURL());
				//pngImg[i] = new ImageIcon(ClassLoader.getSystemResource("res/"+i+".png"));
			}
			
			source = new URI("http://fk.cs.nccu.edu.tw/~l314/res/crosshair1.gif");
			crosshairImg = new ImageIcon(source.toURL());
			
			source = new URI("http://fk.cs.nccu.edu.tw/~l314/res/arrw03_33a.gif");
			selectedImg = new ImageIcon(source.toURL());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
*/
		
		
	}
	
	public static ImageLoader getInstance(){
		return imageLoader;
	}
	
	public ImageIcon getGifImg(int i){	
		return img[i];
	}
	
	public ImageIcon getPngImg(int i) {
		return pngImg[i];
	}
	
	public ImageIcon getDancerImg(int i) {
		return dancerImg[i];
	}
	
	/* 
	public ImageIcon getCrosshairImg(){
		return crosshairImg;
	}
	
	public ImageIcon getSelectedImg(){
		return selectedImg;
	}
	*/
	
}
