package GUI.canciones.Cancion6;

import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion6 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion6(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
        mapeado = new Mapeado(this, resolucion,
            "GUI/canciones/Cancion6/audio.mp3",
            "/GUI/canciones/Cancion6/Artic Monkeys - 505 (aadurite).json");
        new Thread(mapeado::iniciar, "inicio-cancion").start();
    }

    @Override
    protected void finalizarCancion() {
        if (mapeado != null) mapeado.detener();
        super.finalizarCancion();
    }
}
