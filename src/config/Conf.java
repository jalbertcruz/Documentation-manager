
package config;

import java.util.ArrayList;

public class Conf {
    
    ArrayList<String> dirs2check = new ArrayList<>();

    public ArrayList<String> getDirs2check() {
        return dirs2check;
    }

    public void setDirs2check(ArrayList<String> dirs2check) {
        this.dirs2check = dirs2check;
    }
    
}
