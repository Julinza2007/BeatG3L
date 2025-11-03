package GUI.canciones.Cancion4;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

class Nota {
  String tipo; int columna; long tiempo; long duracion;
}

class Mapa { List<Nota> notas = new ArrayList<>(); }

public class DebugJSONHolds {
  public static void main(String[] args) throws Exception {
    String ruta = args.length>0 ? args[0] : "src/GUI/canciones/Cancion5/I-Dont-Wanna-Be-Me.json";
    try (Reader r = new FileReader(ruta)) {
      Mapa mapa = new Gson().fromJson(r, Mapa.class);
      List<Nota> holds = new ArrayList<>();
      for (Nota n : mapa.notas) if ("hold".equalsIgnoreCase(n.tipo)) holds.add(n);
      holds.sort(Comparator.comparingLong(n -> n.duracion));

      long min = holds.isEmpty()?0:holds.get(0).duracion;
      long max = holds.isEmpty()?0:holds.get(holds.size()-1).duracion;
      long p50 = percentile(holds, 0.50);
      long p90 = percentile(holds, 0.90);
      long p99 = percentile(holds, 0.99);

      System.out.printf("Holds: %d  | min=%dms p50=%dms p90=%dms p99=%dms max=%dms%n",
              holds.size(), min, p50, p90, p99, max);

      System.out.println("Top 20 holds más largos:");
      for (int i = Math.max(0, holds.size()-20); i < holds.size(); i++) {
        Nota h = holds.get(i);
        System.out.printf("col=%d inicio=%dms duran=%dms fin=%dms%n",
                h.columna, h.tiempo, h.duracion, h.tiempo + h.duracion);
      }
    }
  }
  static long percentile(List<Nota> hs, double p) {
    if (hs.isEmpty()) return 0;
    int idx = (int)Math.floor((hs.size()-1)*p);
    return hs.get(Math.max(0, Math.min(idx, hs.size()-1))).duracion;
  }
}
