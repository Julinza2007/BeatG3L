package GUI.canciones.Cancion8;

import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion8 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion8(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
    	
        mapeado = new Mapeado(this, resolucion,
            "GUI/canciones/Cancion8/audio.mp3",
            "/GUI/canciones/Cancion8/Travis Scott feat Kendrick Lamar - Goosebumps (Duparisto).json");
     // Después (no bloquea el EDT):
        new Thread(mapeado::iniciar, "inicio-cancion").start();

    }

    @Override
    protected void finalizarCancion() {
        if (mapeado != null) mapeado.detener();
        super.finalizarCancion();
    }
}
