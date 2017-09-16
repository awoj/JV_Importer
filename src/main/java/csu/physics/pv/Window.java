package csu.physics.pv;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Window extends JPanel {

    // Main frame
    private JFrame frame;
    private JPanel panel = new JPanel();

    // UI elements
    private JPanel leftPanel, rightPanel;
    private JButton selectButton, clearButton, goButton;
    private JScrollPane listScroller;
    private JTextField dirField;

    // File IO
    private JList fileList;
    private String [] fileNames;
    private File [] filePaths;
    private JVReader reader;
    private JVWriter writer;

    // format
    private String dataFormat = "physics";


    public Window() {

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
        leftPanel.setLayout(new GridLayout(3,0));
        panel.add(leftPanel);

        // make right panel and add
        rightPanel = new JPanel();
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightPanel.setLayout(new BorderLayout());
        panel.add(rightPanel);

        // Create the panels and components
        createFilesButtons();
        createFormatSelection();
        createGoButton();
        createDirField();
        createScroller();

        // Set window size and center in the screen
        frame.setSize(new Dimension(500, 350));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    /*
    Creates radio buttons for the input format (i.e. physics or engineering) and updates the dataFormat appropriately.
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
        };

        // create the buttons
        JRadioButton physButton = new JRadioButton("Physics");
        physButton.setBorder(new EmptyBorder(10,10,5,10));
        physButton.addActionListener(new SelectFormatListener());
        physButton.setActionCommand("physics");
        physButton.setSelected(true);

        JRadioButton engrButton = new JRadioButton("ERC");
        engrButton.setBorder(new EmptyBorder(0,10,0,10));
        engrButton.setActionCommand("engr");
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
        title = BorderFactory.createTitledBorder("Format");
        radioPanel.setBorder(title);

        // add the buttons
        radioPanel.add(physButton);
        radioPanel.add(engrButton);
        leftPanel.add(radioPanel);
    }

    /*
    Creates the "Select Files" and "Clear Files" button with listeners that update the JScrollPane.
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
        };

        class ClearFilesListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {

                fileNames = new String[0];
                filePaths = new File[0];
                updateScroller();
                updateDirField();

            }
        };

        // make a sub-panel
        JPanel filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));

        // Add title
        TitledBorder title = BorderFactory.createTitledBorder("J-V Data");
        filesPanel.setBorder(title);

        // create buttons
        selectButton = new JButton("Select Files");
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectButton.addActionListener(new SelectFilesListener());

        clearButton = new JButton("Clear Files");
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.addActionListener(new ClearFilesListener());

        // add to panel
        filesPanel.add(Box.createVerticalGlue());
        filesPanel.add(selectButton);
        filesPanel.add(clearButton);
        filesPanel.add(Box.createVerticalGlue());
        leftPanel.add(filesPanel);
    }

    /*
    Creates the "GO" button that triggers the data reading and writing procedures
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
                    //data = new EngrData(reader.getFileData());
                }

                if (data == null)
                    return;

                // pass file names and extract param and curves data
                data.setFileNames(fileNames);
                data.extractData();

                // write out
                writer = new JVWriter(data);
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
        };

        // make a sub-panel
        JPanel goPanel = new JPanel();
        goPanel.setLayout(new BoxLayout(goPanel, BoxLayout.Y_AXIS));
        goPanel.setBorder(new EmptyBorder(10,10,10,10));

        // Create button
        goButton = new JButton("Go!");
        goButton.addActionListener(new GoListener());
        //goButton.setMaximumSize(new Dimension(80,50));
        goButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // add to panel
        goPanel.add(Box.createVerticalGlue());
        goPanel.add(goButton);
        goPanel.add(Box.createVerticalGlue());
        leftPanel.add(goPanel);
    }

    private void createDirField() {

        dirField = new JTextField(20);
        dirField.setEditable(false);
        rightPanel.add(dirField, BorderLayout.PAGE_START);

    }

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

    private void createScroller() {

        // Add the scroller to files panel
        fileList = new JList();
        fileList.setLayoutOrientation(JList.VERTICAL);
        listScroller = new JScrollPane(fileList,
                                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.add(listScroller, BorderLayout.CENTER);
    }

    /*
    Updates fileNames, used after the JScrollPane has been updated with new file names.
     */
    private void updateFileNames() {
        fileNames = new String[filePaths.length];
        for (int i = 0; i < filePaths.length; i++)
            fileNames[i] = filePaths[i].getName();
    }

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

