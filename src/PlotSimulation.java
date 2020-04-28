import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;

import utils.FloatMath;
import utils.GuiUtils;

public class PlotSimulation {
	
	public static void main(String[] args) throws Exception {
		File file = new File("position.log");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line = reader.readLine();
		
		DefaultCategoryDataset dataX = new DefaultCategoryDataset(),
				dataY = new DefaultCategoryDataset(),
				dataZ = new DefaultCategoryDataset(),
				dataHead = new DefaultCategoryDataset(),
				dataPit = new DefaultCategoryDataset(),
				dataRoll = new DefaultCategoryDataset(),
				dataLIncl = new DefaultCategoryDataset(),
				dataHIncl = new DefaultCategoryDataset(),
				dataRIncl = new DefaultCategoryDataset(),
				dataVIncl = new DefaultCategoryDataset(),
				dataThrust = new DefaultCategoryDataset(),
				dataXZ = new DefaultCategoryDataset();
		
		try {
			while ((line = reader.readLine()) != null) {
				String[] floats = line.split(" ");
				float time = Float.valueOf(floats[0].substring(0, floats[0].length()-2)),
					  x = Float.valueOf(floats[1]),
					  y = Float.valueOf(floats[2]),
					  z = Float.valueOf(floats[3]),
					  head = FloatMath.toDegrees(Float.valueOf(floats[4])),
					  pit = FloatMath.toDegrees(Float.valueOf(floats[5])),
					  roll = FloatMath.toDegrees(Float.valueOf(floats[6])),
					  lIncl = FloatMath.toDegrees(Float.valueOf(floats[7])),
					  hIncl = FloatMath.toDegrees(Float.valueOf(floats[8])),
					  rIncl = FloatMath.toDegrees(Float.valueOf(floats[9])),
					  vIncl = FloatMath.toDegrees(Float.valueOf(floats[10])),
					  thrust = Float.valueOf(floats[11]);
				dataX.addValue(x, "X", time + "");
				dataY.addValue(y, "Y", time + "");
				dataZ.addValue(z, "Z", time + "");
				dataHead.addValue(head, "Head", time + "");
				dataPit.addValue(pit, "Pit", time + "");
				dataRoll.addValue(roll, "Roll", time + "");
				dataLIncl.addValue(lIncl, "Left Incl", time + "");
				dataHIncl.addValue(hIncl, "Horz Incl", time + "");
				dataRIncl.addValue(rIncl, "Right Incl", time + "");
				dataVIncl.addValue(vIncl, "Vert Incl", time + "");
				dataThrust.addValue(thrust, "Thrust", time + "");
				dataXZ.addValue(-z, "XZ plot", x + "");
			}
		} catch (ArrayIndexOutOfBoundsException ignored) {
		}
		
		reader.close();
		
		JFreeChart chartX = ChartFactory.createLineChart("X", null, null, dataX, PlotOrientation.VERTICAL, true, false, false),
				chartY = ChartFactory.createLineChart("Y", null, null, dataY, PlotOrientation.VERTICAL, true, false, false),
				chartZ = ChartFactory.createLineChart("Z", null, null, dataZ, PlotOrientation.VERTICAL, true, false, false),
				chartHead = ChartFactory.createLineChart("Heading", null, null, dataHead, PlotOrientation.VERTICAL, true, false, false),
				chartPit = ChartFactory.createLineChart("Pitch", null, null, dataPit, PlotOrientation.VERTICAL, true, false, false),
				chartRoll = ChartFactory.createLineChart("Roll", null, null, dataRoll, PlotOrientation.VERTICAL, true, false, false),
				chartLIncl = ChartFactory.createLineChart("Left Incl", null, null, dataLIncl, PlotOrientation.VERTICAL, true, false, false),
				chartHIncl = ChartFactory.createLineChart("Horz Incl", null, null, dataHIncl, PlotOrientation.VERTICAL, true, false, false),
				chartRIncl = ChartFactory.createLineChart("Right Incl", null, null, dataRIncl, PlotOrientation.VERTICAL, true, false, false),
				chartVIncl = ChartFactory.createLineChart("Vert Incl", null, null, dataVIncl, PlotOrientation.VERTICAL, true, false, false),
				chartThrust = ChartFactory.createLineChart("Thrust", null, null, dataThrust, PlotOrientation.VERTICAL, true, false, false),
				chartXZ = ChartFactory.createLineChart("XZ", null, null, dataXZ, PlotOrientation.VERTICAL, true, false, false);
		
		ChartPanel panelX = new ChartPanel(chartX),
				panelY = new ChartPanel(chartY),
				panelZ = new ChartPanel(chartZ),
				panelHead = new ChartPanel(chartHead),
				panelPit = new ChartPanel(chartPit),
				panelRoll = new ChartPanel(chartRoll),
				panelLIncl = new ChartPanel(chartLIncl),
				panelHIncl = new ChartPanel(chartHIncl),
				panelRIncl = new ChartPanel(chartRIncl),
				panelVIncl = new ChartPanel(chartVIncl),
				panelThrust = new ChartPanel(chartThrust),
				panelXZ = new ChartPanel(chartXZ);
		
		panelX.setPreferredSize(new Dimension(600, 500));
		panelY.setPreferredSize(new Dimension(600, 500));
		panelZ.setPreferredSize(new Dimension(600, 500));
		panelHead.setPreferredSize(new Dimension(600, 500));
		panelPit.setPreferredSize(new Dimension(600, 500));
		panelRoll.setPreferredSize(new Dimension(600, 500));
		panelLIncl.setPreferredSize(new Dimension(600, 500));
		panelHIncl.setPreferredSize(new Dimension(600, 500));
		panelRIncl.setPreferredSize(new Dimension(600, 500));
		panelVIncl.setPreferredSize(new Dimension(600, 500));
		panelThrust.setPreferredSize(new Dimension(600, 500));
		panelXZ.setPreferredSize(new Dimension(600, 500));
		
		ApplicationFrame frame = new ApplicationFrame("Simulation analysis");
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JScrollPane content = new JScrollPane(panel);
		frame.setContentPane(content);
		panel.setPreferredSize(new Dimension(1900, 2100));
		
		panel.add(panelX,GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER));
		panel.add(panelY,GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER));
		panel.add(panelZ,GuiUtils.buildGBC(2, 0, GridBagConstraints.CENTER));
		panel.add(panelHead,GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER));
		panel.add(panelPit,GuiUtils.buildGBC(1, 1, GridBagConstraints.CENTER));
		panel.add(panelRoll,GuiUtils.buildGBC(2, 1, GridBagConstraints.CENTER));
		panel.add(panelLIncl,GuiUtils.buildGBC(0, 2, GridBagConstraints.CENTER));
		panel.add(panelHIncl,GuiUtils.buildGBC(1, 2, GridBagConstraints.CENTER));
		panel.add(panelRIncl,GuiUtils.buildGBC(2, 2, GridBagConstraints.CENTER));
		panel.add(panelVIncl,GuiUtils.buildGBC(0, 3, GridBagConstraints.CENTER));
		panel.add(panelThrust,GuiUtils.buildGBC(1, 3, GridBagConstraints.CENTER));
		panel.add(panelXZ,GuiUtils.buildGBC(2, 3, GridBagConstraints.CENTER));
		
		frame.pack();
		frame.setVisible(true);
	}
	
	
	
	
	
}
