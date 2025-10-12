package GUI.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import GUI.BeatG3L;
import GUI.Fondo;
import config.ResolucionManager;
import config.Sonido;

public class MenuInicioOpciones extends JPanel {
    private static final long serialVersionUID = 1L;

    public MenuInicioOpciones(ResolucionManager resolucion) {
        setLayout(new BorderLayout());

        Fondo contentPane = new Fondo(resolucion.getFondoOpciones()); // Se pone el fondo del menú de opciones
        contentPane.setLayout(new BorderLayout(10, 10)); 	// Se usa BorderLayout con espacios de 10 pixeles entre componentes
        add(contentPane, BorderLayout.CENTER);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        contentPane.add(centro, BorderLayout.CENTER);

        JLabel lblResol = new JLabel("Resolución");
        lblResol.setAlignmentX(CENTER_ALIGNMENT);
        lblResol.setForeground(Color.WHITE);
        lblResol.setFont(new Font("Arial", Font.BOLD, Math.max(resolucion.escalarY(24), 16)));

        String[] opcionesResol = {
            "800 x 600",
            "1280 x 720",
            "1366 x 768",
            "1600 x 900",
            "1920 x 1080"
        };
        
        JComboBox<String> comboResol = new JComboBox<>(opcionesResol);
        comboResol.setAlignmentX(CENTER_ALIGNMENT);

        String etiquetaElegida = resolucion.getAncho() + " x " + resolucion.getAlto();
        comboResol.setSelectedItem(etiquetaElegida);

        Dimension tamanioCombo = new Dimension((int)(resolucion.getAncho() * 0.40), resolucion.escalarY(40));
        comboResol.setMaximumSize(tamanioCombo);
        comboResol.setPreferredSize(tamanioCombo);

        JLabel lblVol = new JLabel("Volumen");
        lblVol.setAlignmentX(CENTER_ALIGNMENT);
        lblVol.setForeground(Color.WHITE);
        lblVol.setFont(new Font("Arial", Font.BOLD, Math.max(resolucion.escalarY(24), 16)));

        int volInicial = Math.round(Sonido.getVolume() * 100f); // 0..100
        JSlider sliderVol = new JSlider(0, 100, volInicial);
        sliderVol.setAlignmentX(CENTER_ALIGNMENT);
        sliderVol.setOpaque(false);
        sliderVol.setMajorTickSpacing(25);
        sliderVol.setMinorTickSpacing(5);
        sliderVol.setPaintTicks(true);
        sliderVol.setPaintLabels(true);
        sliderVol.setForeground(Color.WHITE);
        sliderVol.setFont(new Font("Arial", Font.BOLD, 14));

        int anchoSlider = (int)(resolucion.getAncho() * 0.40);
        int altoSlider  = resolucion.escalarY(80);
        sliderVol.setMaximumSize(new Dimension(anchoSlider, altoSlider));
        sliderVol.setPreferredSize(new Dimension(anchoSlider, altoSlider));

        // Se aplica volumen en tiempo real
        sliderVol.addChangeListener(e -> {
            float vol = sliderVol.getValue() / 100f;
            Sonido.setVolume(vol);
        });

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));

        JButton btnAplicar = new JButton("Aplicar");
        JButton btnVolver  = new JButton("Volver");
        Font fuentebtn = new Font("Arial", Font.BOLD, resolucion.escalarY(22));
        Dimension tamanioBtn = new Dimension(resolucion.escalarX(200), resolucion.escalarY(60));
        for (JButton b : new JButton[]{btnAplicar, btnVolver}) {
            b.setFont(fuentebtn);
            b.setForeground(Color.WHITE);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setPreferredSize(tamanioBtn);
            b.setMaximumSize(tamanioBtn);
        }
        panelBotones.add(Box.createHorizontalGlue());
        panelBotones.add(btnAplicar);
        panelBotones.add(Box.createRigidArea(new Dimension(resolucion.escalarX(20), 0)));
        panelBotones.add(btnVolver);
        panelBotones.add(Box.createHorizontalGlue());
        panelBotones.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(600))));
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        centro.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(200))));
        centro.add(lblVol);
        centro.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(10))));
        centro.add(sliderVol);
        centro.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(50))));
        centro.add(lblResol);
        centro.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(10))));
        centro.add(comboResol);


        centro.add(Box.createVerticalGlue());

        btnAplicar.addActionListener(e -> {
            String txt = (String) comboResol.getSelectedItem(); // trae el texto seleccionado.            
            String[] p = txt.replace(" ", "").split("x"); // saca los espacios y separa por segun la x para así luego usarlo.
            int w = Integer.parseInt(p[0]); // se convierten a int los valores de ancho y alto
            int h = Integer.parseInt(p[1]); // donde p[0] sería el ancho y p[1] el alto.
            
            ResolucionManager nueva = new ResolucionManager(w, h); // se crea una nueva resolución con los valores obtenidos.

            JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this); // se obtiene la ventana padre.
            if (ventana instanceof BeatG3L juego) { // si la ventana es una instancia de BeatG3L o sea la ventana principal del juego.
                juego.setearResolucion(nueva); // se usa el método para setear a la nueva resolución.
            }

            ventana.setContentPane(new MenuInicioOpciones(nueva)); // Se recreaa este mismo panel pero con la nueva resolución.
            ventana.revalidate();
            ventana.repaint();
        });

        btnVolver.addActionListener(e -> {
            JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
            ventana.setContentPane(new MenuInicio(resolucion));
            ventana.revalidate();
            ventana.repaint();
        });
    }
}
