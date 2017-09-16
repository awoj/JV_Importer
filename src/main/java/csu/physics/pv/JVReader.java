package csu.physics.pv;

import java.io.*;
import java.util.ArrayList;

public class JVReader {

    ArrayList<String> fileData;

    JVReader() {
        fileData = new ArrayList<String>();
    }

    public ArrayList<String> getFileData() {
        return fileData;
    }

    /*
    Reads files in, parses the contents of each as an array and adds it to fileData.
     */
    public void read(File[] files) throws IOException {

        String line, currData;
        StringBuilder sb;
        InputStream is;
        BufferedReader buf;

        File currFile;
        for (int i = 0; i < files.length; i++) {

            // open stream to current file
            currFile = files[i];
            is = new FileInputStream(currFile);
            buf = new BufferedReader(new InputStreamReader(is));

            // keep reading and appending until eof
            line = buf.readLine();
            sb = new StringBuilder();
            while(line != null) {
                sb.append(line + '\n');
                line = buf.readLine();
            }

            // add the contents of the file as a string to fileData.
            String fileAsString = sb.toString();
            fileData.add(fileAsString);
        }

    }

}
