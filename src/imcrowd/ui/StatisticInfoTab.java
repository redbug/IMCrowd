package imcrowd.ui;

import imcrowd.engine.Engine;
import imcrowd.io.report.ConfigurationReport;
import imcrowd.io.textIO.ConfigurationIOHandler;
import imcrowd.patterns.Colleague;
import imcrowd.patterns.Mediator;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StatisticInfoTab extends JScrollPane implements ActionListener, Colleague {
	private static StatisticInfoTab singleton = new StatisticInfoTab();
	
	private Mediator mediator;
	private Engine engine;
	
	JTextArea textArea;
	JButton	initialConfigButton;
	JButton currentConfigButton;
	JButton	currentSituationButton;
	
	private StatisticInfoTab(){
		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", Font.PLAIN, 12));
		
		initialConfigButton = new JButton("Initial Configuration");
		initialConfigButton.addActionListener(this);
		
		currentConfigButton = new JButton("Current Configuration");
		currentConfigButton.addActionListener(this);
		
		currentSituationButton = new JButton("Current Situation");
		currentSituationButton.addActionListener(this);
		
		
		
		createPanel();
	}
	
	public static StatisticInfoTab getInstance(){
		return singleton;
	}
	
	  public void setMediator(Mediator mediator) {
			this.mediator = mediator;
			engine = (Engine)mediator;
	  }
	  
	  public void setManagerMethod() {}
	
	public void createPanel(){
		JPanel outterPanel = new JPanel();
		outterPanel.setLayout(new BoxLayout(outterPanel,BoxLayout.Y_AXIS));
		outterPanel.add(Box.createVerticalStrut(10));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel,BoxLayout.X_AXIS));
		
		innerPanel.add(initialConfigButton);
		innerPanel.add(currentConfigButton);
		innerPanel.add(currentSituationButton);
		
		outterPanel.add(innerPanel);
		outterPanel.add(textArea);
		
		this.getViewport().add(outterPanel);
		
		//JPanel innerPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == initialConfigButton){
			engine.pause();
			ConfigurationReport configReport = ConfigurationIOHandler.getInstance().getConfigReport();
			
			if(configReport == null){
				return;
			}
			
			StringBuffer text = new StringBuffer();
			String separator = "=================================\n";
			
			text.append(configReport.report_AlphaBeta());
			text.append(separator);
			text.append(configReport.report_PartyInformation());
			text.append(separator);
			text.append(configReport.report_CrowdStructure());
			text.append(separator);
			text.append(configReport.report_Attribute());
			text.append(separator);
			
			textArea.setText(text.toString());
			
		}
		else if(e.getSource() == currentConfigButton){
			engine.pause();
			
			int alpha = ExperimentParameterTab.getInstance().getAlpha();
			int beta = ExperimentParameterTab.getInstance().getBeta();
			ConfigurationReport configReport = new ConfigurationReport(alpha, beta);
			
			StringBuffer text = new StringBuffer();
			String separator = "=================================\n";
			
			text.append(configReport.report_AlphaBeta());
			text.append(separator);
			text.append(configReport.report_PartyInformation());
			text.append(separator);
			text.append(configReport.report_CrowdStructure());
			text.append(separator);
			text.append(configReport.report_Attribute());
			text.append(separator);
			
			textArea.setText(text.toString());
			
		}
		else if(e.getSource() == currentSituationButton){
			engine.pause();
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			
			StringBuffer text = new StringBuffer();
			String separator = "=================================\n";
			
			text.append(engine.getAgentManager().report_State(true));
			text.append(separator);
			text.append(engine.getAgentManager().report_Riot(true));
			text.append(separator);
			text.append(engine.getAgentManager().report_PartyInfo(true));
			text.append(separator);
			
			
			textArea.setText(text.toString());
		}
	}	
	
}
