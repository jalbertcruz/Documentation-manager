package pkgnew.books.checker.filters;

import java.io.File;

public class Extensions implements Filter {

    String[] valid;

    public Extensions() {
        valid = new String[]{".pdf", ".doc", ".rtf", ".docx", ".ppt", ".pptx", ".zip", ".epub", ".prc", ".mobi", ".djvu", ".ps", ".rar", ".gz", ".chm", ".mp3", ".aac", ".wma"};
    }

    @Override
    public boolean pass() {
        boolean res = false;
        for (String s : valid) {
            if (file.getName().toLowerCase().endsWith(s)) {
                res = true;
            }
        }
        return res;
    }
    File file;

    @Override
    public void setFile(File f) {
        file = f;
    }
}
