package csu.physics.pv;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Window extends JPanel {

    // Main frame
    private JFrame frame;
    private JPanel panel = new JPanel();

    // UI elements
    private JPanel leftPanel, rightPanel;
    private JScrollPane listScroller;
    private JTextField dirField;

    private String [] fileNames;
    private File [] filePaths;
    private JVReader reader;
    private JVWriter writer;

    // input format
    private String dataFormat = "physics";
    // output format
    private boolean printCurves = true;
    private boolean printParams = true;


    /**
     * Constructor sets up the main window with a left and right panel, adding Component to each.
     */
    private Window() {

        // Initialize the window and set layout
        frame = new JFrame("J-V Data Importer");

        panel.setLayout(new GridLayout(0,2));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(panel);

        // Add listener for the close button
        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                }
        );

        // make left panel and add
        leftPanel = new JPanel();
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.setLayout(new GridLayout(4,0));
        panel.add(leftPanel);

        // make right panel and add
        rightPanel = new JPanel();
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightPanel.setLayout(new BorderLayout());
        panel.add(rightPanel);

        // Create the panels and components
        createFilesButtons();
        createFormatSelection();
        createOutputSelection();
        createGoButton();
        createDirField();
        createScroller();

        // set UI theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Set window size and center in the screen
        frame.setSize(new Dimension(550, 450));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    /**
     * Creates the "Select Files" and "Clear Files" buttons and updates the directory and file list in the UI.
     */
    private void createFilesButtons() {

        class SelectFilesListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {

                // Setup the file choosing dialog
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new java.io.File(""));
                fileChooser.setDialogTitle("Select J-V Files");
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setAcceptAllFileFilterUsed(false);

                // Add the files to the scroller, otherwise throw a warning
                if (fileChooser.showOpenDialog(Window.this) == JFileChooser.APPROVE_OPTION) {
                    filePaths = fileChooser.getSelectedFiles();
                    updateFileNames();
                    updateDirField();
                    updateScroller();
                }
            }
        }

        class ClearFilesListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {

                fileNames = new String[0];
                filePaths = new File[0];
                updateScroller();
                updateDirField();

            }
        }

        // make a sub-panel
        JPanel filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));

        // Add title
        TitledBorder title = BorderFactory.createTitledBorder("J-V Data");
        filesPanel.setBorder(title);

        // create buttons
        JButton selectButton = new JButton("Select Files");
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectButton.setFocusPainted(false);
        selectButton.addActionListener(new SelectFilesListener());

        JButton clearButton = new JButton("Clear Files");
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(new ClearFilesListener());

        // add to panel
        filesPanel.add(Box.createVerticalGlue());
        filesPanel.add(selectButton);
        filesPanel.add(Box.createVerticalGlue());
        filesPanel.add(clearButton);
        filesPanel.add(Box.createVerticalGlue());
        leftPanel.add(filesPanel);
    }

    /**
     * Creates radio buttons for the input format (physics, engineering, etc.) and updates the dataFormat flag
     * appropriately.
     */
    private void createFormatSelection() {

        class SelectFormatListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {

                // update dataFormat depending on which radio button was selected
                if ("physics".equals(e.getActionCommand())) {
                    dataFormat = "physics";
                } else if ("engr".equals(e.getActionCommand())) {
                    dataFormat = "engr";
                }
            }
        }

        // create the buttons
        JRadioButton physButton = new JRadioButton("Physics");
        physButton.setBorder(new EmptyBorder(10,10,5,10));
        physButton.addActionListener(new SelectFormatListener());
        physButton.setActionCommand("physics");
        physButton.setFocusPainted(false);
        physButton.setSelected(true);

        JRadioButton engrButton = new JRadioButton("ERC");
        engrButton.setBorder(new EmptyBorder(0,10,0,10));
        engrButton.setActionCommand("engr");
        engrButton.setFocusPainted(false);
        engrButton.addActionListener(new SelectFormatListener());

        // group buttons
        ButtonGroup group = new ButtonGroup();
        group.add(physButton);
        group.add(engrButton);

        // make a sub-panel
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));

        // Add title
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Input Format");
        radioPanel.setBorder(title);

        // add the buttons
        radioPanel.add(physButton);
        radioPanel.add(engrButton);
        leftPanel.add(radioPanel);
    }

    /**
     * Sets the print options to be used by {@link JVWriter}.
     */
    private void createOutputSelection() {

        final JCheckBox curvesButton = new JCheckBox("Curves");
        final JCheckBox summaryButton = new JCheckBox("Summary");

        class OutputSelectionListener implements ItemListener {

            public void itemStateChanged(ItemEvent e) {

                // get the ItemSelectable of the event
                Object source = e.getItemSelectable();

                // if the button was selected, toggle the print token to true
                if (source == curvesButton) {
                    printCurves = true;
                } else if (source == summaryButton) {
                    printParams = true;
                }

                // if the button was deselected, toggle the print token to false
                if (e.getStateChange() == ItemEvent.DESELECTED) {

                    if (source == curvesButton) {
                        printCurves = false;
                    } else if (source == summaryButton) {
                        printParams = false;
                    }
                }

            }

        }

        // Add the buttons, on by default
        curvesButton.setSelected(true);
        curvesButton.addItemListener(new OutputSelectionListener());

        summaryButton.setSelected(true);
        summaryButton.addItemListener(new OutputSelectionListener());

        // make a sub-panel
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));

        // Add title
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Output Options");
        outputPanel.setBorder(title);

        // add the buttons
        outputPanel.add(curvesButton);
        outputPanel.add(summaryButton);
        leftPanel.add(outputPanel);

    }

    /**
     * Creates the Component that triggers the data reading and writing procedures when pressed.
     */
    private void createGoButton() {

        class GoListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                // begin by reading the files in
                reader = new JVReader();
                try {

                    if (filePaths == null || filePaths.length == 0) {
                        JOptionPane.showMessageDialog(panel,
                                "No files were selected",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    reader.read(filePaths);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                // convert data into appropriate JVData type
                JVData data = null;
                if (dataFormat.equals("physics")) {
                    data = new PhysicsData(reader.getFileData());
                } else if (dataFormat.equals("engr")) {
                    data = new EngrData(reader.getFileData());
                }

                if (data == null || !data.isValidData()) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid input, check files and try again",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // pass file names and extract param and curves data
                data.setFileNames(fileNames);
                data.extractData();

                // setup the writer
                writer = new JVWriter(data);
                // set the print options
                ArrayList<Boolean> printOptions = new ArrayList<>();
                printOptions.add(printCurves);
                printOptions.add(printParams);
                writer.setPrintOptions(printOptions);
                // do it
                boolean success = writer.write();

                // display dialog reporting results
                if (success) {

                    JOptionPane.showMessageDialog(frame,
                            "J-V import complete",
                            "Success",
                            JOptionPane.PLAIN_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Process cancelled",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        }

        // make a sub-panel
        JPanel goPanel = new JPanel();
        goPanel.setLayout(new BoxLayout(goPanel, BoxLayout.Y_AXIS));
        goPanel.setBorder(new EmptyBorder(10,10,10,10));

        // Create button
        JButton goButton = new JButton("Go!");
        goButton.addActionListener(new GoListener());
        goButton.setPreferredSize(new Dimension(80,50));
        goButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        goButton.setFocusPainted(false);
        // add to panel
        goPanel.add(Box.createVerticalGlue());
        goPanel.add(goButton);
        goPanel.add(Box.createVerticalGlue());
        leftPanel.add(goPanel);
    }

    /**
     * Creates the Component that displays the current directory of selected files.
     */
    private void createDirField() {

        dirField = new JTextField(20);
        dirField.setEditable(false);
        rightPanel.add(dirField, BorderLayout.PAGE_START);

    }

    /**
     * Updates what's displayed in the directory field if the filepath has changed.
     */
    private void updateDirField() {

        rightPanel.remove(dirField);
        String dir = filePaths.length > 0 ? filePaths[0].getPath() : "";
        dirField = !dir.equals("") ?
                new JTextField(dir.substring(0, dir.lastIndexOf(File.separator))) : new JTextField();
        dirField.setEditable(false);
        rightPanel.add(dirField, BorderLayout.PAGE_START);
        rightPanel.revalidate();
        rightPanel.repaint();

    }

    /**
     * Updates the list of file names, used after the JScrollPane has been updated with new files.
     */
    private void updateFileNames() {
        fileNames = new String[filePaths.length];
        for (int i = 0; i < filePaths.length; i++)
            fileNames[i] = filePaths[i].getName();
    }

    /**
     * Creates the Component which displays the list of current files.
     */
    private void createScroller() {

        // Add the scroller to files panel
        JList fileList = new JList();
        fileList.setLayoutOrientation(JList.VERTICAL);
        listScroller = new JScrollPane(fileList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.add(listScroller, BorderLayout.CENTER);
    }

    /**
     * Updates the contents of the file scroller if the list of file has been updated.
     */
    private void updateScroller() {
        rightPanel.remove(listScroller);

        // selection model to disable selection in the JScrollPane
        class DisabledItemSelectionModel extends DefaultListSelectionModel {

            @Override
            public void setSelectionInterval(int index0, int index1) {
                super.setSelectionInterval(-1, -1);
            }
        }

        updateDirField();
        JList list = new JList(fileNames);
        list.setSelectionModel(new DisabledItemSelectionModel());
        listScroller = new JScrollPane(list);
        rightPanel.add(listScroller);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public static void main(String[] args) {
        new Window();
    }


}

