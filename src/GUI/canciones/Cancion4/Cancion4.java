package GUI.canciones.Cancion4;

import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion4 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion4(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
    	
        mapeado = new Mapeado(this, resolucion,
            "GUI/canciones/Cancion4/my-8-bit-hero-301280.mp3",
            "/GUI/canciones/Cancion4/my-8-bit-hero-301280.json");
     // Después (no bloquea el EDT):
        new Thread(mapeado::iniciar, "inicio-cancion").start();

    }

    @Override
    protected void finalizarCancion() {
        if (mapeado != null) mapeado.detener();
        super.finalizarCancion();
    }
}
