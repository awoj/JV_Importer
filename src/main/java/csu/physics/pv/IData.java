package csu.physics.pv;

import java.math.BigDecimal;
import java.util.ArrayList;

/*
Interface for J-V data types
 */
interface IData {

    void setFileNames(String [] names);

    String [] getFileNames();

    int getNumFiles();

    void extractData();

    ArrayList<ArrayList<BigDecimal>> getCurves();

    ArrayList<ArrayList<BigDecimal>> sortParams(ArrayList<String> p);

    ArrayList<ArrayList<BigDecimal>> sortCurves(ArrayList<String> c);

    ArrayList<String> makeParamsTable();

}
