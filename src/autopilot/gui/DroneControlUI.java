package autopilot.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import autopilot.airports.VirtualDrone;
import interfaces.AutopilotConfig;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.GuiUtils;
import utils.Utils;

public class DroneControlUI {

    private List<VirtualDrone> drones;

    int selectedDrone;

    private boolean manual;

    JPanel content;

    private SliderHandler lwSlider, rwSlider, verStabSlider, horStabSlider, thrustSlider,
            lbSlider, rbSlider, fbSlider;

    private JLabel stateLabel, lblAutopilotGui;

    private JToggleButton manualToggle;


    public DroneControlUI(List<VirtualDrone> drones) {
        this.drones = drones;
        this.selectedDrone = 0;
        this.manual = false;

        buildContent(drones.get(selectedDrone).getConfig());
    }


    public void setSelected(int droneId) {
        this.selectedDrone = droneId;
        this.manual = false;
        this.manualToggle.setSelected(false);

        updateContent(drones.get(selectedDrone).getConfig());
        updateOutputs();
    }

    public void setTask(String task) {
        this.stateLabel.setText("Current Task: " + task);
    }


    public boolean getManual(int droneId) {
        return droneId == this.selectedDrone && this.manual;
    }

    public AutopilotOutputs getOutputs() {
        return Utils.buildOutputs(FloatMath.toRadians(lwSlider.getValue()),
                FloatMath.toRadians(rwSlider.getValue()),
                FloatMath.toRadians(verStabSlider.getValue()),
                FloatMath.toRadians(horStabSlider.getValue()),
                thrustSlider.getValue(),
                lbSlider.getValue(),
                fbSlider.getValue(),
                rbSlider.getValue());
    }


    public void updateOutputs() {
        if (manual)
            return;

        AutopilotOutputs output = drones.get(selectedDrone).getOutputs();

        lwSlider.setValue((int) Math.toDegrees(output.getLeftWingInclination()));
        rwSlider.setValue((int) Math.toDegrees(output.getRightWingInclination()));
        horStabSlider.setValue((int) Math.toDegrees(output.getHorStabInclination()));
        verStabSlider.setValue((int) Math.toDegrees(output.getVerStabInclination()));
        thrustSlider.setValue((int) output.getThrust());
        lbSlider.setValue((int) output.getLeftBrakeForce());
        fbSlider.setValue((int) output.getFrontBrakeForce());
        rbSlider.setValue((int) output.getRightBrakeForce());
    }


    private void buildContent(AutopilotConfig config) {
        float maxThrust = config.getMaxThrust();
        int maxAOA = (int) Math.toDegrees(config.getMaxAOA());
        float maxBrake = config.getRMax();

        this.content = new JPanel(new BorderLayout());

        JPanel northPanel = new JPanel();
        content.add(northPanel, BorderLayout.NORTH);
        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[]{250, 250};
        northPanel.setLayout(gbl);


        lblAutopilotGui = new JLabel("Autopilot GUI: " + config.getDroneID());
        lblAutopilotGui.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lbl = GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(5, 0, 5, 0));
        gbc_lbl.gridwidth = 2;
        northPanel.add(lblAutopilotGui, gbc_lbl);

        stateLabel = new JLabel("Current Task: ");
        stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        northPanel.add(stateLabel, GuiUtils.buildGBC(0, 1, GridBagConstraints.WEST, new Insets(5, 40, 5, 0)));

        manualToggle = new JToggleButton("Manual Control");
        manualToggle.addActionListener(e -> manual = ((JToggleButton) e.getSource()).isSelected());
        northPanel.add(manualToggle, GuiUtils.buildGBC(1, 1, GridBagConstraints.CENTER, new Insets(5, 0, 5, 20)));


        JPanel contentPanel = new JPanel();
        content.add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new GridBagLayout());

        JPanel leftPanel = new JPanel();
        contentPanel.add(leftPanel, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));
        leftPanel.setLayout(new GridBagLayout());

        JPanel brakePanel = new JPanel();
        leftPanel.add(brakePanel, GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER));
        brakePanel.setLayout(new GridBagLayout());

        Hashtable<Integer, JLabel> brakeTable = new Hashtable<>();
        JLabel zeroLbl = new JLabel("0");
        zeroLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
        brakeTable.put(0, zeroLbl);
        JLabel halfLbl = new JLabel("" + (int) (maxBrake / 2f));
        halfLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
        brakeTable.put((int) (maxBrake / 2f), halfLbl);
        JLabel maxLbl = new JLabel("" + (int) (maxBrake));
        maxLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
        brakeTable.put((int) (maxBrake), maxLbl);

        lbSlider = buildSlider("L brake", SwingConstants.VERTICAL, 0, (int) (maxBrake),
                (int) (maxBrake / 4f), (int) (maxBrake / 8f), brakeTable, true);
        lbSlider.slider.setPreferredSize(new Dimension(50, 100));
        brakePanel.add(lbSlider.panel, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(0, 20, 0, 5)));

        fbSlider = buildSlider("F brake", SwingConstants.VERTICAL, 0, (int) (maxBrake),
                (int) (maxBrake / 4f), (int) (maxBrake / 8f), brakeTable, true);
        fbSlider.slider.setPreferredSize(new Dimension(50, 100));
        brakePanel.add(fbSlider.panel, GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));

        rbSlider = buildSlider("R brake", SwingConstants.VERTICAL, 0, (int) (maxBrake),
                (int) (maxBrake / 4f), (int) (maxBrake / 8f), brakeTable, true);
        rbSlider.slider.setPreferredSize(new Dimension(50, 100));
        brakePanel.add(rbSlider.panel, GuiUtils.buildGBC(2, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));


        JPanel rightPanel = new JPanel();
        contentPanel.add(rightPanel, GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER));
        rightPanel.setLayout(new GridBagLayout());

        JPanel horSliderPanel = new JPanel();
        rightPanel.add(horSliderPanel, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));
        GridBagLayout gbl_horSliderPanel = new GridBagLayout();
        gbl_horSliderPanel.columnWidths = new int[]{60, 60, 60};
        horSliderPanel.setLayout(gbl_horSliderPanel);


        Hashtable<Integer, JLabel> lblTable = new Hashtable<>();
        for (int i = 0; i <= 10 + maxAOA; i += (maxAOA + 10) / 3) {
            JLabel lbl = new JLabel("" + i);
            lbl.setFont(lbl.getFont().deriveFont(9.0f));
            lblTable.put(i, lbl);
            if (i != 0) {
                JLabel lbl2 = new JLabel("" + (-i));
                lbl2.setFont(lbl.getFont().deriveFont(9.0f));
                lblTable.put(-i, lbl2);
            }
        }

        lwSlider = buildSlider("L wing", SwingConstants.VERTICAL, -10 - maxAOA, 10 + maxAOA, 30, 10, lblTable, false);
        horSliderPanel.add(lwSlider.panel, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));


        horStabSlider = buildSlider("H stab", SwingConstants.VERTICAL, -10 - maxAOA, 10 + maxAOA, 30, 10, lblTable, false);
        horSliderPanel.add(horStabSlider.panel, GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));


        rwSlider = buildSlider("R wing", SwingConstants.VERTICAL, -10 - maxAOA, 10 + maxAOA, 30, 10, lblTable, false);
        horSliderPanel.add(rwSlider.panel, GuiUtils.buildGBC(2, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));


        verStabSlider = buildSlider("V stab", SwingConstants.HORIZONTAL, -10 - maxAOA, 10 + maxAOA, 30, 10, lblTable, false);
        rightPanel.add(verStabSlider.panel, GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER, new Insets(5, 0, 5, 5)));


        Hashtable<Integer, JLabel> thrustTable = new Hashtable<>();
        thrustTable.put(0, zeroLbl);
        thrustTable.put((int) (maxThrust / 2f), new JLabel("" + (int) (maxThrust / 2f)));
        thrustTable.put((int) (maxThrust), new JLabel("" + (int) (maxThrust)));

        thrustSlider = buildSlider("Thrust", SwingConstants.VERTICAL, 0, (int) maxThrust,
                (int) (maxThrust / 10f), (int) (maxThrust / 20f), thrustTable, true);
        thrustSlider.slider.setPreferredSize(new Dimension(57, 270));
        GridBagConstraints gbc_thrustSlider = GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5));
        gbc_thrustSlider.gridheight = 2;
        rightPanel.add(thrustSlider.panel, gbc_thrustSlider);
    }

    private void updateContent(AutopilotConfig config) {
        float maxThrust = config.getMaxThrust();
        int maxAOA = (int) Math.toDegrees(config.getMaxAOA());
        float maxBrake = config.getRMax();

        lblAutopilotGui.setText("Autopilot GUI: " + config.getDroneID());

        Hashtable<Integer, JLabel> brakeTable = new Hashtable<>();
        JLabel zeroLbl = new JLabel("0");
        zeroLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
        brakeTable.put(0, zeroLbl);
        JLabel halfLbl = new JLabel("" + (int) (maxBrake / 2f));
        halfLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
        brakeTable.put((int) (maxBrake / 2f), halfLbl);
        JLabel maxLbl = new JLabel("" + (int) (maxBrake));
        maxLbl.setFont(zeroLbl.getFont().deriveFont(10.0f));
        brakeTable.put((int) (maxBrake), maxLbl);

        lbSlider.update(0, (int) maxBrake, brakeTable);
        rbSlider.update(0, (int) maxBrake, brakeTable);
        fbSlider.update(0, (int) maxBrake, brakeTable);

        Hashtable<Integer, JLabel> lblTable = new Hashtable<>();
        for (int i = 0; i <= 10 + maxAOA; i += (maxAOA + 10) / 3) {
            JLabel lbl = new JLabel("" + i);
            lbl.setFont(lbl.getFont().deriveFont(9.0f));
            lblTable.put(i, lbl);
            if (i != 0) {
                JLabel lbl2 = new JLabel("" + (-i));
                lbl2.setFont(lbl.getFont().deriveFont(9.0f));
                lblTable.put(-i, lbl2);
            }
        }

        lwSlider.update(-10 - maxAOA, 10 + maxAOA, lblTable);
        rwSlider.update(-10 - maxAOA, 10 + maxAOA, lblTable);
        horStabSlider.update(-10 - maxAOA, 10 + maxAOA, lblTable);
        verStabSlider.update(-10 - maxAOA, 10 + maxAOA, lblTable);

        Hashtable<Integer, JLabel> thrustTable = new Hashtable<>();
        thrustTable.put(0, zeroLbl);
        thrustTable.put((int) (maxThrust / 2f), new JLabel("" + (int) (maxThrust / 2f)));
        thrustTable.put((int) (maxThrust), new JLabel("" + (int) (maxThrust)));

        thrustSlider.update(0, (int) maxThrust, thrustTable);
    }


    private SliderHandler buildSlider(String title, int orientation, int min, int max, int majorSpacing, int minorSpacing, Hashtable<Integer, JLabel> lblTable, boolean hasBar) {
        JPanel panel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        panel.setLayout(gbl);

        JLabel titleLbl = new JLabel(title);
        panel.add(titleLbl, GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));

        JSlider slider = new JSlider(orientation, min, max, 0);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(majorSpacing);
        slider.setMinorTickSpacing(minorSpacing);
        slider.setLabelTable(lblTable);
        slider.setPaintLabels(true);
        if (!hasBar)
            slider.setUI(new NoBarMetalSliderUI());
        panel.add(slider, GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER));

        JLabel label = new JLabel("" + 0);
        panel.add(label, GuiUtils.buildGBC(0, 2, GridBagConstraints.CENTER));

        SliderHandler handler = new SliderHandler(slider, panel, label);
        slider.addChangeListener(handler);
        return handler;
    }


    private class SliderHandler implements ChangeListener {

        private final JSlider slider;
        private final JPanel panel;
        private final JLabel label;
        private int val;

        public SliderHandler(JSlider slider, JPanel panel, JLabel label) {
            this.slider = slider;
            this.panel = panel;
            this.label = label;
            this.val = slider.getValue();
        }

        public void stateChanged(ChangeEvent e) {
            if (!manual) {
                slider.setValue(val);
            } else {
                val = slider.getValue();
                label.setText("" + val);
            }
        }

        public void setValue(int val) {
            boolean oldLock = manual;
            manual = true;
            slider.setValue(val);
            manual = oldLock;
        }

        public int getValue() {
            return slider.getValue();
        }

        public void update(int min, int max, Hashtable<Integer, JLabel> lblTable) {
            slider.setMinimum(min);
            slider.setMaximum(max);
            slider.setLabelTable(lblTable);
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