
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MEFF_Contado {

    private final String servernameIBEX
            = "http://www.meff.es/aspx/Financiero/Ficha.aspx?ticker=FIE";

    private final int ntrials = 5;
    private final int timeout = 1000; // 10 seconds

    public String Spot;
    public String Diferencia;
    public String Anterior;
    public String Maximo;
    public String Minimo;
    public String Fecha;
    public String Hora;

    public MEFF_Contado() {
    }

    private Float toFloat(String texto) {
        texto = texto.replace(".", "");
        texto = texto.replace(",", ".");
        return Float.valueOf(texto);
    }

    public boolean getSpot() {
        int trial = ntrials;
        while (trial > 0) {
            try {
                Document doc = Jsoup.connect(servernameIBEX).timeout(timeout).get();

                for (Element table : doc.getElementsByTag("table")) {
                    Elements rows = table.getElementsByTag("tr");
                    if (rows.size() > 0) {
                        String head = rows.get(0).text();
                        if (head.substring(0, 4).compareTo("Últ.") == 0) {
                            Elements data = rows.get(1).getElementsByTag("td");
                            Spot = data.get(0).text();
                            Diferencia = data.get(2).text();
                            Maximo = data.get(4).text();
                            Minimo = data.get(5).text();
                            Anterior = data.get(6).text();
                            Fecha = data.get(7).text();
                            Hora = data.get(8).text();
                            return true;
                        }
                    }
                }
                try {
                    //Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.sleep(3000);
                    trial--;
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MEFF_Contado.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (IOException ex) {
                try {
                    //Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.sleep(3000);
                    trial--;
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MEFF_Contado.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        return false;
    }
}
