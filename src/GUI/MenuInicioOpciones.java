package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class MenuInicioOpciones extends JPanel {

    private static final long serialVersionUID = 1L;

    public MenuInicioOpciones(ResolucionManager resolucion) {
        setLayout(new BorderLayout());

        // Fondo del menú de opciones
        Fondo contentPane = new Fondo(resolucion.getFondoOpciones());
        contentPane.setLayout(new BorderLayout(10, 10));
        add(contentPane);

        // Panel superior con título
        JLabel titulo = new JLabel("Opciones", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, resolucion.escalarY(36)));
        titulo.setForeground(Color.WHITE);
        contentPane.add(titulo, BorderLayout.NORTH);

        // Panel central con slider de volumen
        JPanel panelCentral = new JPanel();
        panelCentral.setOpaque(false);
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

        JLabel lblVolumen = new JLabel("Volumen", SwingConstants.CENTER);
        lblVolumen.setFont(new Font("Arial", Font.BOLD, resolucion.escalarY(24)));
        lblVolumen.setForeground(Color.WHITE);
        lblVolumen.setAlignmentX(CENTER_ALIGNMENT);

//      JSlider sliderVolumen = new JSlider(0, 100, 100); // min, max, inicial
        JSlider sliderVolumen = new JSlider(0, 100, Math.round(Sonido.getVolume() * 100f));
        
        sliderVolumen.addChangeListener(e -> {
            float v = sliderVolumen.getValue() / 100f;
            Sonido.setVolume(v);
        });

        
        sliderVolumen.setMajorTickSpacing(25);
        sliderVolumen.setMinorTickSpacing(5);
        sliderVolumen.setPaintTicks(true);
        sliderVolumen.setPaintLabels(true);
        sliderVolumen.setAlignmentX(CENTER_ALIGNMENT);
        sliderVolumen.setOpaque(false);
        sliderVolumen.setBackground(new Color(0,0,0,0)); // fondo transparente
        sliderVolumen.setForeground(Color.WHITE);         // color de los ticks y labels
        sliderVolumen.setFont(new Font("Arial", Font.BOLD, 14));

        // Ajuste proporcional al ancho de la pantalla
        int anchoSlider = (int) (resolucion.getAncho() * 0.40); // 40% del ancho
        int altoSlider = resolucion.escalarY(60);
        sliderVolumen.setPreferredSize(new Dimension(anchoSlider, altoSlider));
        sliderVolumen.setMaximumSize(new Dimension(anchoSlider, altoSlider));
        sliderVolumen.setMinimumSize(new Dimension(anchoSlider, altoSlider));


        panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
        panelCentral.add(lblVolumen);
        panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(15))));
        panelCentral.add(sliderVolumen);
        panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(50))));

        contentPane.add(panelCentral, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));

        JButton btnAplicar = new JButton("Aplicar");
        JButton btnReset = new JButton("Restablecer");
        JButton btnVolver = new JButton("Volver");

        Font fuenteBotones = new Font("Arial", Font.BOLD, resolucion.escalarY(22));
        Dimension tamBoton = new Dimension(resolucion.escalarX(200), resolucion.escalarY(60));

        for (JButton boton : new JButton[]{btnAplicar, btnReset, btnVolver}) {
            boton.setFont(fuenteBotones);
            boton.setForeground(Color.WHITE);
            boton.setFocusPainted(false);
            boton.setContentAreaFilled(false);
            boton.setOpaque(false);
            boton.setPreferredSize(tamBoton);
            boton.setMaximumSize(tamBoton);
        }

        panelBotones.add(Box.createHorizontalGlue());
        panelBotones.add(btnAplicar);
        panelBotones.add(Box.createRigidArea(new Dimension(resolucion.escalarX(20), 0)));
        panelBotones.add(btnReset);
        panelBotones.add(Box.createRigidArea(new Dimension(resolucion.escalarX(20), 0)));
        panelBotones.add(btnVolver);
        panelBotones.add(Box.createHorizontalGlue());

        contentPane.add(panelBotones, BorderLayout.SOUTH);

        // Eventos (decorativos por ahora)
        btnAplicar.addActionListener(e -> {
            int volumen = sliderVolumen.getValue();
            System.out.println("Volumen aplicado: " + volumen);
        });

        btnReset.addActionListener(e -> {
            sliderVolumen.setValue(50);
            System.out.println("Volumen restablecido.");
        });

        btnVolver.addActionListener(e -> {
            JFrame ventana = (JFrame) this.getTopLevelAncestor();
            MenuInicio menu = new MenuInicio(resolucion);
            ventana.setContentPane(menu);
            ventana.revalidate();
            ventana.repaint();
        });
    }

}
