package documentation.manager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenFileButtonCell extends ButtonCellBase {

    public OpenFileButtonCell(String str) {
        super(str);
    }

    @Override
    public void process(DocEntry d) {
        try {
            
            String f = "exec.bat";
            
            try (PrintWriter pw = new PrintWriter(new File(f))) {
                
                pw.write("\"" + d.getPath() + "/" + d.getName() + "\"");
                
            }
            
            ProcessBuilder pb = new ProcessBuilder(f);
            
            pb.start();
            
            
        } catch (IOException ex) {
            Logger.getLogger(OpenFileButtonCell.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
