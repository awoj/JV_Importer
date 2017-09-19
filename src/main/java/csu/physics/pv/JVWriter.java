package csu.physics.pv;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class JVWriter {

    private JVData data;

    JVWriter(JVData input) {
        data = input;
    }

    /*
    Writes sorted data to file by prompting for save location. Returns a boolean to indicate if it wrote successfully.
     */

    boolean write() {


        boolean writeSuccess = false;
        ArrayList<String> lines = new ArrayList<>();

        // Write the file names first
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.getNumFiles(); i++) {
            sb.append((data.getFileNames())[i].substring(0, data.getFileNames()[i].lastIndexOf(".txt"))).append("_X").append('\t');
            sb.append((data.getFileNames())[i].substring(0, data.getFileNames()[i].lastIndexOf(".txt"))).append("_Y").append('\t');
        }
        lines.add(sb.toString());

        // figure out which file has most J-V points (ie. max num of lines to print)
        int biggest = 0;
        for (ArrayList<Double> file : data.getCurves()) {
            if (file.size() >= biggest)
                biggest = file.size();
        }

        // write the curve columns
        ArrayList<Double> currData;
        String voltage, current;
        for (int i = 0; i < biggest/2; i++) { // line number

            sb = new StringBuilder();
            for (int j = 0; j < data.getNumFiles(); j++) { // file number

                // get the curve data
                currData = data.getCurves().get(j);

                // write the voltage and current, tab separated
                voltage = i < currData.size()/2 ? currData.get(2*i).toString() : "";
                current = i < currData.size()/2 ? currData.get(2*i+1).toString() : "";
                sb.append(voltage).append('\t').append(current).append('\t');

            }

            lines.add(sb.toString());
        }

        lines.add("\n");

        // write the params table
        ArrayList<String> paramsTable = data.makeParamsTable();
        lines.addAll(paramsTable);

        // write everything out to a string
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
