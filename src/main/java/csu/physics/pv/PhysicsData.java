package csu.physics.pv;

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


public class PhysicsData extends JVData {

    PhysicsData() {
       // do nothing
    }

    PhysicsData(ArrayList<String> data) {
        fileData = data;
    }

    @Override
    public void extractData() {

        ArrayList<String> extractedCurves = new ArrayList<String>();
        ArrayList<String> extractedParams = new ArrayList<String>();

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
                for (int k = 0; k < par.length; k++)
                    sb.append(par[k] + '\n');
                extractedParams.add(sb.toString());
                lightFileNames.add(fileNames[i]);
            }

            // Build curves data back into a string
            sb = new StringBuilder();
            for (int k = 0; k < cur.length; k++)
                sb.append(cur[k] + '\n');
            extractedCurves.add(sb.toString());

        }

        curves = sortCurves(extractedCurves);
        params = sortParams(extractedParams);

    }

    @Override
    public ArrayList<ArrayList<Double>> sortParams(ArrayList<String> p) {

        ArrayList<Double> currFile;
        ArrayList<ArrayList<Double>> sorted = new ArrayList<ArrayList<Double>>();

        String Jsc_m, Voc_m, FF, eff, Jsc_f, Voc_f, area;
        String [] lines;

        for (int i = 0; i < p.size(); i++) {

                currFile = new ArrayList<Double>();

                // split into lines
                lines = (p.get(i)).split("\n");

                // area
                area = (lines[2]).substring(lines[2].lastIndexOf('\t') + 1);
                currFile.add(Double.parseDouble(area));

                // measured Jsc
                Jsc_m = (lines[8]).substring(lines[8].lastIndexOf('\t') + 1);
                currFile.add(Double.parseDouble(Jsc_m));

                // measured Voc
                Voc_m = (lines[9]).substring(lines[9].lastIndexOf('\t') + 1);
                currFile.add(Double.parseDouble(Voc_m));

                // fill factor
                FF = (lines[6]).substring(lines[6].lastIndexOf('\t') + 1);
                currFile.add(Double.parseDouble(FF));

                // efficiency
                eff = (lines[7]).substring(lines[7].lastIndexOf('\t') + 1);
                currFile.add(Double.parseDouble(eff));

                // fit Jsc
                Jsc_f = (lines[4]).substring(lines[4].lastIndexOf('\t') + 1);
                currFile.add(Double.parseDouble(Jsc_f));

                // fit Voc
                Voc_f = (lines[5]).substring(lines[5].lastIndexOf('\t') + 1);
                currFile.add(Double.parseDouble(Voc_f));

                // add to final list
                sorted.add(currFile);
            }

        return sorted;
    }

    @Override
    public ArrayList<ArrayList<Double>> sortCurves(ArrayList<String> c) {

        ArrayList<Double> currFile;
        ArrayList<ArrayList<Double>> sorted = new ArrayList<ArrayList<Double>>();

        String [] lines;
        String voltage, current;
        for (int i = 0; i < fileData.size(); i++) {

            currFile = new ArrayList<Double>();

            // split into lines
            lines = (c.get(i)).split("\n");

            for (int j = 0; j < lines.length; j++) {

                // get voltage from line
                voltage = (lines[j]).substring(0, lines[j].lastIndexOf('\t')-1);
                currFile.add(Double.parseDouble(voltage));

                // get current from line
                current = (lines[j]).substring(lines[j].lastIndexOf('\t')+1);
                currFile.add(Double.parseDouble(current));

            }

            // add to final list
            sorted.add(currFile);

        }

        return sorted;
    }

    @Override
    public ArrayList<String> makeParamsTable() {

        ArrayList<String> table = new ArrayList<String>();

        // write the table header
        table.add("" + '\t' + "Area [cm^2]" + '\t'
                            + "Jsc_meas [mA/cm^2]" + '\t'
                            + "Voc_meas [V]" + '\t'
                            + "FF [%]" + '\t'
                            + "Eff [%]" + '\t'
                            + "Jsc_fit [mA/cm^2]" + '\t'
                            + "Voc_fit [V]");

        // write the table lines
        ArrayList<Double> p;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) { // file number

            // get the cell parameters
            p = params.get(i);

            // list cell name first
            sb = new StringBuilder();
            String fileName = lightFileNames.get(i);
            String name = fileName.substring(0, fileName.lastIndexOf(".txt"));
            sb.append(name + '\t');

            // build a line of the parameters,
            for (int k = 0; k < p.size(); k++)
                sb.append(p.get(k).toString() + '\t');

            // add to the final table
            table.add(sb.toString());
        }

        return table;

    }
}
