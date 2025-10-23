package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import config.ResolucionManager;
import GUI.menu.MenuJuego;

public class Dialogos extends JPanel implements ActionListener {
    private JFrame ventana;
    private ResolucionManager resolucion;
    private ArrayList<ImageIcon> imagenes;  // Imágenes de cada diálogo     
    private int indice = 0;
    private JLabel imagenLabel;
    private Timer timer;
    private boolean listoParaContinuar = true;

    public Dialogos(JFrame ventana, ResolucionManager resolucion) {
        this.ventana = ventana;
        setLayout(null);
        setBackground(Color.black);

        // Inicialización de los componentes visuales
        imagenLabel = new JLabel();
        imagenLabel.setBounds(0, 0, 1366, 768);
        add(imagenLabel);

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
        
        imagenes.add(new ImageIcon("src/img/dialogoTransicion1.jpg"));
        
        imagenes.add(new ImageIcon("src/img/fernandoDialogo1.jpg"));
        


        // Al terminar estos, pasa a la canción
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

        if (indice < imagenes.size()) {
            Image img = imagenes.get(indice).getImage().getScaledInstance(1366, 768, Image.SCALE_SMOOTH);
            imagenLabel.setIcon(new ImageIcon(img));
        } else {
        	iniciarMenuJuego();
        }
    }

    private void avanzarDialogo() {
        if (!listoParaContinuar) return;
        listoParaContinuar = false;

        indice++;
        mostrarDialogo();

        // Pequeña pausa para evitar doble clic rápido
        Timer pausa = new Timer(200, e -> listoParaContinuar = true);
        pausa.setRepeats(false);
        pausa.start();
    }

   
    private void iniciarMenuJuego() {
        ventana.setContentPane(new MenuJuego(resolucion));
        ventana.revalidate();
        ventana.repaint();
        javax.swing.SwingUtilities.invokeLater(() -> {
            ventana.getContentPane().requestFocusInWindow();
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}
