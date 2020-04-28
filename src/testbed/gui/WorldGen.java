package testbed.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import testbed.graphics.meshes.cube.BufferedCube;
import testbed.world.World;
import testbed.world.WorldBuilder;

import org.joml.Vector3f;

import utils.Constants;
import utils.Cubes;
import utils.GuiUtils;
import utils.Utils;

public enum WorldGen {

    random {

        private JSpinner nbCubesSpinner,xMinSpinner,xMaxSpinner,yMinSpinner,
                yMaxSpinner,zMinSpinner,zMaxSpinner,slowDownSpinner;
        private JCheckBox planner,physics;

        public String getComboText() {
            return "Random cubes";
        }

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

            Map<Vector3f, BufferedCube> worldObjects = new HashMap<>();
            Random rand = new Random();

            for (int i = 0; i < nbCubes; i++) {
                int x = rand.nextInt(Math.abs(xMin - xMax)) + xMin,
                        y = rand.nextInt(Math.abs(yMin - yMax)) + yMin,
                        z = rand.nextInt(Math.abs(zMin - zMax)) + zMin;
                worldObjects.put(new Vector3f(x, y, z), Cubes.getBufferedCubes()[rand.nextInt(Cubes.getBufferedCubes().length)]);
            }


            return new WorldBuilder(tSM, wantPhysicsEngine);
        }
    },

    oneCube {

        private JCheckBox planner,physics;
        private JSpinner slowDownSpinner,colorHSpinner,colorSSpinner;
        private JSpinner[] positionSpinners;

        @Override
        public String getComboText() {
            return "One cube";
        }

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
                    Utils.buildIntArr(-10000, -10000, -10000), Utils.buildIntArr(10000, 10000, 10000), Utils.buildIntArr(0, 0, -10), Utils.buildIntArr(1, 1, 1));

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

            BufferedCube cube = new BufferedCube(hue, ((float) sat) / 100f);
            Map<Vector3f, BufferedCube> worldObjects = new HashMap<>();
            worldObjects.put(new Vector3f(x, y, z), cube);

            return new WorldBuilder(tSM, wantPhysicsEngine);
        }

    },

    premade {

        private JList<String> worldList;
        private Map<String, World> worlds;

        public String getComboText() {
            return "Premade worlds";
        }

        public Container getContent() {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(0, 0));

            JPanel inputPanel = buildBase(panel, "Load one of the premade worlds", 2, 2);


            String[] classNames = Constants.PREMADE_WORLDS;
            Arrays.sort(classNames);

            worlds = new HashMap<>();
            for (String className : classNames) {
                try {
                    World world = (World) Class.forName("testbed.world.premade." + className).newInstance();
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
            worldList.addListSelectionListener(e -> {
                String world = worldList.getSelectedValue();
                if (!e.getValueIsAdjusting() && world != null) {
                    lblDesc.setText("Description for: " + world + "\n" + worlds.get(world).getDescription());
                    lblDesc.setPreferredSize(new Dimension(450, 50));
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

        private JCheckBox planner,physics;
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
            GridBagConstraints gbc_lblFileName = GuiUtils.buildGBC(0, 4, GridBagConstraints.NORTHEAST, new Insets(5, 5, 5, 5));
            inputPanel.add(lblFileName, gbc_lblFileName);

            fileTextField = new JTextField(20);
            GridBagConstraints gbc_textField = GuiUtils.buildGBC(1, 4, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
            inputPanel.add(fileTextField, gbc_textField);

            lblCheckResult = new JLabel();
            GridBagConstraints gbc_lblCheckResult = GuiUtils.buildGBC(1, 5, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
            gbc_lblCheckResult.gridwidth = 3;
            inputPanel.add(lblCheckResult, gbc_lblCheckResult);

            JButton checkBtn = new JButton("Check file");
            checkBtn.addActionListener(e -> validateFile(fileTextField.getText()));
            checkBtn.setPreferredSize(new Dimension((int) checkBtn.getPreferredSize().getWidth(), (int) fileTextField.getPreferredSize().getHeight()));
            GridBagConstraints gbc_checkBtn = GuiUtils.buildGBC(2, 4, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
            inputPanel.add(checkBtn, gbc_checkBtn);

            return panel;
        }

        private void validateFile(String fileName) {
            try {
                File file = new File(fileName);
                if (!file.exists()) {
                    lblCheckResult.setText("<html><font color='red'>Error: File not found.</font></html>");
                    return;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] coords = line.split(" ");
                    Float.valueOf(coords[0]);
                    Float.valueOf(coords[1]);
                    Float.valueOf(coords[2]);
                }
                reader.close();

            } catch (ArrayIndexOutOfBoundsException e) {
                lblCheckResult.setText("<html><font color='red'>Error: Not enough numbers on a line</font></html>");
                return;
            } catch (NumberFormatException e) {
                lblCheckResult.setText("<html><font color='red'>Error: Wrong number format</font></html>");
                return;
            } catch (IOException e) {
                lblCheckResult.setText("<html><font color='red'>Error: IO exception</font></html>");
                return;
            } catch (Exception e) {
                lblCheckResult.setText("<html><font color='red'>" + e.getMessage() + "</font></html>");
                return;
            }
            lblCheckResult.setText("<html><font color='green'>Valid file</font></html>");
        }

        private List<Vector3f> readFile() throws Exception {
            File file = new File(fileTextField.getText());
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            List<Vector3f> cubes = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] coords = line.split(" ");
                float x = Float.parseFloat(coords[0]),
                        y = Float.parseFloat(coords[1]),
                        z = Float.parseFloat(coords[2]);
                cubes.add(new Vector3f(x, y, z));
            }
            reader.close();

            return cubes;
        }

        @Override
        public World generateWorld() throws Exception {
            boolean wantPlanner = planner.isSelected(),
                    wantPhysics = physics.isSelected();

            int tSM = (int) slowDownSpinner.getValue();

            List<Vector3f> cubePositions = readFile();

            Map<Vector3f, BufferedCube> cubes = new HashMap<>();
            Random rand = new Random();

            for (Vector3f cubePosition : cubePositions) {
                BufferedCube cube = Cubes.getBufferedCubes()[rand.nextInt(Cubes.getBufferedCubes().length)];
                cubes.put(cubePosition, cube);
            }

            return new WorldBuilder(tSM, wantPhysics);
        }

    }, cilinder {

        private JCheckBox planner,physics;
        private JSpinner slowDownSpinner;

        @Override
        public String getComboText() {
            return "Generate cubes in a cilinder";
        }

        @Override
        public Container getContent() {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(0, 0));

            JPanel inputPanel = buildBase(panel, "Generate a world based on the requirement M 2.3,"
                    + " with all cubes positioned randomly in a cilinder, based on a certain distribution", 2, 4);

            planner = GuiUtils.buildCheckBox(inputPanel, "Motion planner?", 1, true);
            physics = GuiUtils.buildCheckBox(inputPanel, "Physics engine?", 2, true);

            slowDownSpinner = GuiUtils.buildInputSpinner(inputPanel, "Time slowdown factor:", 3, 1, 100, 10, 1);

            return panel;
        }

        @Override
        public World generateWorld() {
            boolean wantPlanner = planner.isSelected(),
                    wantPhysics = physics.isSelected();

            int tSM = (int) slowDownSpinner.getValue();

            Random rand = new Random();

            Map<Vector3f, BufferedCube> cubes = new HashMap<>();

            for (int i = 0; i < 5; i++) {
                BufferedCube cube = Cubes.getBufferedCubes()[rand.nextInt(Cubes.getBufferedCubes().length)];

                float r = rand.nextFloat() * 10, t = rand.nextFloat() * (float) Math.PI;
                Vector3f pos = new Vector3f((float) (r * Math.cos(t)), (float) (r * Math.sin(t)), (float) -40 * i);

                cubes.put(pos, cube);
            }

            return new WorldBuilder(tSM, wantPhysics);
        }

    };


    private static JPanel buildBase(JPanel panel, String text, int cols, int rows) {
        JLabel lbl = new JLabel();
        lbl.setText("<html>" + text + "</html>");
        panel.add(lbl, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        panel.add(inputPanel, BorderLayout.CENTER);
        GridBagLayout gbl_inputPanel = new GridBagLayout();
        gbl_inputPanel.columnWidths = new int[]{150};
        gbl_inputPanel.columnWeights = new double[cols];
        gbl_inputPanel.columnWeights[cols - 1] = 1.0;
        gbl_inputPanel.rowWeights = new double[rows];
        gbl_inputPanel.rowWeights[rows - 1] = 1.0;
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

    public abstract World generateWorld() throws Exception;
}