import java.io.File;
import javax.swing.filechooser.*;

public class FiltroCartera extends FileFilter {
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String filename = f.getName();
        int dot = filename.lastIndexOf('.');
        String extension = filename.substring(dot + 1);
        
        if (extension != null) {
            if(extension.equals("car")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Ficheros de cartera (*.car)";
    } 
}