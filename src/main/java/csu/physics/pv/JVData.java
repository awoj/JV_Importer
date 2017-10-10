package csu.physics.pv;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A generic J-V data format intended to be extended by individual formats: PhysicsData, EngrData...
 */

public class JVData {

    protected ArrayList<String> fileData;         // input file data, each string is the contents of one file
    protected String [] fileNames;                // list of all file names
    protected ArrayList<String> lightFileNames;   // file names of light data only, used for printing params table

    protected ArrayList<ArrayList<BigDecimal>> curves;    // array of raw curves data, each array corresponding to one file
    protected ArrayList<ArrayList<BigDecimal>> params;    // arrays of parameter numbers, each array corresponding to one file

    /**
     * Constructor.
     */
    JVData() {
        fileData = new ArrayList<>();
        fileNames = new String[0];
        lightFileNames = new ArrayList<>();
        params = new ArrayList<>();
        curves = new ArrayList<>();
    }

    /**
     * Sets the file names.
     * @param names The file names to set.
     */
    public void setFileNames(String [] names) {
        fileNames = names;
    }

    /**
     * Returns the file names.
     * @return  The file names as an array.
     */
    public String [] getFileNames() {
        return fileNames;
    }

    /**
     * Returns the number of files.
     * @return  The number of files.
     */
    public int getNumFiles() {
        return fileData.size();
    }

    /**
     * Returns the raw curve data as an array of arrays. Each array corresponds to the data of one file of the same
     * index number as in {@link csu.physics.pv.JVData#fileNames}.
     *
     * @return  The array of arrays containing J-V data for all files.
     */
    public ArrayList<ArrayList<BigDecimal>> getCurves() {
        return curves;
    }

    /**
     * Verifies if data is of the appropriate type by looking for certain keywords near the beginning of file. Method is
     * intended to be implemented by sub-classes.
     *
     * @return Boolean indicating if the data is valid.
     */
    public boolean isValidData() { return false; }

    /**
     * Breaks the file contents into header and raw J-V data, setting them to the {@link JVData#params}
     * and {@link JVData#curves} members respectively. Inteded to be implemented by sub-classes.
     */
    public void extractData() { }

    /**
     * Method called by {@link JVData#extractData()}. Takes input of a string array where each string represents the
     * header of one file. Extracts numerical parameters from the header string and places them in a BigDouble array,
     * where each array represents the data of one file. Intended to be implemented by subclasses, where parameter
     * order/meaning is determined by the subclass.
     *
     * @param p The array of strings containing parameter data.
     * @return  An array of BigDouble arrays where each array corresponds to the data of one file.
     */
    public ArrayList<ArrayList<BigDecimal>> sortParams(ArrayList<String> p) {
        return null;
    }

    /**
     * Method called by {@link JVData#extractData()}. Takes input of a string array where each string represents the
     * J-V curve data of one file. Extracts voltage and current from the strings and places them in a BigDouble array,
     * where each array represents the data of one file. Intended to be implemented by sub-classes.
     *
     * @param c The array of strings containing J-V curve data.
     * @return  An array of BigDouble arrays where each arra
     */
    public ArrayList<ArrayList<BigDecimal>> sortCurves(ArrayList<String> c) {
        return null;
    }

    /**
     * Writes the parameter table that is printed to file. Intended to be implemented by sub-classes.
     *
     * @return  String containing formatted data to be printed to file.
     */
    public ArrayList<String> makeParamsTable() {
        return null;
    }


}
