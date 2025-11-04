package GUI.canciones.Cancion9;

import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion9 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion9(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
        mapeado = new Mapeado(this, resolucion,
            "GUI/canciones/Cancion9/audio.mp3",
            "/GUI/canciones/Cancion9/Evanescence - bring me to life (soap osu).json");
        new Thread(mapeado::iniciar, "inicio-cancion").start();
    }

    @Override
    protected void finalizarCancion() {
        if (mapeado != null) mapeado.detener();
        super.finalizarCancion();
    }
}