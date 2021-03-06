package csu.physics.pv;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *  Takes an input {@link JVData} object and converts its contents into a string that is written to file.
 */
class JVWriter {

    private JVData data;
    private ArrayList<Boolean> printOptions;

    /**
     * Constructor.
     *
     * @param input {@link JVData} object to use.
     */
    JVWriter(JVData input) {
        data = input;
    }

    /**
     * Sets the print options as an array of booleans. More print options can be added in the future. The current
     * structure of the array is:
     *
     * [0] - print curves?
     * [1] - print parameters?
     *
     * @param options   The list of boolean flags to set the file printing options.
     */
    void setPrintOptions(ArrayList<Boolean> options) {

        printOptions = options;

    }

    /**
     * Writes sorted data to file according to the print options set. Prompts the user for a save location, and returns
     * a boolean indicating if the write process was successful.
     *
     * @return  Boolean indicating if the write was successful.
     */
    boolean write() {

        boolean writeSuccess = false;
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        // if curves is enabled for printing, write it
        if (printOptions != null && printOptions.size() > 0
                && printOptions.get(0)) {

            // write the file names first
            for (int i = 0; i < data.getNumFiles(); i++) {
                sb.append((data.getFileNames())[i].substring(0, data.getFileNames()[i].lastIndexOf(".txt"))).append("_X").append('\t');
                sb.append((data.getFileNames())[i].substring(0, data.getFileNames()[i].lastIndexOf(".txt"))).append("_Y").append('\t');
            }
            lines.add(sb.toString());

            // figure out which file has most J-V points (ie. max num of lines to print)
            int biggest = 0;
            for (ArrayList<BigDecimal> file : data.getCurves()) {
                if (file.size() >= biggest)
                    biggest = file.size();
            }

            // write the curves data
            ArrayList<BigDecimal> currData;
            String voltage, current;
            for (int i = 0; i < biggest / 2; i++) { // line number

                sb = new StringBuilder();
                for (int j = 0; j < data.getNumFiles(); j++) { // file number

                    // get the curve data
                    currData = data.getCurves().get(j);

                    // write the voltage and current, tab separated
                    voltage = i < currData.size() / 2 ? currData.get(2 * i).toString() : "";
                    current = i < currData.size() / 2 ? currData.get(2 * i + 1).toString() : "";
                    sb.append(voltage).append('\t').append(current).append('\t');

                }

                lines.add(sb.toString());
            }

            lines.add("\n");
        }


        // if parameters is enable for printing, write it
        if (printOptions != null && printOptions.size() > 1
                && printOptions.get(1)) {
            ArrayList<String> paramsTable = data.makeParamsTable();
            lines.addAll(paramsTable);
        }

        // finally, write everything out to a string
        StringBuilder output = new StringBuilder();
        for (String line : lines)
            output.append(line).append('\n');

        // prompt for save location
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            File fileWithExt = new File(file.getPath() + ".txt");

            // try writing data out
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(fileWithExt));
                out.write(output.toString());
                out.close();
                writeSuccess = true;
            } catch (IOException e) {
                writeSuccess = false;
            }

        }

        // return the results
        return writeSuccess;

    }
}
