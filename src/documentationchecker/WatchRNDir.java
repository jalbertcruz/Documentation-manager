package documentationchecker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.*;

public class WatchRNDir extends Thread {

    ActionListener actionListener;
    Path path;

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public Path getPath() {
        return path;
    }

    public WatchRNDir(Path path) {
        this.path = path;
    }

    @Override
    public void run() {
        try {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

                //start an infinite loop
                while (true) {

                    //retrieve and remove the next watch key
                    final WatchKey key = watchService.take();

                    //get list of pending events for the watch key
                    for (WatchEvent<?> watchEvent : key.pollEvents()) {

                        //get the kind of event (create, modify, delete)
                        final Kind<?> kind = watchEvent.kind();

                        //handle OVERFLOW event
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        //get the filename for the event
                        final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                        final Path filename = watchEventPath.context();

                        if (actionListener != null) {
                            actionListener.actionPerformed(new ActionEvent(this, 0, kind + " -> " + filename));
                        }
                        //print it out
//                        System.out.println(kind + " -> " + filename);
                    }

                    //reset the key
                    boolean valid = key.reset();

                    //exit loop if the key is not valid (if the directory was deleted, per example)
                    if (!valid) {
                        break;
                    }

                }
            }

        } catch (IOException | InterruptedException ex) {
//            Logger.getLogger(DisplayFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
