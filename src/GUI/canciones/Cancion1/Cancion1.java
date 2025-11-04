package GUI.canciones.Cancion1;

import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion1 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion1(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
    	
        mapeado = new Mapeado(this, resolucion,
            "GUI/canciones/Cancion1/soda stereo - la historia de soda estereo - de musica ligera.mp3",
            "/GUI/canciones/Cancion1/soda stereo - la historia de soda estereo - de musica ligera.json");
     // Después (no bloquea el EDT):
        new Thread(mapeado::iniciar, "inicio-cancion").start();

    }

    @Override
    protected void finalizarCancion() {
        if (mapeado != null) mapeado.detener();
        super.finalizarCancion();
    }
}
