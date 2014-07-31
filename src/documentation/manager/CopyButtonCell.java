package documentation.manager;

import documentationchecker.TextTransfer;

public class CopyButtonCell extends ButtonCellBase {

    public CopyButtonCell(String str) {
        super(str);
    }

    @Override
    public void process(DocEntry d) {
        new TextTransfer().setClipboardContents("cd " + d.getPath());
    }
}
