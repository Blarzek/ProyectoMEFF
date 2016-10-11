
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


public class Cartera extends javax.swing.JPanel {

    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tablaCartera;
    private final String nombre;
    private final String ubicacion;
    private int ultimaFila;
    private boolean cambiosRealizados;
    private final MEFF_Opciones opciones = new MEFF_Opciones();
    
    public Cartera(String n, String u) {
        nombre = n;
        ubicacion = u;
        ultimaFila = 0;
        cambiosRealizados = false;
        initComponents();
    }
    
    private void initComponents() {
        scroll = new javax.swing.JScrollPane();
        tablaCartera = new javax.swing.JTable();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("N. opciones");
        model.addColumn("Tipo");
        model.addColumn("Fecha vencim.");
        model.addColumn("P. Ejercicio");
        model.addColumn("Fecha incor.");
        model.addColumn("Importe");
        model.addColumn("P. Compra actual");
        model.addColumn("Ganancia");
        tablaCartera.setModel(model);
        
        tablaCartera.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaCartera.getColumnModel().getColumn(1).setPreferredWidth(40);
        
        setCellRender(tablaCartera);
        tablaCartera.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(scroll);
        scroll.setViewportView(tablaCartera);
        
        javax.swing.GroupLayout carteraLayout = new javax.swing.GroupLayout(this);
        this.setLayout(carteraLayout);
        carteraLayout.setHorizontalGroup(carteraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, carteraLayout.createSequentialGroup()
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE))
        );
        carteraLayout.setVerticalGroup(carteraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, carteraLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(carteraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)))
        );
        
    }
    
    // Añade la línea a la tabla
    public void addRow(String line){
        // Comprobamos campos y los vamos introduciendo en la tabla
        String [] campos = line.split(" ");
        DefaultTableModel model = (DefaultTableModel) tablaCartera.getModel();
        model.addRow(campos);
        ultimaFila++;
    }
    
    // Elimina la fila indicada de la tabla
    public void removeRow(int indice){
        DefaultTableModel model = (DefaultTableModel) tablaCartera.getModel();
        model.removeRow(indice);
        ultimaFila--;
    }
    
    // Devuelve la fila seleccionada de la tabla
    public int getSelectedRow(){
        return tablaCartera.getSelectedRow();
    }
    
    // Designar el CellRenderer a cada una de las columnas de la tabla
    public void setCellRender(JTable table) {
        Enumeration<TableColumn> en = table.getColumnModel().getColumns();
        while (en.hasMoreElements()) {
            TableColumn tc = en.nextElement();
            tc.setCellRenderer(new CellRenderer());
        }
    }
    
    // Devuelve la primera fila de la tabla en la que no hay datos
    public int getLastRow(){
        return ultimaFila;
    }
    
    // Devuelve el nombre de la cartera
    public String getWalletName(){
        return nombre;
    }
    
    // Guarda los cambios de la cartera en el fichero
    public void save() {
        int row = tablaCartera.getRowCount();
        String line = "";
        PrintWriter writer;
        try {
            // Creamos un nuevo fichero y escribimos el nombre
            writer = new PrintWriter(ubicacion, "UTF-8");
            writer.println(nombre);
            // Leemos toda la tabla
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < 6; j++) {
                    line = line + tablaCartera.getValueAt(i, j) + " ";
                }
                // Una vez haya leido toda la fila, la guardamos en el fichero
                writer.println(line);
                line = "";
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Comprueba si se han producido cambios en la cartera
    public boolean checkChanges(){
        return cambiosRealizados;
    }
    
    // Establece que se han producido cambios en la cartera
    public void setChanges(boolean b){
        cambiosRealizados = b;
    }
    
    // Comprueba en qué fila de la tabla está la opción pasada
    public int include(Opcion opcion){
        for (int i = 0; i < tablaCartera.getRowCount(); i++) {
            // Comprobamos si la opcion ya está dentro de la cartera
            if (opcion.Ejercicio.trim().equals(tablaCartera.getValueAt(i, 3))){
                if (opcion.Vencimiento.replaceAll(" ", "-").equals(tablaCartera.getValueAt(i, 2))){
                    if (opcion.Tipo.equals(tablaCartera.getValueAt(i, 1))){
                        return i;
                    }
                }
            }
        }
        // No se encontró dentro de la cartera
        return -1;
    }
    
    // Incrementa la cantidad de opciones de ese tipo de una fila dada
    public void increaseCount(int row){
        int num = Integer.parseInt((String)tablaCartera.getValueAt(row, 0));
        num++;
        String dato = Integer.toString(num);
        tablaCartera.setValueAt(dato, row, 0);
    }
    
    // Comprueba si la fecha pasada es anterior o posterior a la actual
    private boolean checkDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String current = dateFormat.format(currentDate);
        return sortDate(date).compareTo(current) < 0;
    }
    
    // Borra las filas de la tabla cuya fecha de vencimiento ha caducado e informa de ello
    public boolean deleteOldDates(){
        for (int i = 0; i < tablaCartera.getRowCount(); i++) {
            if (checkDate((String)tablaCartera.getValueAt(i, 2))){
                removeRow(i);
                return true;
            }
        }
        return false;
    }
    
    // Dada una fecha en formato "DD-MMM-AAAA" nos la ordena a formato "AAAA-MM-DD"
    private String sortDate(String date) {
        String mes = date.split("-")[1];
        switch (mes) {
            case "ene":
                mes = "01";
                break;
            case "feb":
                mes = "02";
                break;
            case "mar":
                mes = "03";
                break;
            case "abr":
                mes = "04";
                break;
            case "may":
                mes = "05";
                break;
            case "jun":
                mes = "06";
                break;
            case "jul":
                mes = "07";
                break;
            case "ago":
                mes = "08";
                break;
            case "sep":
                mes = "09";
                break;
            case "oct":
                mes = "10";
                break;
            case "nov":
                mes = "11";
                break;
            case "dic":
                mes = "12";
                break;
            default:
                break;
        }
        String fecha = date.split("-")[2] + "-" + mes + "-" + date.split("-")[0];
        return fecha;
    }
    
    public void calculate(JLabel labelImporte, JLabel labelValoracion, JLabel labelGanancia){
        // Calculamos los beneficios individuales para cada opción
        for (int i = 0; i < tablaCartera.getRowCount(); i++) {
            float precioCompra = Float.parseFloat(((String)tablaCartera.getValueAt(i, 5)).replace(".", "").replace(",", "."));
            opciones.getOptions();
            if (((String)tablaCartera.getValueAt(i, 1)).equals("PUT")){
                for (Opcion opcionPut : opciones.listaPut) {
                    if (opcionPut.Ejercicio.trim().equals(tablaCartera.getValueAt(i, 3))){
                        if (opcionPut.Vencimiento.replaceAll(" ", "-").equals(tablaCartera.getValueAt(i, 2))){
                            float precioActual = Float.parseFloat((opcionPut.Compra_Precio).replace(".", "").replace(",", "."));
                            String actual = precioActual + "";
                            tablaCartera.setValueAt(actual, i, 6);
                            float beneficio = precioActual - precioCompra;
                            String dato = beneficio + "";
                            
                            if (beneficio > 0) {
                                tablaCartera.setValueAt("<html><font color='green'>" + dato + "</font></html>", i, 7);
                            } else if (beneficio < 0) {
                                tablaCartera.setValueAt("<html><font color='red'>" + dato + "</font></html>", i, 7);
                            } else {
                                tablaCartera.setValueAt("<html><font color='black'>" + dato + "</font></html>", i, 7);
                            }
                        }
                    }
                }
            } else {
                if (((String)tablaCartera.getValueAt(i, 1)).equals("CALL")){
                    for (Opcion opcionCall : opciones.listaCall) {
                        if (opcionCall.Ejercicio.trim().equals(tablaCartera.getValueAt(i, 3))){
                            if (opcionCall.Vencimiento.replaceAll(" ", "-").equals(tablaCartera.getValueAt(i, 2))){
                                float precioActual = Float.parseFloat((opcionCall.Compra_Precio).replace(".", "").replace(",", "."));
                                String actual = precioActual + "";
                                tablaCartera.setValueAt(actual, i, 6);
                                float beneficio = precioActual - precioCompra;
                                String dato = beneficio + "";
                                
                                // Coloreamos el texto dependiendo si el beneficio es positivo o negativo
                                if (beneficio > 0) {
                                    tablaCartera.setValueAt("<html><font color='green'>" + dato + "</font></html>", i, 7);
                                } else if (beneficio < 0) {
                                    tablaCartera.setValueAt("<html><font color='red'>" + dato + "</font></html>", i, 7);
                                } else {
                                    tablaCartera.setValueAt("<html><font color='black'>" + dato + "</font></html>", i, 7);
                                }
                            }
                        }
                    }
                }
            }
        }
        // Calculamos el importe invertido, la valoración total de la cartera y la ganancia total
        float invertido = 0;
        float valoracion = 0;
        float ganancia = 0;
        for (int i = 0; i < tablaCartera.getRowCount(); i++) {
            // Importe invertido en la adquisición de la cartera que será la suma de los precios de compra multiplicado respectivamente por el número de opciones
            invertido = invertido + (Float.parseFloat(((String)tablaCartera.getValueAt(i, 5)).replace(".", "").replace(",", ".")) * Float.parseFloat((String)tablaCartera.getValueAt(i, 0)));
            
            if (tablaCartera.getValueAt(i, 6) != null){
                // Valoración actual de toda la cartera
                valoracion = valoracion + Float.parseFloat((String)tablaCartera.getValueAt(i, 6));
            } else {
                tablaCartera.setValueAt("<html><font color='blue'>Error al buscar esta opción</font></html>", i, 6);
            }
            // Ganancia total
            ganancia = valoracion - invertido;
        }
        // Escribimos en las etiquetas los valores obtenidos
        labelImporte.setText(Float.toString(invertido) + " €");
        labelValoracion.setText(Float.toString(valoracion) + " €");
        
        // Coloreamos el texto dependiendo si el beneficio es positivo o negativo
        if (ganancia > 0) {
            labelGanancia.setText("<html><font color='green'><b>" + Float.toString(ganancia) + " €</b></font></html>");
        } else if (ganancia < 0) {
            labelGanancia.setText("<html><font color='red'><b>" + Float.toString(ganancia) + " €</b></font></html>");
        } else {
            labelGanancia.setText("<html><font color='black'><b>" + Float.toString(ganancia) + " €</b></font></html>");
        }
    }
}
