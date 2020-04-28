package testbed.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.border.EmptyBorder;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import testbed.world.World;
import testbed.world.WorldBuilder;
import utils.GuiUtils;
import utils.Utils;


public class ConfigSetupGUI extends JDialog {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> genComboBox;

    private JSpinner[] velSpinners;

    private JSpinner[] posSpinners;

    private Map<String, WorldGen> worldGens;

    private JCheckBox logCheck;


    public static void main(String[] args) throws Exception {
        ConfigSetupGUI dlg = new ConfigSetupGUI();
        System.out.println(dlg.showDialog());
    }


    /**
     * Create the frame.
     */
    public ConfigSetupGUI() {
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screen.getWidth();
        int screenHeight = (int) screen.getHeight();
        int windowWidth = 700; // => 650
        int windowHeight = 600; // => 300
        setBounds((screenWidth - windowWidth) / 2, (screenHeight - windowHeight) / 2, windowWidth, windowHeight);


        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
                System.exit(0);
            }
        });


        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));


        JLabel lblSimulationInitialisation = new JLabel("Simulation initialisation");
        contentPane.add(lblSimulationInitialisation, BorderLayout.NORTH);
        lblSimulationInitialisation.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblSimulationInitialisation.setHorizontalAlignment(SwingConstants.CENTER);


        JPanel btnPanel = new JPanel();
        contentPane.add(btnPanel, BorderLayout.SOUTH);
        JButton btnStart = new JButton("Start simulation");
        btnStart.addActionListener(arg0 -> {
			setVisible(false);
			dispose();
		});
        btnPanel.add(btnStart);

        SwingUtilities.getRootPane(btnStart).setDefaultButton(btnStart);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);


        // create the worldgen tab
        JPanel worldGenPanel = new JPanel();
        tabbedPane.addTab("Worldgen", worldGenPanel);
        worldGenPanel.setLayout(new BorderLayout(0, 0));

        JLabel lblWorldgen = new JLabel("World generation settings");
        lblWorldgen.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblWorldgen.setHorizontalAlignment(SwingConstants.LEFT);
        worldGenPanel.add(lblWorldgen, BorderLayout.NORTH);

        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BorderLayout(0, 0));
        worldGenPanel.add(selectorPanel, BorderLayout.CENTER);

        JPanel genCards = new JPanel();
        CardLayout genCardLayout = new CardLayout();
        genCards.setLayout(genCardLayout);
        selectorPanel.add(genCards, BorderLayout.CENTER);

        String[] genComboLbls = new String[WorldGen.values().length];
        worldGens = new HashMap<>();
        int i = 0;
        for (WorldGen gen : new WorldGen[]{WorldGen.premade}) {
            genCards.add(gen.getContent(), gen.getComboText());
            genComboLbls[i] = gen.getComboText();
            worldGens.put(gen.getComboText(), gen);
            i++;
        }

        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new BorderLayout(0, 0));
        selectorPanel.add(comboPanel, BorderLayout.NORTH);

        JPanel anotherPanel = new JPanel();
        anotherPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboPanel.add(anotherPanel, BorderLayout.NORTH);
        JLabel selectorLbl = new JLabel("Select world generation method: ");
        anotherPanel.add(selectorLbl);

        genComboBox = new JComboBox<>();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(genComboLbls);
        genComboBox.setModel(comboBoxModel);
        anotherPanel.add(genComboBox);
        genComboBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				genCardLayout.show(genCards, (String) e.getItem());
			}
		});
        genComboBox.setSelectedItem(WorldGen.premade.getComboText());

        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logCheck = new JCheckBox("Enable logging?");
        checkPanel.add(logCheck);
        comboPanel.add(checkPanel, BorderLayout.SOUTH);
    }

    public World showDialog() throws Exception {
        this.setVisible(true);

        return prepareConfig();
    }

    private World prepareConfig() throws Exception {

        World world = worldGens.get(genComboBox.getSelectedItem()).generateWorld();

        if (world == null)
            throw new Exception("No worldgen selected");

        if (world instanceof WorldBuilder) {
            Vector3f pos = GuiUtils.buildVector(posSpinners),
                    vel = GuiUtils.buildVector(velSpinners);

            ((WorldBuilder) world).setupDrone(generateAutoPilotConfig(), pos, vel);
        }

        if (logCheck.isSelected())
            world.initLogging(0);

        return world;
    }

    private AutopilotConfig generateAutoPilotConfig() {
        return Utils.createDefaultConfig("drone1");
    }
}
