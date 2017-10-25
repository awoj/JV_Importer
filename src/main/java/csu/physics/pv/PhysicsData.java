package csu.physics.pv;

import java.math.BigDecimal;
import java.util.ArrayList;

import static java.util.Arrays.copyOfRange;

/* Format for ArrayList<ArrayList<Double>> params

Outer list: each element corresponds to a file
Inner list: each element corresponds to a parameter, defined by the order:

    [0] - cell area
    [1] - Jsc measured
    [2] - Voc measured
    [3] - FF
    [4] - eff
    [5] - Jsc fit
    [6] - Voc fit
*/

/**
 * Extension of {@link JVData} for the J-V data output created by the CSU physics lightbox in Jim Sites' PV lab.
 */
public class PhysicsData extends JVData {

    /**
     * Constructor
     *
     * @param data  A list of strings where each string contains the contents of one J-V file.
     */
    PhysicsData(ArrayList<String> data) {

        fileData = data;
    }

    /**
     * See {@link JVData#isValidData()} for more information.
     *
     * @return  Boolean indicating if the input is valid.
     */
    @Override
    public boolean isValidData() {

        boolean isValid = false;

        for (String file : fileData) {
            if (file.contains("Voltage [V]\tCurrent Density [mA/cm2]")) {
                isValid = true;
            } else {
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    /**
     * See {@link JVData#extractData()} for more information.
     */
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
            int index;
            boolean illumination;

            // Check for light or dark data and set the line number where curve data begin
            if (fileNames[i].contains("jvl")) {
                index = 12;
                illumination = true;
            } else {
                index = 6;
                illumination = false;
            }

            // Copy the sub-array of parameter and curve data
            par = copyOfRange(lines, 0, index);
            cur = copyOfRange(lines, index, lines.length);

            // Build light parameter data back into a string
            StringBuilder sb;
            if (illumination) {
                sb = new StringBuilder();
                for (String aPar : par)
                    sb.append(aPar).append('\n');
                extractedParams.add(sb.toString());
                lightFileNames.add(fileNames[i]);
            }

            // Build curves data back into a string
            sb = new StringBuilder();
            for (String aCur : cur)
                sb.append(aCur).append('\n');
            extractedCurves.add(sb.toString());

        }

        curves = sortCurves(extractedCurves);
        params = sortParams(extractedParams);

    }

    /**
     * See {@link JVData#sortParams(ArrayList)} for more information. <br>
     *
     * Structure of the parameter array for physics data: <br>
     *
     * [0] - cell area (cm^2) <br>
     * [1] - Jsc measured (mA/cm^2)<br>
     * [2] - Voc measured (V) <br>
     * [3] - FF (%) <br>
     * [4] - Efficiency (%) <br>
     * [5] - Jsc fit (mA/cm^2) <br>
     * [6] - Voc fit (mA/cm^2)
     */
    @Override
    public ArrayList<ArrayList<BigDecimal>> sortParams(ArrayList<String> p) {

        ArrayList<BigDecimal> currFile;
        ArrayList<ArrayList<BigDecimal>> sorted = new ArrayList<>();

        String Jsc_m, Voc_m, FF, eff, Jsc_f, Voc_f, area;
        String [] lines;

        for (String aP : p) {

            currFile = new ArrayList<>();

            // split into lines
            lines = aP.split("\n");

            // area
            area = (lines[2]).substring(lines[2].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(area));

            // measured Jsc
            Jsc_m = (lines[8]).substring(lines[8].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(Jsc_m));

            // measured Voc
            Voc_m = (lines[9]).substring(lines[9].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(Voc_m));

            // fill factor
            FF = (lines[6]).substring(lines[6].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(FF));

            // efficiency
            eff = (lines[7]).substring(lines[7].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(eff));

            // fit Jsc
            Jsc_f = (lines[4]).substring(lines[4].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(Jsc_f));

            // fit Voc
            Voc_f = (lines[5]).substring(lines[5].lastIndexOf('\t') + 1);
            currFile.add(new BigDecimal(Voc_f));

            // add to final list
            sorted.add(currFile);
        }

        return sorted;
    }

    /**
     * See {@link JVData#sortCurves(ArrayList)} for more information.
     */
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
                voltage = (line).substring(0, line.lastIndexOf('\t'));
                currFile.add(new BigDecimal(voltage));

                // get current from line
                current = (line).substring(line.lastIndexOf('\t')+1);
                currFile.add(new BigDecimal(current));

            }

            // add to final list
            sorted.add(currFile);

        }

        return sorted;
    }

    /**
     * See {@link JVData#makeParamsTable()} for more information.
     */
    @Override
    public ArrayList<String> makeParamsTable() {

        ArrayList<String> table = new ArrayList<>();

        // write the table header
        table.add("" + '\t' + "Area [cm^2]" + '\t'
                            + "Jsc_meas [mA/cm^2]" + '\t'
                            + "Voc_meas [V]" + '\t'
                            + "FF [%]" + '\t'
                            + "Eff [%]" + '\t'
                            + "Jsc_fit [mA/cm^2]" + '\t'
                            + "Voc_fit [V]");

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