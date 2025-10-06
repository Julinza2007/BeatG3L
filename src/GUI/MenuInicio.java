package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JFrame;

public class MenuInicio extends JPanel {

    private static final long serialVersionUID = 1L;
    private ResolucionManager resolucion;
    private JLabel logoLabel;
    private Timer animacionLogo;
    private ImageIcon logoBase;

    public MenuInicio(ResolucionManager resolucion) {
        this.resolucion = resolucion;

        Sonido.reproducirMusicaLoop("GUI/sonidos/inicio.wav");

        setLayout(new BorderLayout(10, 10));

        Fondo contentPane = new Fondo(resolucion.getFondoMenuInicio());
        contentPane.setLayout(new BorderLayout(10, 10));
        add(contentPane, BorderLayout.CENTER);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        // altura fija para evitar que el layout salte
        int alturaLogo = resolucion.escalarY(400);
        panelSuperior.setPreferredSize(new Dimension(0, alturaLogo));

        // 1) Cargar el PNG original
        logoBase = new ImageIcon(getClass().getResource("/GUI/img/fondo/logoG3L.png"));

        // 2) Calcular el tamaño máximo que puede ocupar el logo dentro del panel superior
        int anchoMaxLogo = resolucion.escalarX(700);                 // ajustá a gusto
        int altoMaxLogo  = alturaLogo - resolucion.escalarY(30);     // un pequeño margen

        // 3) Escalar UNA vez manteniendo proporción para que encaje
        int w0 = logoBase.getIconWidth();
        int h0 = logoBase.getIconHeight();
        double r = Math.min(anchoMaxLogo / (double) w0, altoMaxLogo / (double) h0);
        int w  = Math.max(1, (int) Math.round(w0 * r));
        int h  = Math.max(1, (int) Math.round(h0 * r));
        ImageIcon logoAjustado = new ImageIcon(
                logoBase.getImage().getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH)
        );

        // 4) Crear el label con el logo AJUSTADO (no el original)
        logoLabel = new JLabel(logoAjustado, SwingConstants.CENTER);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);

        panelSuperior.add(logoLabel, BorderLayout.CENTER);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        // 5) La animación ahora debe partir del AJUSTADO, así no se sale
        int bpm = 115;
        int intervalo = 60000 / bpm;
        animacionLogo = new Timer(intervalo, evt -> Animaciones.animarLogo(logoLabel, logoAjustado));
        animacionLogo.setInitialDelay(500);
        animacionLogo.start();

        JPanel contenedorBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        contenedorBotones.setOpaque(false);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));

        ImageIcon btnBase = new ImageIcon(getClass().getResource("/GUI/img/fondo/btnEscalado.png"));

        JButton btnJugar = Componentes.crearBotonConImagen("Jugar", btnBase, resolucion);
        JButton btnOpciones = Componentes.crearBotonConImagen("Opciones", btnBase, resolucion);
        JButton btnCreditos = Componentes.crearBotonConImagen("Créditos", btnBase, resolucion);
        JButton btnSalir = Componentes.crearBotonConImagen("Salir", btnBase, resolucion);

        btnJugar.addActionListener(e -> {
            System.out.println("Hola. Que divertido, estás jugando...");
            if (animacionLogo != null && animacionLogo.isRunning()) {
                animacionLogo.stop();
            }
            JFrame ventana = (JFrame) this.getTopLevelAncestor();
            ventana.setContentPane(new MenuJuego(resolucion));
            ventana.revalidate();
            ventana.repaint();
        });

        btnOpciones.addActionListener(e -> {
            if (animacionLogo != null && animacionLogo.isRunning()) {
                animacionLogo.stop();
            }
            JFrame ventana = (JFrame) this.getTopLevelAncestor();
            ventana.setContentPane(new MenuInicioOpciones(resolucion));
            ventana.revalidate();
            ventana.repaint();
        });

        btnCreditos.addActionListener(e -> {
            System.out.println("Se supone que acá irían los créditos...");
        });

        btnSalir.addActionListener(e -> System.exit(0));

        panelBotones.add(btnJugar);
        panelBotones.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(15))));
        panelBotones.add(btnOpciones);
        panelBotones.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(15))));
        panelBotones.add(btnCreditos);
        panelBotones.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(15))));
        panelBotones.add(btnSalir);

        contenedorBotones.add(panelBotones);
        contentPane.add(contenedorBotones, BorderLayout.CENTER);
    }

/*
    public JButton crearBotonConImagen(String texto, ImageIcon icono) {
        JButton boton = new JButton(texto, icono);

        boton.setHorizontalTextPosition(SwingConstants.CENTER);
        boton.setVerticalTextPosition(SwingConstants.CENTER);

        boton.setFont(new Font("Arial", Font.BOLD, resolucion.escalarY(22)));
        boton.setForeground(Color.WHITE);

        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);

        int ancho = resolucion.escalarX(300);
        int alto  = resolucion.escalarY(130);
        boton.setPreferredSize(new Dimension(ancho, alto));
        boton.setMaximumSize(new Dimension(ancho, alto));
        boton.setMinimumSize(new Dimension(ancho, alto));
        boton.setAlignmentX(CENTER_ALIGNMENT);

        int wImg = icono.getIconWidth();
        int hImg = icono.getIconHeight();
        double r = Math.min(ancho / (double) wImg, alto / (double) hImg);
        int wEsc = Math.max(1, (int) Math.round(wImg * r));
        int hEsc = Math.max(1, (int) Math.round(hImg * r));
        java.awt.Image img = icono.getImage().getScaledInstance(wEsc, hEsc, java.awt.Image.SCALE_SMOOTH);
        boton.setIcon(new ImageIcon(img));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                Sonido.reproducirEfecto("GUI/sonidos/pickOpcion.wav");
            }
        });
        boton.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                Sonido.reproducirEfecto("GUI/sonidos/pickOpcion.wav");
            }
        });

        return boton;
    }
*/
    
}
