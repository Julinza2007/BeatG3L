package GUI.canciones.Cancion5;

import GUI.dialogos6;
import config.CancionBase;
import config.Mapeado;
import config.ResolucionManager;

public class Cancion5 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Mapeado mapeado;

    public Cancion5(ResolucionManager resolucion) {
        super(resolucion);
        this.numeroCancion = 5;
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
        
        /*
        //  Después de terminar la canción, cambiamos a la escena de diálogos
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame ventana = resolucion.getVentana(); 
            if (ventana != null) {
                ventana.getContentPane().removeAll();
                ventana.getContentPane().add(new dialogos6(ventana, resolucion));
                ventana.revalidate();
                ventana.repaint();
            } else {
                System.err.println(" No se encontró la ventana principal para mostrar los diálogos.");
            }
        });
        */
    }

    }

