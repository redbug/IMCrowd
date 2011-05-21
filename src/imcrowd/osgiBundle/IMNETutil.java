package imcrowd.osgiBundle;

import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/*  for IMNET Bundle
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import imlab.gui.shell.IMGuiManager;
*/


public class IMNETutil {
	final int convertUnit = 10;
	
	public IMNETutil(){
	}
	
/*	private void initialWalk(float x, float y){
		walk = "<AnimItem DEF=\"TestHigh\" playMode=\"seq\">\n";
		walk += "<AnimHigh dur=\"10000\" DEF=\"High2\">Walk to ("+x+","+y+")</AnimHigh>\n";
		 the syntax of standing 
//		walk += "<AnimTransition init=\"true\">\n";
//	    walk += "<AnimImport src=\"Stand\"/>\n";
//      walk += "</AnimTransition>\n";
		walk += "</AnimItem>\n";
	}
	*/

/*	
	public String login(){
		String owner = IMGuiManager.getPluginContext().getClient().getOwnerId();	
		IMGuiManager.getPluginContext().getClient().login(owner,"test1234","./model/avatar/man_2/man_2.wrl","world_1");
		String user = IMGuiManager.getPluginContext().getClient().getUserId();
		IMGuiManager.setShellTitle(user);
		return user;
	}
	
	public boolean isActionDone(Map<Integer, Point2D> currentFrame){
		Integer agentId;
		String avatarId;
		Point2D pos;
		float[] modelPos_IMNET = new float[3];
		
		for(Iterator<Integer> it = currentFrame.keySet().iterator(); it.hasNext();){
			agentId = ((Integer)it.next());
			avatarId = "user1_"+agentId;
			IMGuiManager.getPluginContext().getBrowser().getModel(avatarId)
						.getNode("humanoidRoot").getGlobalTranslation(modelPos_IMNET);
			pos = currentFrame.get(agentId);
			
			if(pos.getX() - modelPos_IMNET[0] * convertUnit > 1 ||
			   pos.getY() - modelPos_IMNET[2] * convertUnit > 1){
						  return false;
			}
		}
//		System.out.println("IMNET: "+ p[0]+", "+p[1]+", "+p[2]);
//		System.out.println("SIM2: "+ pos);
		return true;
	}
	
	public void teleportTo(String id, float x, float y){
		IMGuiManager.getPluginContext().getBrowser().getModel(id).getNode("humanoidRoot")
				.translateTo(new float[]{x/convertUnit,0f,y/convertUnit});
		//IMGuiManager.getPluginContext().getBrowser().getModel(id).getNode("humanoidRoot").getGlobalTranslation(returnValue);
	}
	
	public void walkTo(Map<Integer, Point2D> frame){
		Integer agentId;
		String avatarId;
		Point2D pos;
		Element xaml;
		Element model;
		Element walkto;
		float x,y;
		
		xaml = new Element("AnimItem");
		for(Iterator it = frame.keySet().iterator(); it.hasNext();)
		{	
			agentId = ((Integer)it.next());
			avatarId = "user1_"+agentId;
			pos = frame.get(agentId);
			x = (float)pos.getX()/convertUnit;
			y = (float)pos.getY()/convertUnit;
			
			model = new Element("AnimItem").setAttribute("model", avatarId).setAttribute("playMode", "seq");
			walkto = new Element("AnimHigh").setText("Walk to ("+ x +","+ y +")");
			model.addContent(walkto);
			xaml.addContent(model);
		}
//		XMLOutputter op = new XMLOutputter(Format.getPrettyFormat());
//		try {
//			op.output(xaml, System.out);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		IMGuiManager.getPluginContext().getClient().sendNet(xaml);
	}
*/	
}
