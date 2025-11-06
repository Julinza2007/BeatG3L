package GUI.canciones.Cancion7;

import GUI.dialogos8;
import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion7 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion7(ResolucionManager resolucion) {
        super(resolucion);
        this.numeroCancion = 7;
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
    	
        mapeado = new Mapeado(this, resolucion,
            "GUI/canciones/Cancion7/audio.mp3",
            "/GUI/canciones/Cancion7/Pierce The Veil - A Match Into Water (puxtu).json");
     // Después (no bloquea el EDT):
        new Thread(mapeado::iniciar, "inicio-cancion").start();

    }

    @Override
    protected void finalizarCancion() {
        if (mapeado != null) mapeado.detener();
        super.finalizarCancion();
        
        
        /*
        //  Después de terminar la canción, cambiamos a la escena de diálogos
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame ventana = resolucion.getVentana(); 
            if (ventana != null) {
                ventana.getContentPane().removeAll();
                ventana.getContentPane().add(new dialogos8(ventana, resolucion));
                ventana.revalidate();
                ventana.repaint();
            } else {
                System.err.println(" No se encontró la ventana principal para mostrar los diálogos.");
            }
        });
        */
    }

    }

