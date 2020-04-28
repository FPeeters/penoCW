package testbed.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import testbed.Physics;
import testbed.entities.airport.Airport;
import testbed.entities.packages.Package;
import testbed.world.World;
import testbed.world.helpers.DroneHelper;
import utils.Constants;

public class TestbedGui extends JFrame {

    private static final long serialVersionUID = 1L;

    private PackageTable packageTable;

    private JTable drones;

    private MiniMap minimap;

    private boolean lock = false;

    private int[] packageDetails;

    public TestbedGui(World world, DroneHelper helper, List<Airport> airports) {
        setTitle("Testbed GUI");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int ubuntuSiderBar = 0;
        int ubuntuHeader = 0;
        if (System.getProperty("os.name").contains("Linux")) {
            ubuntuSiderBar = 105;
            ubuntuHeader = 44;
        }

        setBounds(ubuntuSiderBar, 2 * ubuntuHeader, Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT);
        JPanel contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        drones = new JTable(new DroneTable(helper));
        drones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        drones.setColumnSelectionAllowed(false);
        drones.setRowSelectionAllowed(true);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        drones.getColumnModel().getColumn(0).setMaxWidth(75);
        drones.getColumnModel().getColumn(0).setCellRenderer(renderer);
        drones.getColumnModel().getColumn(3).setMaxWidth(100);
        drones.getColumnModel().getColumn(3).setCellRenderer(renderer);
        drones.getTableHeader().setFont(drones.getTableHeader().getFont().deriveFont(Font.BOLD));
        drones.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            private int lastId;

            public void valueChanged(ListSelectionEvent e) {
                if (lock || e.getValueIsAdjusting()) {
                    lock = false;
                    return;
                }

                int newId = (e.getFirstIndex() == lastId) ? e.getLastIndex() : e.getFirstIndex();

                world.setFollowDrone(newId);
                world.setFreeCamPos(helper.getDronePhysics(newId).getPosition().add(new Vector3f(0, 5, 0), new Vector3f()));
                minimap.setActiveDrone(newId);
                lastId = newId;
                lock = false;
            }
        });
        JScrollPane dronePane = new JScrollPane(drones);
        dronePane.setPreferredSize(new Dimension(Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT / 3));
        contentPane.add(dronePane);


        packageTable = new PackageTable();
        JTable packages = new JTable(packageTable);
        packages.setColumnSelectionAllowed(false);
        packages.setRowSelectionAllowed(true);
        packages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packages.getColumnModel().getColumn(0).setMaxWidth(75);
        packages.getColumnModel().getColumn(0).setCellRenderer(renderer);
        packages.getColumnModel().getColumn(3).setMaxWidth(100);
        packages.getTableHeader().setFont(packages.getTableHeader().getFont().deriveFont(Font.BOLD));
        packages.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            private int lastId;

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;

                int newId = (e.getFirstIndex() == lastId) ? e.getLastIndex() : e.getFirstIndex();
                lastId = newId;

                Package pack = packageTable.packages.get(newId);
                if (pack.isAlive())
                    world.setFreeCamPos(pack.getCube().getPosition().add(new Vector3f(0, 5.85f, 0), new Vector3f()));
            }
        });
        JScrollPane packagePane = new JScrollPane(packages);
        packagePane.setPreferredSize(new Dimension(Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT / 3 - 30));
        contentPane.add(packagePane);

        AddPackage addBtn = new AddPackage(airports.size());
        contentPane.add(addBtn.panel);

        minimap = new MiniMap(3000, 3000, helper, airports);
        minimap.setPreferredSize(new Dimension(Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT / 3));
        contentPane.add(minimap);
    }


    public void showGUI() {
        pack();
        setVisible(true);
    }


    public void addPackage(Package pack) {
        packageTable.addPackage(pack);
    }


    public void setActiveDrone(int activeDrone) {
        lock = true;
        drones.getSelectionModel().setSelectionInterval(activeDrone, activeDrone);
        minimap.setActiveDrone(activeDrone);
    }

    public int[] getNewPackage() {
        return packageDetails;
    }

    public void removePackage() {
        packageDetails = null;
    }

    private class DroneTable extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private DroneHelper helper;


        public DroneTable(DroneHelper helper) {
            this.helper = helper;
        }


        public String getColumnName(int col) {
            return new String[]{"ID", "DroneId", "Location", "Package"}[col];
        }

        public int getColumnCount() {
            return 4;
        }

        public int getRowCount() {
            return this.helper.getMaxNbDrones();
        }

        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return "" + row;

                case 1:
                    AutopilotConfig config = helper.getDroneConfig(row);
                    return config == null ? "" : config.getDroneID();

                case 2:
                    Physics physics = helper.getDronePhysics(row);
                    return physics == null ? "" : physics.getAirport() == null ? "In the air." : "At airport " +
                            physics.getAirportNb() + ", " + physics.getAirportLocoationDesc();
                case 3:
                    Package pack = helper.getDronePackage(row);
                    return pack == null ? "" : "" + packageTable.packages.indexOf(pack);
                default:
                    return null;
            }
        }

    }


    private static class PackageTable extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private List<Package> packages;


        public PackageTable() {
            packages = new ArrayList<>();
        }


        public void addPackage(Package pack) {
            this.packages.add(pack);
            this.fireTableDataChanged();
        }

        public String getColumnName(int col) {
            return new String[]{"Package Nb", "From", "To", "Status"}[col];
        }

        public int getColumnCount() {
            return 4;
        }

        public int getRowCount() {
            return packages.size();
        }

        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return row;

                case 1:
                    return "Airport " + packages.get(row).getFromAirport() + ", gate " + packages.get(row).getFromGate();

                case 2:
                    return "Airport " + packages.get(row).getDestAirport() + ", gate " + packages.get(row).getDestGate();

                case 3:
                    return packages.get(row).getStatusDesc();

                default:
                    return null;
            }
        }
    }


    private class AddPackage {

        private JPanel panel;

        private JSpinner fromPort, fromGate, destPort, destGate;

        public AddPackage(int nbPorts) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

            JButton btn = new JButton("Add package");
            btn.addActionListener(e -> packageDetails = new int[]{(int) fromPort.getValue(), (int) fromGate.getValue(),
					(int) destPort.getValue(), (int) destGate.getValue()});
            panel.add(btn);

            fromPort = new JSpinner(new SpinnerNumberModel(0, 0, nbPorts, 1));
            panel.add(fromPort);

            fromGate = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
            panel.add(fromGate);

            destPort = new JSpinner(new SpinnerNumberModel(0, 0, nbPorts, 1));
            panel.add(destPort);

            destGate = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
            panel.add(destGate);
        }
    }


    private static class MiniMap extends Component {

        private static final long serialVersionUID = 1L;

        private final int maxX;
        private final int maxY;

        private int activeDrone;

        private DroneHelper helper;
        private List<Vector3f> airports;

        public MiniMap(int maxX, int maxZ, DroneHelper helper, List<Airport> airports) {
            this.maxX = maxZ;
            this.maxY = maxX;

            this.helper = helper;
            this.airports = airports.stream().map(Airport::getPosition).collect(Collectors.toList());
        }

        public void setActiveDrone(int droneId) {
            this.activeDrone = droneId;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            Dimension size = this.getSize();

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));

            g2.drawRect(0, 0, size.width - 1, size.height - 1);

            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(1));

            int i = 0;
            for (Vector3f port : this.airports) {
                int x = (int) (size.width / 2 + port.x / this.maxX * size.width / 2),
                        y = (int) (size.height / 2 + port.z / this.maxY * size.height / 2);

                g2.fillRect(x - 6, y - 6, 12, 12);
                g2.drawString("A" + i, x - 7, y - 6);
                i++;
            }
            for (int drone : helper.droneIds.values()) {
                if (drone == activeDrone)
                    g2.setColor(Color.GREEN);
                else
                    g2.setColor(Color.RED);

                Vector3f pos = helper.getDronePhysics(drone).getPosition();
                int x = (int) (size.width / 2 + pos.x / this.maxX * size.width / 2),
                        y = (int) (size.height / 2 + pos.z / this.maxY * size.height / 2);

                g2.fillRect(x - 3, y - 3, 6, 6);
                g2.drawString("D" + drone, x + 3, y + 5);
            }
        }
    }

}
