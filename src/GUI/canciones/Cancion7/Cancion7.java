package GUI.canciones.Cancion7;

import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion7 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion7(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
    	
        mapeado = new Mapeado(this, resolucion,
            "GUI/canciones/Cancion7/audio.mp3",
            "/GUI/canciones/Cancion7/30 Seconds to Mars - The Kill (Bury Me) (Plutes).json");
     // Después (no bloquea el EDT):
        new Thread(mapeado::iniciar, "inicio-cancion").start();

    }

    @Override
    protected void finalizarCancion() {
        if (mapeado != null) mapeado.detener();
        super.finalizarCancion();
    }
}
