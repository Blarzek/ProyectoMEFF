
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class CellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Establecemos el fondo blanco o vacío
        setBackground(null);
        
        // Constructor de la clase DefaultTableCellRenderer
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Establecemos las filas que queremos cambiar el color. == 0 para pares y != 0 para impares
        boolean oddRow = (row % 2 == 0);

        // Creamos un color para las filas. El 200, 200, 200 en RGB es un color gris
        Color c = new Color(220, 220, 220);

        // Si las filas son pares, se cambia el color a gris
        if (!isSelected){
            if (oddRow) {
                setBackground(c);
            }
        }
        return this;
    }

}
