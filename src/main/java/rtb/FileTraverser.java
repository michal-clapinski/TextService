package rtb;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class FileTraverser {
    private static final int line_diff = 256;
    private ArrayList<Long> line_positions;
    private String path;

    public FileTraverser(String path) throws Exception {
        this.path = path;
        line_positions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = null;
            long position = 0;
            for (long i = 0; (line = br.readLine()) != null; i++) {
                if (i % line_diff == 0) {
                    line_positions.add(position);
                }
                position += line.length() + 1;
            }
        }
    }

    public String getLine(long nr) throws Exception {
        int pos = Math.toIntExact((nr - 1) / line_diff);
        long lower_line = (long)pos * line_diff;
        long seek_to = line_positions.get(pos);

        try (FileInputStream fis = new FileInputStream(path)) {
            fis.getChannel().position(seek_to);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line = null;
            for (long i = lower_line; i != nr && (line = br.readLine()) != null; i++);
            if (line == null) {
                throw new Exception("Line number too big");
            }
            return line;
        }
    }
}
