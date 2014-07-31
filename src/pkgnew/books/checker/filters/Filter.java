package pkgnew.books.checker.filters;

import java.io.File;

public interface Filter {
    
    boolean pass();
    
    void setFile(File f);
}
