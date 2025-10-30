package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import config.ResolucionManager;
import GUI.menu.MenuJuego;

public class dialogos2 extends JPanel implements ActionListener {
    private JFrame ventana;
    private ResolucionManager resolucion;
    private ArrayList<ImageIcon> imagenes;  
    private int indice = 0;
    private JLabel imagenLabel;
    private Timer timer;
    private boolean listoParaContinuar = true;

    public dialogos2(JFrame ventana, ResolucionManager resolucion) {
        this.ventana = ventana;
        this.resolucion = resolucion; 

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
        imagenes.add(new ImageIcon("src/img/nicolasDialogo2.jpg"));
        imagenes.add(new ImageIcon("src/img/fernandoDialogo 2.jpg"));
    }

    private void mostrarDialogo() {
        System.out.println("Índice actual: " + indice + " / tamaño: " + imagenes.size());

        if (indice < imagenes.size()) {
            Image img = imagenes.get(indice).getImage().getScaledInstance(1366, 768, Image.SCALE_SMOOTH);
            imagenLabel.setIcon(new ImageIcon(img));
        } else {
            System.out.println("Fin de los diálogos, iniciando menú...");
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

    // ACÁ FALTA HACER PARA QUE PASE AL NIVEL2 UNA VEZ QUE TERMINEN LOS DIALOGOS.



    @Override
    public void actionPerformed(ActionEvent e) {}
}

