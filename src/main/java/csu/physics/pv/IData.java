package csu.physics.pv;

import java.util.ArrayList;

/*
Interface for J-V data types
 */
interface IData {

    void setFileNames(String [] names);

    String [] getFileNames();

    int getNumFiles();

    void extractData();

    ArrayList<ArrayList<Double>> getParams();

    ArrayList<ArrayList<Double>> getCurves();

    ArrayList<ArrayList<Double>> sortParams(ArrayList<String> p);

    ArrayList<ArrayList<Double>> sortCurves(ArrayList<String> c);

    ArrayList<String> makeParamsTable();

}
