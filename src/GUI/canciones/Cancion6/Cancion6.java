package GUI.canciones.Cancion6;

import GUI.dialogos7;
import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion6 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion6(ResolucionManager resolucion) {
        super(resolucion);
        this.numeroCancion = 6;
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
        
        /*
        //  Después de terminar la canción, cambiamos a la escena de diálogos
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame ventana = resolucion.getVentana(); 
            if (ventana != null) {
                ventana.getContentPane().removeAll();
                ventana.getContentPane().add(new dialogos7(ventana, resolucion));
                ventana.revalidate();
                ventana.repaint();
            } else {
                System.err.println(" No se encontró la ventana principal para mostrar los diálogos.");
            }
        });
        */
    }

    }

