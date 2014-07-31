package documentation.manager;

import java.io.File;

public class DelButtonCell extends ButtonCellBase {

    public DelButtonCell(String str) {
        
        super(str);
        
//        bt.disableProperty().set(true);
        
    }

    @Override
    public void process(DocEntry d) {

        File f = new File(d.getPath() + "/" + d.getName());

        f.delete();

    }
}