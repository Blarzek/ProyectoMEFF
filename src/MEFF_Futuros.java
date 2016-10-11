
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MEFF_Futuros {

    private final String servernameIBEXmini
            = "http://www.meff.es/aspx/Financiero/Ficha.aspx?ticker=FIEM";

    private final int ntrials = 5;
    private final int timeout = 10000; // 10 seconds

    public ArrayList<Futuro> Futuros = new ArrayList<>();

    public MEFF_Futuros() {
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

    public boolean getFutures() {
        int trial = ntrials;
        while (trial > 0) {
            try {
                Document doc = Jsoup.connect(servernameIBEXmini).timeout(timeout).get();
                Futuros.clear();
                for (Element table : doc.getElementsByTag("table")) {
                    Elements rows = table.getElementsByTag("tr");
                    if (rows.size() > 0) {
                        String head = rows.get(0).text();
                        if (head.substring(0, 11).compareTo("Vencimiento") == 0) {
                            for (int i = 1; i < rows.size() - 1; i++) {
                                Elements data = rows.get(i).getElementsByTag("td");
                                if (data.size() > 0) {
                                    Futuro f = new Futuro();
                                    f.Vencimiento = data.get(0).text();
                                    f.Compra_Vol = data.get(2).text();
                                    f.Compra_Precio = data.get(3).text();
                                    f.Venta_Precio = data.get(4).text();
                                    f.Venta_Vol = data.get(5).text();
                                    f.Ultimo = data.get(6).text();
                                    f.Volumen = data.get(7).text();
                                    f.Apertura = data.get(8).text();
                                    f.Maximo = data.get(9).text();
                                    f.Minimo = data.get(10).text();
                                    f.Anterior = data.get(11).text();
                                    f.Hora = data.get(12).text();
                                    Futuros.add(f);
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
                    Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (IOException ex) {
                //Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex);   
                try {
                    //Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.sleep(3000);
                    trial--;
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MEFF_Futuros.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        return false;
    }
}
