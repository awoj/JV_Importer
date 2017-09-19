package csu.physics.pv;

import java.util.ArrayList;

/*
Generic J-V data format intended to be extended by individual formats: PhysicsData, EngrData...
 */
public class JVData implements IData {

    ArrayList<String> fileData;         // input file data, each string is the contents of one file
    String [] fileNames;
    ArrayList<String> lightFileNames;   // file names of light data only, used for printing params table
    ArrayList<ArrayList<Double>> params;
    ArrayList<ArrayList<Double>> curves;


    JVData() {
        fileData = new ArrayList<>();
        fileNames = new String[0];
        lightFileNames = new ArrayList<>();
        params = new ArrayList<>();
        curves = new ArrayList<>();
    }


    public void setFileNames(String [] names) {
        fileNames = names;
    }

    public String [] getFileNames() {
        return fileNames;
    }

    public int getNumFiles() {
        return fileData.size();
    }

    public ArrayList<ArrayList<Double>> getCurves() {
        return curves;
    }

    public void extractData() { }

    public ArrayList<ArrayList<Double>> sortParams(ArrayList<String> p) {
        return null;
    }

    public ArrayList<ArrayList<Double>> sortCurves(ArrayList<String> c) {
        return null;
    }

    public ArrayList<String> makeParamsTable() {
        return null;
    }
}
