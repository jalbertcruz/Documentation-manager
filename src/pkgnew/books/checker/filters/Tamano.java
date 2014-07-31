package pkgnew.books.checker.filters;

import java.io.File;

public class Tamano implements Filter {

    @Override
    public boolean pass() {
        return file.length() > 30 * 1024;
    }
    File file;

    @Override
    public void setFile(File f) {
        file = f;
    }
}
