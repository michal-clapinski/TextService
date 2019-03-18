package rtb;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class FileTraverser {
    // is int big enough?
    public String getLine(int nr) throws Exception {
        Stream<String> lines = Files.lines(Paths.get("file.txt"));
        return lines.skip(nr - 1).findFirst().get();
    }
}
