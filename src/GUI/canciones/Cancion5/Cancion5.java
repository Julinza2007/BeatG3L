package GUI.canciones.Cancion5;

import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion5 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion5(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
        mapeado = new Mapeado(this, resolucion,
            "GUI/canciones/Cancion5/audio.mp3",
            "/GUI/canciones/Cancion5/I-Dont-Wanna-Be-Me.json");
        new Thread(mapeado::iniciar, "inicio-cancion").start();
    }

    @Override
    protected void finalizarCancion() {
        if (mapeado != null) mapeado.detener();
        super.finalizarCancion();
    }
}
