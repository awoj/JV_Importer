package csu.physics.pv;

import java.io.*;
import java.util.ArrayList;

class JVReader {

    private ArrayList<String> fileData;

    JVReader() {
        fileData = new ArrayList<>();
    }

    ArrayList<String> getFileData() {
        return fileData;
    }

    /*
    Reads files in, parses the contents of each as an array and adds it to fileData.
     */
    void read(File[] files) throws IOException {

        String line;
        StringBuilder sb;
        InputStream is;
        BufferedReader buf;

        File currFile;
        for (File file : files) {

            // open stream to current file
            currFile = file;
            is = new FileInputStream(currFile);
            buf = new BufferedReader(new InputStreamReader(is));

            // keep reading and appending until eof
            line = buf.readLine();
            sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append('\n');
                line = buf.readLine();
            }

            // add the contents of the file as a string to fileData.
            String fileAsString = sb.toString();
            fileData.add(fileAsString);
        }

    }

}
