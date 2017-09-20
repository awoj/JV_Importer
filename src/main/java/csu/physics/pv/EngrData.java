package csu.physics.pv;

import java.math.BigDecimal;
import java.util.ArrayList;

import static java.util.Arrays.copyOfRange;

/* Format for ArrayList<ArrayList<Double>> params

Outer list: each element corresponds to a file
Inner list: each element corresponds to a parameter, defined by the order:

    [0] - cell area (cm^2)
    [1] - Jsc (mA/cm^2)
    [2] - Voc (mV)
    [3] - FF (%)
    [4] - Efficiency (%)
    [5] - Jmp (mA/cm^2)
    [6] - Vmp (mV)
*/


public class EngrData extends JVData {

    EngrData(ArrayList<String> data) {
        fileData = data;
    }

    @Override
    public boolean isValidData() {

        boolean isValid = false;

        for (String file : fileData) {
            if (file.contains("V(V)\tJ(A/cm^2)")) {
                isValid = true;
            } else {
                break;
            }
        }

        return isValid;
    }

    @Override
    public void extractData() {

        ArrayList<String> extractedCurves = new ArrayList<>();
        ArrayList<String> extractedParams = new ArrayList<>();

        // Do for each file
        for (int i = 0; i < fileNames.length; i++) {

            // Break the string into an array at each newline
            String[] lines;
            String str = fileData.get(i);
            lines = str.split("\n");

            String[] par, cur;
            int index = getIndex(lines);

            // Copy the sub-array of parameter and curve data
            par = copyOfRange(lines, 0, index);
            cur = copyOfRange(lines, index+1, lines.length);

            // Build light parameter data back into a string
            StringBuilder sb;

            sb = new StringBuilder();
            for (String aPar : par)
                sb.append(aPar).append('\n');
            extractedParams.add(sb.toString());
            lightFileNames.add(fileNames[i]);


            // Build curves data back into a string
            sb = new StringBuilder();
            for (String aCur : cur)
                sb.append(aCur).append('\n');
            extractedCurves.add(sb.toString());

        }

        curves = sortCurves(extractedCurves);
        params = sortParams(extractedParams);

    }

    private int getIndex(String[] lines) {

        String currLine;
        int index = 0;

        for (int i = 0; i < lines.length; i++) {

            currLine = lines[i];
            if (currLine.contains("V(V)\tJ(A/cm^2)")) {
                index = i;
            }

        }


        return index;
    }

    @Override
    public ArrayList<ArrayList<BigDecimal>> sortParams(ArrayList<String> p) {

        ArrayList<BigDecimal> currFile;
        ArrayList<ArrayList<BigDecimal>> sorted = new ArrayList<>();

        String Jsc, Voc, FF, eff, Jmp, Vmp, area;
        String [] lines;

        for (String aP : p) {

            currFile = new ArrayList<>();

            // split into lines
            lines = aP.split("\n");

            // area
            area = (lines[8]).substring(lines[8].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(area));

            // Jsc
            Jsc = (lines[6]).substring(lines[6].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(Jsc));

            // Voc
            Voc = (lines[4]).substring(lines[4].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(Voc));

            // fill factor
            FF = (lines[5]).substring(lines[5].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(FF));

            // efficiency
            eff = (lines[7]).substring(lines[7].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(eff));

            // Jmp
            Jmp = (lines[11]).substring(lines[11].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(Jmp));

            // Vmp
            Vmp = (lines[10]).substring(lines[10].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(Vmp));

            // add to final list
            sorted.add(currFile);
        }

        return sorted;
    }

    @Override
    public ArrayList<ArrayList<BigDecimal>> sortCurves(ArrayList<String> c) {

        ArrayList<BigDecimal> currFile;
        ArrayList<ArrayList<BigDecimal>> sorted = new ArrayList<>();

        String [] lines;
        String voltage, current;
        for (int i = 0; i < fileData.size(); i++) {

            currFile = new ArrayList<>();

            // split into lines
            lines = (c.get(i)).split("\n");

            for (String line : lines) {

                // get voltage from line
                voltage = line.substring(0, line.lastIndexOf('\t'));
                currFile.add(new BigDecimal(voltage));

                // get current from line
                current = line.substring(line.lastIndexOf('\t')+1);
                currFile.add(new BigDecimal(current).multiply(new BigDecimal(1000)));    // convert to mA/cm^2

            }

            // add to final list
            sorted.add(currFile);

        }

        return sorted;
    }

    @Override
    public ArrayList<String> makeParamsTable() {

        ArrayList<String> table = new ArrayList<>();

        // write the table header
        table.add("" + '\t' + "Area [cm^2]" + '\t'
                            + "Jsc [mA/cm^2]" + '\t'
                            + "Voc [mV]" + '\t'
                            + "FF [%]" + '\t'
                            + "Eff [%]" + '\t'
                            + "Jmp [mA/cm^2]" + '\t'
                            + "Vmp [mV]");

        // write the table lines
        ArrayList<BigDecimal> p;
        StringBuilder sb;
        for (int i = 0; i < params.size(); i++) { // file number

            // get the cell parameters
            p = params.get(i);

            // list cell name first
            sb = new StringBuilder();
            String fileName = lightFileNames.get(i);
            String name = fileName.substring(0, fileName.lastIndexOf(".txt"));
            sb.append(name).append('\t');

            // build a line of the parameters,
            for (BigDecimal aP : p)
                sb.append(aP.toString()).append('\t');

            // add to the final table
            table.add(sb.toString());
        }

        return table;

    }
}
