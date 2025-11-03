package GUI.menu;

import java.awt.*;
import GUI.Animaciones;
import javax.swing.*;
import config.ResolucionManager;
import java.awt.event.*;

public class PantallaInicio extends JPanel {
    private static final long serialVersionUID = 1L;
    private Font fuenteElectric_kicks;

    public PantallaInicio(ResolucionManager resolucion) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Texto inferior
        JLabel texto = new JLabel("G3L Studios", SwingConstants.CENTER);
        texto.setBorder(BorderFactory.createEmptyBorder(30, 0, 70, 0));
        try {
            fuenteElectric_kicks = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/config/fonts/electric.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
            fuenteElectric_kicks = new Font("Arial", Font.BOLD, 48);
        }
        texto.setFont(fuenteElectric_kicks.deriveFont(Font.PLAIN, resolucion.escalarY(50)));
        texto.setForeground(Color.WHITE);
        add(texto, BorderLayout.SOUTH);

        // Logo centrado
        ImageIcon logo = new ImageIcon(getClass().getResource("/img/G3LgroupMasChico.png"));
        JLabel labelLogo = new JLabel(logo, SwingConstants.CENTER);
        add(labelLogo, BorderLayout.CENTER);
        Animaciones.zoomPantalla(labelLogo, logo);

        // Tecla para saltar
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                cambiarAMenu(resolucion);
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        // Timer para cambiar a MenuInicio
        new Timer(7000, e -> {
            SwingUtilities.invokeLater(() -> cambiarAMenu(resolucion));
        }) {private static final long serialVersionUID = 1L;

		{
            setRepeats(false);
            start();
        }};
    }

    private void cambiarAMenu(ResolucionManager resolucion) {
        JFrame ventana = (JFrame) getTopLevelAncestor();
        if (ventana != null) {
            ventana.setContentPane(new MenuInicio(resolucion));
            ventana.revalidate();
            ventana.repaint();
        } else {
            System.err.println("⚠️ Advertencia: ventana aún no disponible, se omitió cambio.");
        }
    }
}