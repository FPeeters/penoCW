package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;




import interfaces.AutopilotConfig;
import interfaces.AutopilotOutputs;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JSlider;

import utils.Constants;

public class AutopilotGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final int imgWidth, imgHeight;
	
	private JLabel lblImage;
	private SliderHandler lwSlider, horStabSlider, rwSlider, verStabSlider, thrustSlider,
						  lbSlider, fbSlider, rbSlider;
	

	/**
	 * test the gui
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AutopilotGUI frame = new AutopilotGUI(new AutopilotConfig() {
			            public float getGravity() {return 0f;}
			            public float getWingX() {return 0f;}
			            public float getTailSize() {return 0f;}
			            public float getEngineMass() {return 0f;}
			            public float getWingMass() {return 0f;}
			            public float getTailMass() {return 0f;}
			            public float getMaxThrust() {return 500f;}
			            public float getMaxAOA() {return (float) Math.toRadians(30);}
			            public float getWingLiftSlope() {return 0f;}
			            public float getHorStabLiftSlope() {return 0f;}
			            public float getVerStabLiftSlope() {return 0f;}
			            public float getHorizontalAngleOfView() {return (float) Math.toRadians(120f);}
			            public float getVerticalAngleOfView() {return (float) Math.toRadians(120f);}
			            public int getNbColumns() {return 200;}
			            public int getNbRows() {return 200;}
						public String getDroneID() {return "ID";}
						public float getWheelY() {return 0f;}
						public float getFrontWheelZ() {return 0f;}
						public float getRearWheelZ() {return 0f;}
						public float getRearWheelX() {return 0f;}
						public float getTyreSlope() {return 0f;}
						public float getDampSlope() {return 0f;}
						public float getTyreRadius() {return 0f;}
						public float getRMax() {return 500f;}
						public float getFcMax() {return 0f;}});
					
					BufferedImage img = ImageIO.read(new File("ss.png"));
					frame.lblImage.setIcon(new ImageIcon(GuiUtils.addCrossHair(img, 100, 100, 13133055)));
					frame.showGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public AutopilotGUI(AutopilotConfig config) {		
		this.imgWidth = config.getNbColumns();
		this.imgHeight = config.getNbRows();
		float maxThrust = config.getMaxThrust();
		int maxAOA = (int) Math.toDegrees(config.getMaxAOA());
		float maxBrake = config.getRMax();
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, Constants.AUTOPILOT_GUI_WIDTH, Constants.AUTOPILOT_GUI_HEIGHT);
		setTitle("Autopilot");
		
		
		JPanel topContentPanel = new JPanel();
		topContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		topContentPanel.setLayout(new BorderLayout(0, 0));
		setContentPane(topContentPanel);
		
		JLabel lblAutopilotGui = new JLabel("Autopilot GUI: " + config.getDroneID());
		lblAutopilotGui.setHorizontalAlignment(SwingConstants.CENTER);
		topContentPanel.add(lblAutopilotGui, BorderLayout.NORTH);
		
		JPanel contentPanel = new JPanel();
		topContentPanel.add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridBagLayout());
		
		JPanel leftPanel = new JPanel();
		contentPanel.add(leftPanel, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));		
		leftPanel.setLayout(new GridBagLayout());
		
		lblImage = new JLabel("");
		GridBagConstraints gbc_img = GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5));
		gbc_img.gridwidth = 3;
		leftPanel.add(lblImage, gbc_img);
		lblImage.setIcon(new ImageIcon());
		lblImage.setPreferredSize(new Dimension(imgWidth, imgHeight));
		
		JPanel brakePanel = new JPanel();
		leftPanel.add(brakePanel, GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER));
		brakePanel.setLayout(new GridBagLayout());
		
		Hashtable<Integer, JLabel> brakeTable = new Hashtable<>();
		JLabel zeroLbl = new JLabel("0");
		zeroLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
		brakeTable.put(0, zeroLbl);
		JLabel halfLbl = new JLabel("" + (int) (maxBrake/2f));
		halfLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
		brakeTable.put((int) (maxBrake/2f), halfLbl);
		JLabel maxLbl = new JLabel("" + (int) (maxBrake));
		maxLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
		brakeTable.put((int) (maxBrake), maxLbl);
		
		lbSlider = buildSlider("L brake", SwingConstants.VERTICAL, 0, 0, (int) (maxBrake), 
					(int) (maxBrake/4f), (int) (maxBrake/8f), brakeTable, false);
		lbSlider.slider.setPreferredSize(new Dimension(50, 100));
		brakePanel.add(lbSlider.panel, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(0, 20, 0, 5)));		
		
		fbSlider = buildSlider("F brake", SwingConstants.VERTICAL, 0, 0, (int) (maxBrake), 
				(int) (maxBrake/4f), (int) (maxBrake/8f), brakeTable, false);
		fbSlider.slider.setPreferredSize(new Dimension(50, 100));
		brakePanel.add(fbSlider.panel, GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));
		
		rbSlider = buildSlider("R brake", SwingConstants.VERTICAL, 0, 0, (int) (maxBrake), 
				(int) (maxBrake/4f), (int) (maxBrake/8f), brakeTable, false);
		rbSlider.slider.setPreferredSize(new Dimension(50, 100));
		brakePanel.add(rbSlider.panel, GuiUtils.buildGBC(2, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));
		
		
		JPanel rightPanel = new JPanel();
		contentPanel.add(rightPanel, GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER));
		rightPanel.setLayout(new GridBagLayout());
				
		JPanel horSliderPanel = new JPanel();
		rightPanel.add(horSliderPanel, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));
		GridBagLayout gbl_horSliderPanel = new GridBagLayout();
		gbl_horSliderPanel.columnWidths = new int[] {60, 60, 60};
		horSliderPanel.setLayout(gbl_horSliderPanel);
		
		
		Hashtable<Integer, JLabel> lblTable = new Hashtable<>();
		for (int i=0; i<=10+maxAOA; i+= (maxAOA + 10)/3) {
			JLabel lbl = new JLabel("" + i);
			lbl.setFont(lbl.getFont().deriveFont(9.0f));
			lblTable.put(i, lbl);
			if (i != 0) {
				JLabel lbl2 = new JLabel("" + (-i));
				lbl2.setFont(lbl.getFont().deriveFont(9.0f));
				lblTable.put(-i, lbl2);
			}
		}
				
		lwSlider = buildSlider("R wing", SwingConstants.VERTICAL, -10 - maxAOA, 0, 10 + maxAOA, 30, 10, lblTable, false);
		horSliderPanel.add(lwSlider.panel, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));
		
		
		horStabSlider = buildSlider("H stab", SwingConstants.VERTICAL, -10 - maxAOA, 0, 10 + maxAOA, 30, 10, lblTable, false);
		horSliderPanel.add(horStabSlider.panel, GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));
		
		
		rwSlider = buildSlider("L wing", SwingConstants.VERTICAL, -10 - maxAOA, 0, 10 + maxAOA, 30, 10, lblTable, false);
		horSliderPanel.add(rwSlider.panel, GuiUtils.buildGBC(2, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));
		

		verStabSlider = buildSlider("V stab", SwingConstants.HORIZONTAL, -10 - maxAOA, 0, 10 + maxAOA, 30, 10, lblTable, false); 
		rightPanel.add(verStabSlider.panel, GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER, new Insets(5, 0, 5, 5)));
		
		
		Hashtable<Integer, JLabel> thrustTable = new Hashtable<>();
		thrustTable.put(0, zeroLbl);
		thrustTable.put((int)(maxThrust/2f), new JLabel(""+(int)(maxThrust/2f)));
		thrustTable.put((int)(maxThrust), new JLabel(""+(int)(maxThrust)));
		
		thrustSlider = buildSlider("Thrust", SwingConstants.VERTICAL, 0, 0, (int)maxThrust, 
				(int)(maxThrust/10f), (int)(maxThrust/20f), thrustTable, true);
		thrustSlider.slider.setPreferredSize(new Dimension(57, 270));
		GridBagConstraints gbc_thrustSlider = GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5));
		gbc_thrustSlider.gridheight = 2;
		rightPanel.add(thrustSlider.panel, gbc_thrustSlider);
	}
	
	
	public void showGUI() {
		pack();
		setVisible(true);
	}
	
	public void updateImage(byte[] image, int x, int y) {
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_3BYTE_BGR);
		img.getRaster().setDataElements(0, 0, imgWidth, imgHeight, image);
		lblImage.setIcon(new ImageIcon(GuiUtils.addCrossHair(img, x, y, 255)));
	}
	
	public void updateImage(byte[] image) {
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_3BYTE_BGR);
		img.getRaster().setDataElements(0, 0, imgWidth, imgHeight, image);
		lblImage.setIcon(new ImageIcon(img));
	}
	
	
	public void updateOutputs(AutopilotOutputs output) {
		lwSlider.setValue((int) Math.toDegrees(output.getLeftWingInclination()));
		rwSlider.setValue((int) Math.toDegrees(output.getRightWingInclination()));
		horStabSlider.setValue((int) Math.toDegrees(output.getHorStabInclination()));
		verStabSlider.setValue((int) Math.toDegrees(output.getVerStabInclination()));
		thrustSlider.setValue((int) output.getThrust());
		lbSlider.setValue((int) output.getLeftBrakeForce());
		fbSlider.setValue((int) output.getFrontBrakeForce());
		rbSlider.setValue((int) output.getRightBrakeForce());		
	}

	
	private SliderHandler buildSlider(String title, int orientation, int min, int val, int max, int majorSpacing, int minorSpacing, Hashtable<Integer, JLabel> lblTable, boolean hasBar) {
		JPanel panel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);
		
		JLabel titleLbl = new JLabel(title);
		panel.add(titleLbl, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));
		
		JSlider slider = new JSlider(orientation, min, max, val);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(majorSpacing);
		slider.setMinorTickSpacing(minorSpacing);
		slider.setLabelTable(lblTable);
		slider.setPaintLabels(true);
		if (!hasBar)
			slider.setUI(new NoBarMetalSliderUI());
		panel.add(slider, GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER));
		
		JLabel label = new JLabel("" + val);
		panel.add(label, GuiUtils.buildGBC(0, 2, GridBagConstraints.CENTER));
		
		SliderHandler handler = new SliderHandler(slider, panel, label);
		slider.addChangeListener(handler);
		return handler;
	}
	
	private static class SliderHandler implements ChangeListener {

		private final JSlider slider;
		private final JPanel panel;
		private final JLabel label;
		private int val;
		public boolean lock = true;
		
		public SliderHandler(JSlider slider, JPanel panel, JLabel label) {
			this.slider = slider;
			this.panel = panel;
			this.label = label;
			this.val = slider.getValue();
		}
		
		public void stateChanged(ChangeEvent e) {
			if (lock) {
				slider.setValue(val);
			} else {
				val = slider.getValue();
				label.setText("" + val);
			}
		}
		
		public void setValue(int val) {
			boolean oldLock = lock;
			lock = false;
			slider.setValue(val);
			lock = oldLock;
		}
	}
	
	/**
	 * shady workaround voor iets dat eigenlijk onnodig is.
	 */
	private static class NoBarMetalSliderUI extends MetalSliderUI {
		@Override
		public void paintTrack(Graphics g) {
			super.filledSlider = false;
			super.paintTrack(g);
		}
	}
}
