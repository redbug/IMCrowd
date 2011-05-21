package imcrowd.ui;

import net.infonode.docking.*;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.*;
import net.infonode.docking.util.*;

import javax.swing.*;


import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class DockingWindows {
  private static DockingWindows dockingWindows = new DockingWindows();
  private static final int ICON_SIZE = 8;
  
  private RootWindow rootWindow;
  private View[] views = new View[7];			   //An array of the static views
  private ViewMap viewMap = new ViewMap();		   //Contains all the static views
  private JMenuItem[] viewItems = new JMenuItem[views.length];	//The view menu items

  private Map<Integer,View> dynamicViews = new HashMap<Integer,View>();	   //Contains the dynamic views that has been added to the root window

  private DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();	//The currently applied docking windows theme
  
  

  /* In this properties object the modified property values for close buttons etc. are stored.
   *  This object is cleared when the theme is changed.											*/
  private RootWindowProperties properties = new RootWindowProperties();
	  
  private JFrame frame = new JFrame("IMCrowd");

  /* Custom view icon. */
  private static final Icon VIEW_ICON = new Icon() {
    public int getIconHeight() {
      return ICON_SIZE;
    }

    public int getIconWidth() {
      return ICON_SIZE;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      Color oldColor = g.getColor();

      g.setColor(new Color(70, 70, 70));
      g.fillRect(x, y, ICON_SIZE, ICON_SIZE);

      g.setColor(new Color(100, 230, 100));
      g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);

      g.setColor(oldColor);
    }
  };

  private TabWindow tabWindow1;
  private TabWindow tabWindow2;
  
  
  /* A dynamically created view containing an id. */
  private static class DynamicView extends View {
    private int id;

    DynamicView(String title, Icon icon, Component component, int id) {
      super(title, icon, component);
      this.id = id;
    }

    public int getId() {
      return id;
    }
  }
  
  
  private DockingWindows() {
	createRootWindow();
    setDefaultLayout();
    showFrame();
  }

  /* my */
  public static DockingWindows getInstance(){
	  return dockingWindows;
  }
  
  public TabWindow getTabWindow1(){
	  return tabWindow1;
  }

  
  /* Sets the default window layout. */
  private void setDefaultLayout() {
	  
	View[] mainView, experimentView;
	
	mainView = new View[5];
	experimentView = new View[2];
	
	for(int i=0; i<views.length; i++){
		if(i < 5){
			mainView[i] = views[i];
		}	
		else{
			experimentView[i-5] = views[i];
		}
	}
	  
    tabWindow1 = new TabWindow(mainView);
    tabWindow2 = new TabWindow(experimentView);
 
    rootWindow.setWindow(new SplitWindow(true,				//divide into right/left parts.
            0.7f,											//the size ratio of the left part. 
            //left part of window.
            //disable the log window.
/*            new SplitWindow(false,							//divide into top/bottom parts.
                            0.8f,							//the size ratio of the top part.
                            views[0],						//left top window.
                            views[1]),						//left bottom window.
*/
            views[0],
            //right part of window.                
            new SplitWindow(false,							//divide into top/bottom parts.
            		        0.6f,							//the size ratio of the top part.
            		        tabWindow1,						//right top window.
            		        tabWindow2)));					//right bottom window.  
    
    tabWindow1.setSelectedTab(0);
    tabWindow2.setSelectedTab(0);
  }

  
  /* Initializes the frame number and shows it on the canvas. */
  private void showFrame() {
    frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
    frame.setSize(900, 700);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
  
  
  private static JComponent createViewComponent(String text) {
    StringBuffer sb = new StringBuffer();

    for (int j = 0; j < 100; j++)
      sb.append(text + ". This is line " + j + "\n");

    return new JScrollPane(new JTextArea(sb.toString()));
  }
  

  
  /* Returns a dynamic view with specified id, reusing an existing view if possible. */
  private View getDynamicView(int id) {
    View view = dynamicViews.get(new Integer(id));

    if (view == null)
      view = new DynamicView("Dynamic View " + id, VIEW_ICON, createViewComponent("Dynamic View " + id), id);

    return view;
  } 
  
  
  /* Creates the root window and the views. */
  private void createRootWindow() {
    // Create the views
	String[] name = {"Canvas", "Agent", "Goal", "Obstacle", "Global", "Experiment Parameters", "Statistic Information"};
    for (int i = 0; i < views.length; i++) {
      //MainView
      if(i == 0){
    	  views[i] = new View(name[i], VIEW_ICON, MainView.getInstance());
          viewMap.addView(i, views[i]);
      }
      //Agent Tab
      else if(i == 1){
    	  views[i] = new View(name[i], VIEW_ICON, AgentTab.getInstance());
          viewMap.addView(i, views[i]);
      }
      //Goal Tab
      else if(i == 2){
    	  views[i] = new View(name[i], VIEW_ICON, GoalTab.getInstance());
          viewMap.addView(i, views[i]);
      }
      //Obstacle Tab
      else if(i == 3){
    	  views[i] = new View(name[i], VIEW_ICON, ObstacleTab.getInstance());
          viewMap.addView(i, views[i]);
      }
      //Global Tab
      else if(i == 4){
    	  views[i] = new View(name[i], VIEW_ICON, GlobalTab.getInstance());
          viewMap.addView(i, views[i]);
      }
      //Experiment Parameter Tab
      else if(i == 5){
    	  views[i] = new View(name[i], VIEW_ICON, ExperimentParameterTab.getInstance());
    	  viewMap.addView(i, views[i]);
      }
      //Statistic Information Tab
      else{
    	  views[i] = new View(name[i], VIEW_ICON, StatisticInfoTab.getInstance());
    	  viewMap.addView(i, views[i]);
      }
    }


    // The mixed view map makes it easy to mix static and dynamic views inside the same root window
    MixedViewHandler handler = new MixedViewHandler(viewMap, new ViewSerializer() {
      public void writeView(View view, ObjectOutputStream out) throws IOException {
        out.writeInt(((DynamicView) view).getId());
      }

      public View readView(ObjectInputStream in) throws IOException {
        return getDynamicView(in.readInt());
      }
    });
    
    //static view, dynamic view, show popupmenu
    rootWindow = DockingUtil.createRootWindow(viewMap, handler, true);

    // Set gradient theme. The theme properties object is the super object of our properties object, which
    // means our property value settings will override the theme values
    properties.addSuperObject(currentTheme.getRootWindowProperties());

    // Our properties object is the super object of the root window properties object, so all property values of the
    // theme and in our property object will be used by the root window
    rootWindow.getRootWindowProperties().addSuperObject(properties);

    
    rootWindow.addListener(new DockingWindowAdapter() {
      public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow) {
        updateViews(addedWindow, true);
      }

      public void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow) {
        updateViews(removedWindow, false);
      }

      public void windowClosing(DockingWindow window) throws OperationAbortedException {
        // Confirm close operation
        if (JOptionPane.showConfirmDialog(frame, "Really close window '" + window + "'?") != JOptionPane.YES_OPTION)
          throw new OperationAbortedException("Window close was aborted!");
      }

      public void windowDocking(DockingWindow window) throws OperationAbortedException {
        // Confirm dock operation
        if (JOptionPane.showConfirmDialog(frame, "Really dock window '" + window + "'?") != JOptionPane.YES_OPTION)
          throw new OperationAbortedException("Window dock was aborted!");
      }

      public void windowUndocking(DockingWindow window) throws OperationAbortedException {
        // Confirm undock operation 
        if (JOptionPane.showConfirmDialog(frame, "Really undock window '" + window + "'?") != JOptionPane.YES_OPTION)
          throw new OperationAbortedException("Window undock was aborted!");
      }

    });

  }

  
  /* Update view menu items and dynamic view map. */
  private void updateViews(DockingWindow window, boolean added) {
    if (window instanceof View) {
      if (window instanceof DynamicView) {
        if (added)
          dynamicViews.put(new Integer(((DynamicView) window).getId()), (View)window);
        else
          dynamicViews.remove(new Integer(((DynamicView) window).getId()));
      }
      else {
        for (int i = 0; i < views.length; i++)
          if (views[i] == window && viewItems[i] != null)
            viewItems[i].setEnabled(!added);
      }
    }
    else {
      for (int i = 0; i < window.getChildWindowCount(); i++)
        updateViews(window.getChildWindow(i), added);
    }
  }
}