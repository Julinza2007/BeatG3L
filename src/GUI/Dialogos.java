package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import config.ResolucionManager;
import GUI.menu.MenuJuego;

public class Dialogos extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private ResolucionManager resolucion;
    private ArrayList<ImageIcon> imagenes;  
    private int indice = 0;
    private JLabel imagenLabel;
    private boolean listoParaContinuar = true;

    public Dialogos(JFrame ventana, ResolucionManager resolucion) {
        this.resolucion = resolucion; 

        setLayout(null);
        setBackground(Color.black);

        imagenLabel = new JLabel();
        add(imagenLabel);

     // Centrar automáticamente la imagen una vez que el panel tenga tamaño
     addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
             int x = (getWidth() - 1366) / 2;
             int y = (getHeight() - 768) / 2;
             imagenLabel.setBounds(x, y, 1366, 768);
         }
     });


        imagenes = new ArrayList<>();
        cargarDialogos();
        mostrarDialogo();

        // Detectar tecla o clic para avanzar
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                avanzarDialogo();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                avanzarDialogo();
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void cargarDialogos() {
        imagenes.add(new ImageIcon("src/img/lucasDialogo1.jpg"));
        imagenes.add(new ImageIcon("src/img/nicolasDialogo1.jpg"));
        imagenes.add(new ImageIcon("src/img/fernandoDialogo1.jpg"));
    }

    private void mostrarDialogo() {
        System.out.println("Índice actual: " + indice + " / tamaño: " + imagenes.size());

        if (indice < imagenes.size()) {
            Image img = imagenes.get(indice).getImage().getScaledInstance(1366, 768, Image.SCALE_SMOOTH);
            imagenLabel.setIcon(new ImageIcon(img));
        } else {
            System.out.println("Fin de los diálogos, iniciando menú...");
            iniciarMenuJuego();
        }
    }

    private void avanzarDialogo() {
        if (!listoParaContinuar) return;
        listoParaContinuar = false;

        indice++;
        mostrarDialogo();

        Timer pausa = new Timer(200, e -> listoParaContinuar = true);
        pausa.setRepeats(false);
        pausa.start();
    }

    private void iniciarMenuJuego() {
        // Espera hasta que la ventana esté disponible antes de cambiar
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = (JFrame) getTopLevelAncestor();
            if (ventana != null && resolucion != null) {
                ventana.setContentPane(new MenuJuego(resolucion));
                ventana.revalidate();
                ventana.repaint();
                ventana.getContentPane().requestFocusInWindow();
            } else {
                System.err.println("Error, no se pudo obtener la ventana o resolución.");
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent e) {}
}
