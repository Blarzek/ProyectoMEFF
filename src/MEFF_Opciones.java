
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MEFF_Opciones {

    private final String servernameIBEXmini
            = "http://www.meff.es/aspx/Financiero/Ficha.aspx?ticker=FIEM";

    private final int ntrials = 5;
    private final int timeout = 10000; // 10 seconds

    public ArrayList<Opcion> listaOpciones = new ArrayList<>();
    public ArrayList<Opcion> listaCall = new ArrayList<>();
    public ArrayList<Opcion> listaPut = new ArrayList<>();

    public MEFF_Opciones() {
    }

    private Float toFloat(String texto) {
        texto = texto.replace(".", "");
        texto = texto.replace(",", ".");
        return Float.valueOf(texto);
    }

    private Integer toInteger(String texto) {
        texto = texto.replace(".", "");
        return Integer.valueOf(texto);
    }

    public boolean getOptions() {
        int trial = ntrials;
        while (trial > 0) {
            try {
                Document doc = Jsoup.connect(servernameIBEXmini).timeout(timeout).get();
                listaOpciones.clear();
                listaCall.clear();
                listaPut.clear();
                for (Element table : doc.getElementsByTag("table")) {
                    Elements rows = table.getElementsByTag("tr");
                    if (rows.size() > 0) {
                        String head = rows.get(0).text();
                        //System.out.println(head);
                        if (head.substring(0, 4).compareTo("CALL") == 0) {
                            for (int i = 3; i < rows.size() - 1; i++) {
                                Elements data = rows.get(i).getElementsByTag("td");
                                //System.out.println(data.size());
                                if (data.size() == 15) {
                                    //System.out.println(data.size() +  " " + data.get(7).text());
                                    Opcion CALL = new Opcion();
                                    CALL.Tipo = "CALL";
                                    Opcion PUT = new Opcion();
                                    PUT.Tipo = "PUT";

                                    CALL.Hora = data.get(0).text();
                                    CALL.Volumen = data.get(1).text();
                                    CALL.Ultimo = data.get(2).text();
                                    CALL.Compra_Vol = data.get(3).text();
                                    CALL.Compra_Precio = data.get(4).text();
                                    CALL.Venta_Precio = data.get(5).text();
                                    CALL.Venta_Vol = data.get(6).text();

                                    PUT.Hora = data.get(14).text();
                                    PUT.Volumen = data.get(13).text();
                                    PUT.Ultimo = data.get(12).text();
                                    PUT.Compra_Vol = data.get(8).text();
                                    PUT.Compra_Precio = data.get(9).text();
                                    PUT.Venta_Precio = data.get(10).text();
                                    PUT.Venta_Vol = data.get(11).text();

                                    // TO DO
                                    String texto = data.get(7).text();
                                    int pos = texto.indexOf("-");
                                    CALL.Vencimiento = texto.substring(0, pos - 1);
                                    CALL.Ejercicio = texto.substring(pos + 1);
                                    PUT.Vencimiento = CALL.Vencimiento;
                                    PUT.Ejercicio = CALL.Ejercicio;

                                    listaOpciones.add(CALL);
                                    listaCall.add(CALL);
                                    listaOpciones.add(PUT);
                                    listaPut.add(PUT);
                                    //System.out.println(data.get(12).text());
                                }
                            }
                            return true;
                        }
                    }
                }
                try {
                    //Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.sleep(3000);
                    trial--;
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MEFF_Opciones.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (IOException ex) {
                //Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex);   
                try {
                    //Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.sleep(3000);
                    trial--;
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MEFF_Opciones.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        return false;
    }
}
