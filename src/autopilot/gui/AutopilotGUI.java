package autopilot.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import autopilot.airports.VirtualAirport;
import autopilot.airports.VirtualDrone;
import autopilot.airports.VirtualPackage;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class AutopilotGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private DroneControlUI droneUI;

    private DroneTable droneTable;

    private PackageTable packageTable;

    public AutopilotGUI(List<VirtualDrone> drones) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 500, 500);
        setTitle("Autopilot");

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        setContentPane(contentPanel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        droneTable = new DroneTable(drones);
        JTable droneJTable = new JTable(droneTable);
        droneJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        droneJTable.setColumnSelectionAllowed(false);
        droneJTable.setRowSelectionAllowed(true);
        droneJTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        droneJTable.getColumnModel().getColumn(0).setMaxWidth(35);
        droneJTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        droneJTable.getColumnModel().getColumn(4).setMaxWidth(100);
        droneJTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        droneJTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        droneJTable.getTableHeader().setFont(droneJTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        droneJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            private int lastId;

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                int newId;
                if (e.getFirstIndex() == lastId)
                    newId = e.getLastIndex();
                else
                    newId = e.getFirstIndex();
                lastId = newId;
                droneUI.setSelected(newId);
            }
        });
        JScrollPane dronePane = new JScrollPane(droneJTable);
        dronePane.setPreferredSize(new Dimension(500, 250));
        contentPanel.add(dronePane);

        packageTable = new PackageTable();
        JTable packageJTable = new JTable(packageTable);
        packageJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packageJTable.setColumnSelectionAllowed(false);
        packageJTable.setRowSelectionAllowed(false);
        packageJTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        packageJTable.getColumnModel().getColumn(0).setMaxWidth(35);
        packageJTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        packageJTable.getColumnModel().getColumn(4).setMaxWidth(100);
        packageJTable.getTableHeader().setFont(droneJTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        JScrollPane packagePane = new JScrollPane(packageJTable);
        packagePane.setPreferredSize(new Dimension(500, 250));
        contentPanel.add(packagePane);

        droneUI = new DroneControlUI(drones);
        contentPanel.add(droneUI.content);
    }


    public void showGUI() {
        pack();
        setVisible(true);
    }

    public boolean manualControl(int droneId) {
        return droneUI.getManual(droneId);
    }

    public AutopilotOutputs getOutputs() {
        return droneUI.getOutputs();
    }

    public void updateDrones() {
        droneTable.fireTableDataChanged();
    }

    public void updateOutputs() {
        droneUI.updateOutputs();
        droneUI.setTask(droneTable.drones.get(droneUI.selectedDrone).getTask());

        droneTable.fireTableRowsUpdated(0, droneTable.getRowCount() - 1);
        packageTable.fireTableDataChanged();
    }

    public void addPackage(VirtualPackage pack) {
        this.packageTable.packages.add(pack);
        this.packageTable.fireTableDataChanged();
    }


    private class DroneTable extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private List<VirtualDrone> drones;


        public DroneTable(List<VirtualDrone> drones) {
            this.drones = drones;
        }


        public String getColumnName(int col) {
            return new String[]{"ID", "DroneId", "Location", "Task", "Target", "Package"}[col];
        }

        public int getColumnCount() {
            return 6;
        }

        public int getRowCount() {
            return this.drones.size();
        }

        public String getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return "" + row;

                case 1:
                    AutopilotConfig config = drones.get(row).getConfig();
                    return config == null ? "" : config.getDroneID();

                case 2:
                    AutopilotInputs inputs = drones.get(row).getInputs();
                    return inputs == null ? "" : (int) inputs.getX() + ", " +
                            (int) inputs.getY() + ", " +
                            (int) inputs.getZ();

                case 3:
                    return drones.get(row).getTask();

                case 4:
                    VirtualAirport port = drones.get(row).getTarget();
                    return port == null ? "None" : "Airport " + port.getId();

                case 5:
                    VirtualPackage pack = drones.get(row).getPackage();
                    return drones.get(row).pickedUp() ? "Package " + packageTable.packages.indexOf(pack) : "None";

                default:
                    return "";
            }
        }
    }


    private class PackageTable extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private List<VirtualPackage> packages;

        public PackageTable() {
            this.packages = new ArrayList<>();
        }

        public String getColumnName(int col) {
            return new String[]{"ID", "From", "To", "Status", "Assigned To"}[col];
        }

        public int getColumnCount() {
            return 5;
        }

        public int getRowCount() {
            return packages.size();
        }

        @Override
        public String getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return "" + row;

                case 1:
                    return "Airport " + packages.get(row).getFromAirport() + ", gate "
                            + packages.get(row).getFromGate();

                case 2:
                    return "Airport " + packages.get(row).getToAirport() + ", gate "
                            + packages.get(row).getToGate();

                case 3:
                    return packages.get(row).getStatus();

                case 4:
                    VirtualDrone drone = packages.get(row).getAssignedDrone();
                    return drone == null ? "None" : "Drone " + droneTable.drones.indexOf(drone);

                default:
                    return "";
            }
        }
    }
}
