package utils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.joml.Vector3f;

public class GuiUtils {

    public static GridBagConstraints buildGBC(int gridx, int gridy, int anchor) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = anchor;
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        return gbc;
    }

    public static GridBagConstraints buildGBC(int gridx, int gridy, int anchor, Insets insets) {
        GridBagConstraints gbc = buildGBC(gridx, gridy, anchor);
        gbc.insets = insets;
        return gbc;
    }

    public static JSpinner buildInputSpinner(JPanel parentPanel, String lblText, int ypos, int min, int max, int defaultValue, int stepSize) {
        JLabel lbl = new JLabel(lblText);
        GridBagConstraints gbc_lblnbCubes = buildGBC(0, ypos, GridBagConstraints.NORTHEAST, new Insets(5, 0, 5, 5));
        parentPanel.add(lbl, gbc_lblnbCubes);

        JSpinner spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(defaultValue, min, max, stepSize));
        spinner.setPreferredSize(new Dimension(70, 20));
        GridBagConstraints gbc_spinner = buildGBC(1, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 0));
        gbc_spinner.gridwidth = 3;
        parentPanel.add(spinner, gbc_spinner);

        return spinner;
    }

    public static JSpinner[] buildDoubleInputSpinner(JPanel parentPanel, String lblText, String beginText, String midText, int ypos, int[] min, int[] max, int[] defaultValue, int[] stepSize) {
        JLabel lbl = new JLabel(lblText);
        GridBagConstraints gbc_lbl = buildGBC(0, ypos, GridBagConstraints.NORTHEAST, new Insets(5, 0, 5, 5));
        parentPanel.add(lbl, gbc_lbl);

        JLabel lblBegin = new JLabel(beginText);
        GridBagConstraints gbc_lblBegin = buildGBC(1, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
        parentPanel.add(lblBegin, gbc_lblBegin);

        JSpinner firstSpinner = new JSpinner();
        firstSpinner.setModel(new SpinnerNumberModel(defaultValue[0], min[0], max[0], stepSize[0]));
        firstSpinner.setPreferredSize(new Dimension(70, 20));
        GridBagConstraints gbc_minSpinner = buildGBC(2, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 0, 5, 0));
        parentPanel.add(firstSpinner, gbc_minSpinner);

        JLabel lblMid = new JLabel(midText);
        GridBagConstraints gbc_lblSep = buildGBC(3, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
        parentPanel.add(lblMid, gbc_lblSep);

        JSpinner secSpinner = new JSpinner();
        secSpinner.setModel(new SpinnerNumberModel(defaultValue[1], min[1], max[1], stepSize[1]));
        secSpinner.setPreferredSize(new Dimension(70, 20));
        GridBagConstraints gbc_zMaxSpinner = buildGBC(4, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 0, 5, 0));
        parentPanel.add(secSpinner, gbc_zMaxSpinner);

        return new JSpinner[]{firstSpinner, secSpinner};
    }

    public static JSpinner[] buildTripleInputSpinner(JPanel parentPanel, String lblText, String beginText, String midText, String endText, int ypos, int[] min, int[] max, int[] defaultValue, int[] stepSize) {
        JLabel lbl = new JLabel(lblText);
        GridBagConstraints gbc_lbl = buildGBC(0, ypos, GridBagConstraints.NORTHEAST, new Insets(5, 0, 5, 5));
        parentPanel.add(lbl, gbc_lbl);

        JLabel lblBegin = new JLabel(beginText);
        GridBagConstraints gbc_lblBegin = buildGBC(1, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
        parentPanel.add(lblBegin, gbc_lblBegin);

        JSpinner firstSpinner = new JSpinner();
        firstSpinner.setModel(new SpinnerNumberModel(defaultValue[0], min[0], max[0], stepSize[0]));
        firstSpinner.setPreferredSize(new Dimension(70, 20));
        GridBagConstraints gbc_minSpinner = buildGBC(2, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 0, 5, 0));
        parentPanel.add(firstSpinner, gbc_minSpinner);

        JLabel lblMid = new JLabel(midText);
        GridBagConstraints gbc_lblMid = buildGBC(3, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
        parentPanel.add(lblMid, gbc_lblMid);

        JSpinner secSpinner = new JSpinner();
        secSpinner.setModel(new SpinnerNumberModel(defaultValue[1], min[1], max[1], stepSize[1]));
        secSpinner.setPreferredSize(new Dimension(70, 20));
        GridBagConstraints gbc_secSpinner = buildGBC(4, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 0, 5, 0));
        parentPanel.add(secSpinner, gbc_secSpinner);

        JLabel lblEnd = new JLabel(endText);
        GridBagConstraints gbc_lblEnd = buildGBC(5, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
        parentPanel.add(lblEnd, gbc_lblEnd);

        JSpinner endSpinner = new JSpinner();
        endSpinner.setModel(new SpinnerNumberModel(defaultValue[2], min[2], max[2], stepSize[2]));
        endSpinner.setPreferredSize(new Dimension(70, 20));
        GridBagConstraints gbc_endSpinner = buildGBC(6, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 0, 5, 0));
        parentPanel.add(endSpinner, gbc_endSpinner);

        return new JSpinner[]{firstSpinner, secSpinner, endSpinner};
    }


    public static JCheckBox buildCheckBox(JPanel parentPanel, String lblText, int ypos, boolean checked) {
        JLabel lbl = new JLabel(lblText);
        GridBagConstraints gbc_lbl = GuiUtils.buildGBC(0, ypos, GridBagConstraints.NORTHEAST, new Insets(5, 0, 5, 5));
        parentPanel.add(lbl, gbc_lbl);

        JCheckBox checkBox = new JCheckBox("", checked);
        GridBagConstraints gbc_planner = GuiUtils.buildGBC(1, ypos, GridBagConstraints.NORTHWEST, new Insets(5, 5, 5, 5));
        parentPanel.add(checkBox, gbc_planner);

        return checkBox;
    }


    public static Vector3f buildVector(JSpinner[] spinners) {
        float x = (float) ((int) spinners[0].getValue()),
                y = (float) ((int) spinners[1].getValue()),
                z = (float) ((int) spinners[2].getValue());
        return new Vector3f(x, y, z);
    }


}
