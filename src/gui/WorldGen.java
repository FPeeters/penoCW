package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import entities.WorldObject;
import entities.meshes.cube.Cube;
import utils.Utils;
import world.World;
import world.WorldBuilder;

public enum WorldGen {
	
	random {
		
		private JSpinner nbCubesSpinner, xMinSpinner, xMaxSpinner, yMinSpinner,
						yMaxSpinner, zMinSpinner, zMaxSpinner, slowDownSpinner;
		private JCheckBox planner, physics; 
		
		public String getComboText() {return "Random cubes";}
		
		public Container getContent() {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			
			JPanel inputPanel = buildBase(panel, "This setting wil generate a world containing a specified amount of cubes,"
					+ "randomly generated in the specified region in a uniform way. Their color will also be randomly "
					+ "selected from red, green or blue", 5, 8);
			
			planner = GuiUtils.buildCheckBox(inputPanel, "Motion planner?", 1, true);
			
			physics = GuiUtils.buildCheckBox(inputPanel, "Physics engine?", 2, true);				
			
			slowDownSpinner = GuiUtils.buildInputSpinner(inputPanel, "Time slowdown factor:", 3, 1, 100, 10, 1);
			
			nbCubesSpinner = GuiUtils.buildInputSpinner(inputPanel, "Amount of cubes:", 4, 0, 10000, 200, 10);
			
			JSpinner[] xRange = GuiUtils.buildDoubleInputSpinner(inputPanel, "X-range:", "from", "to", 5, 
					Utils.buildIntArr(-1000, -1000), Utils.buildIntArr(1000, 1000), Utils.buildIntArr(-100, 100), Utils.buildIntArr(10, 10));
			xMinSpinner = xRange[0];
			xMaxSpinner = xRange[1];
			
			JSpinner[] yRange = GuiUtils.buildDoubleInputSpinner(inputPanel, "Y-range:", "from", "to", 6, 
					Utils.buildIntArr(-1000, -1000), Utils.buildIntArr(1000, 1000), Utils.buildIntArr(-100, 100), Utils.buildIntArr(10, 10));
			yMinSpinner = yRange[0];
			yMaxSpinner = yRange[1];
			
			JSpinner[] zRange = GuiUtils.buildDoubleInputSpinner(inputPanel, "Z-range:", "from", "to", 7, 
					Utils.buildIntArr(-1000, -1000), Utils.buildIntArr(1000, 1000), Utils.buildIntArr(-100, 100), Utils.buildIntArr(10, 10));
			zMinSpinner = zRange[0];
			zMaxSpinner = zRange[1];
			
			return panel;
		}

		public World generateWorld() {
			int tSM = (int) slowDownSpinner.getValue(),
					nbCubes = (int) nbCubesSpinner.getValue(),
					xMin = (int) xMinSpinner.getValue(),
					xMax = (int) xMaxSpinner.getValue(),
					yMin = (int) yMinSpinner.getValue(),
					yMax = (int) yMaxSpinner.getValue(),
					zMin = (int) zMinSpinner.getValue(),
					zMax = (int) zMaxSpinner.getValue();
			
			boolean wantPhysicsEngine = physics.isSelected();
			boolean wantPlanner = planner.isSelected();
			
			WorldObject[] worldObjects = new WorldObject[nbCubes];
			Random rand = new Random();
			
			Cube redCube = new Cube(0,1f), greenCube = new Cube(120,1f),
					blueCube = new Cube(240,1f);
	        Cube[] cubes = new Cube[]{redCube, greenCube, blueCube};
	        
			for (int i = 0; i < nbCubes; i++) {
	            WorldObject cube = new WorldObject(cubes[rand.nextInt(cubes.length)].getMesh());
	            cube.setScale(0.5f);
	            int x = rand.nextInt(xMin + xMax)-xMin,
	            		y = rand.nextInt(yMin + yMax)-yMin,
	            		z = rand.nextInt(zMin + zMax)-zMin;

	            cube.setPosition(x, y, z);
	            worldObjects[i] = cube;				
			}
			
			return new WorldBuilder(tSM, wantPhysicsEngine, wantPlanner, worldObjects);		
		}
	},

oneCube {

	private JCheckBox planner, physics;
	private JSpinner slowDownSpinner, colorHSpinner, colorSSpinner;
	private JSpinner[] positionSpinners;

	@Override
	public String getComboText() {return "One cube";}

	@Override
	public Container getContent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel inputPanel = buildBase(panel, "This setting wil generate a world containing one single cube, with it's center at the "
				+ "given position with the specified color", 7, 6);
		
		planner = GuiUtils.buildCheckBox(inputPanel, "Motion planner?", 1, true);
		
		physics = GuiUtils.buildCheckBox(inputPanel, "Physics engine?", 2, true);				
		
		slowDownSpinner = GuiUtils.buildInputSpinner(inputPanel, "Time slowdown factor:", 3, 1, 100, 10, 1);
		
		positionSpinners = GuiUtils.buildTripleInputSpinner(inputPanel, "Cube position:", "X", "Y", "Z", 4, 
				Utils.buildIntArr(-10000,-10000,-10000), Utils.buildIntArr(10000,10000,10000), Utils.buildIntArr(0,0,-10), Utils.buildIntArr(1,1,1));
		
		JSpinner[] colorSpinners = GuiUtils.buildDoubleInputSpinner(inputPanel, "Cube color:", "H", "S(%)", 5, 
				Utils.buildIntArr(0, 0), Utils.buildIntArr(360, 100), Utils.buildIntArr(0, 0), Utils.buildIntArr(1, 1));
		colorHSpinner = colorSpinners[0];
		colorSSpinner = colorSpinners[1];
		
		return panel;
	}

	@Override
	public World generateWorld() {
		int tSM = (int) slowDownSpinner.getValue(),
				hue = (int) colorHSpinner.getValue(),
				sat = (int) colorSSpinner.getValue(),
				x = (int) positionSpinners[0].getValue(),
				y = (int) positionSpinners[1].getValue(),
				z = (int) positionSpinners[2].getValue();
		
		boolean wantPhysicsEngine = physics.isSelected();
		boolean wantPlanner = planner.isSelected();
		
		WorldObject[] worldObjects = new WorldObject[] {new WorldObject((new Cube(hue, sat)).getMesh())};
		worldObjects[0].setPosition(x, y, z);
		
		return new WorldBuilder(tSM, wantPhysicsEngine, wantPlanner, worldObjects);		
	}
	
}, 

premade {
	
	private JList<String> worldList;
	private String[] classNames;
	private Map<String, World> worlds;
	
	public String getComboText() {return "Premade worlds";}

	public Container getContent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel inputPanel = buildBase(panel, "Load one of the premade worlds", 2, 2);
		
		classNames = new String[] {"CubeWorld", "StopWorld", "TestWorld", "TestWorldFlyStraight"};
		worlds = new HashMap<>();
		for (String className: classNames) {
			try {
				World world = (World)Class.forName("world." + className).newInstance();
				worlds.put(className, world);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		JTextArea lblDesc = new JTextArea();
		lblDesc.setText("Description for: ");
		lblDesc.setLineWrap(true);
		lblDesc.setWrapStyleWord(true);
		lblDesc.setBackground(panel.getBackground());
		GridBagConstraints gbc_lblDesc = GuiUtils.buildGBC(1, 1, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
		inputPanel.add(lblDesc, gbc_lblDesc);
		
		JPanel listPanel = new JPanel();
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		listPanel.setBorder(border);
		
		worldList = new JList<>(classNames);
		worldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		worldList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String world = worldList.getSelectedValue();
				if (!e.getValueIsAdjusting() && world != null) {
					lblDesc.setText("Description for: " + world + "\n" + worlds.get(world).getDescription());
					lblDesc.setPreferredSize(new Dimension(450, 50));
				}
			}
		});
		GridBagConstraints gbc_listPanel = GuiUtils.buildGBC(0, 1, GridBagConstraints.NORTHEAST, new Insets(5, 5, 5, 5));
		listPanel.add(worldList);
		inputPanel.add(listPanel, gbc_listPanel);		
		
		return panel;
	}
	
	@Override
	public World generateWorld() {
		return worlds.get(worldList.getSelectedValue());
	}
	
	
	
},

loadFile {
	
	private JCheckBox planner, physics;
	private JSpinner slowDownSpinner;
	private JLabel lblCheckResult;
	private JTextField fileTextField;
	
	@Override
	public String getComboText() {
		return "Load from file";
	}

	@Override
	public Container getContent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel inputPanel = buildBase(panel, "Load a world from a file containing the position of all cubes.", 3, 6);
		
		planner = GuiUtils.buildCheckBox(inputPanel, "Motion planner?", 1, true);		
		physics = GuiUtils.buildCheckBox(inputPanel, "Physics engine?", 2, true);
		
		slowDownSpinner = GuiUtils.buildInputSpinner(inputPanel, "Time slowdown factor:", 3, 1, 100, 10, 1);

		JLabel lblFileName = new JLabel("File path: ");
		GridBagConstraints gbc_lblFileName = GuiUtils.buildGBC(0, 4, GridBagConstraints.NORTHEAST, new Insets(5,5,5,5));
		inputPanel.add(lblFileName, gbc_lblFileName);
		
		fileTextField = new JTextField(20);
		GridBagConstraints gbc_textField = GuiUtils.buildGBC(1, 4, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
		inputPanel.add(fileTextField, gbc_textField);
		
		lblCheckResult = new JLabel();
		GridBagConstraints gbc_lblCheckResult = GuiUtils.buildGBC(1, 5, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
		gbc_lblCheckResult.gridwidth = 3;
		inputPanel.add(lblCheckResult, gbc_lblCheckResult);
		
		JButton checkBtn = new JButton("Check file");
		checkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				validateFile(fileTextField.getText());
			}
		});
		checkBtn.setPreferredSize(new Dimension((int)checkBtn.getPreferredSize().getWidth(), (int)fileTextField.getPreferredSize().getHeight()));
		GridBagConstraints gbc_checkBtn = GuiUtils.buildGBC(2, 4, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
		inputPanel.add(checkBtn, gbc_checkBtn);
		
		
		
		return panel;
	}
	
	private void validateFile(String fileName) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				lblCheckResult.setText("Error: File not found.");
				return;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] coords = line.split(" ");
				float x = Float.valueOf(coords[0]),
						y = Float.valueOf(coords[1]),
						z =	Float.valueOf(coords[2]);
			}
			reader.close();
			
		} catch (ArrayIndexOutOfBoundsException e) {
			lblCheckResult.setText("Error: Not enough numbers on a line");
			return;
		} catch (NumberFormatException e) {
			lblCheckResult.setText("Error: Wrong number format");
			return;
		} catch (IOException e) {
			lblCheckResult.setText("Error: IO exception");
			return;
		} catch (Exception e) {
			lblCheckResult.setText(e.getMessage());
			return;
		}
		
		lblCheckResult.setText("Valid file");
		
	}
	
	
	@Override
	public World generateWorld() {
		boolean wantPlanner = planner.isSelected(),
				wantPhysics = physics.isSelected();
		
		int tSM = (int) slowDownSpinner.getValue();
		
		
		
		return new WorldBuilder(tSM, wantPhysics, wantPlanner, new WorldObject[] {});
	}
	
};
	

private static JPanel buildBase(JPanel panel, String text, int cols, int rows) {		
	JLabel lbl = new JLabel();
	lbl.setText("<html>" + text + "</html>");
	panel.add(lbl, BorderLayout.NORTH);
	
	JPanel inputPanel = new JPanel();
	panel.add(inputPanel, BorderLayout.CENTER);
	GridBagLayout gbl_inputPanel = new GridBagLayout();
	gbl_inputPanel.columnWidths = new int[] {150};
	gbl_inputPanel.columnWeights = new double[cols];
	gbl_inputPanel.columnWeights[cols-1] = 1.0;
	gbl_inputPanel.rowWeights = new double[rows];
	gbl_inputPanel.rowWeights[rows-1] = 1.0;
	inputPanel.setLayout(gbl_inputPanel);
	
	JSeparator separator = new JSeparator();
	GridBagConstraints gbc_separator = GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(5, 0, 5, 0));
	gbc_separator.fill = GridBagConstraints.BOTH;
	gbc_separator.gridwidth = cols;
	inputPanel.add(separator, gbc_separator);
	
	return inputPanel;
}

public abstract String getComboText();

public abstract Container getContent();

public abstract World generateWorld(); 
}