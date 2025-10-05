package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class CancionBase extends JPanel {
	private JPanel notaD;
	private JPanel notaF;
	private JPanel notaJ;
	private JPanel notaK;
	
    private boolean dPressed = false;
    private boolean fPressed = false;
    private boolean jPressed = false;
    private boolean kPressed = false;
    private boolean nivelSuperado = false;

    private Fondo contentPane;

    public CancionBase(ResolucionManager resolucion) {
        contentPane = new Fondo(resolucion.getFondoOpciones());
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setFocusable(true);
        setFocusable(true);
        requestFocusInWindow();

        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D: 
                    	notaD.setBackground(Color.WHITE);
                    	notaD.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    	dPressed = true;
                    	break;
                    
                    case KeyEvent.VK_F : 
                    	notaF.setBackground(Color.WHITE);
                    	notaF.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                    	fPressed = true;
                    	break;
                    
                    case KeyEvent.VK_J : 
                    	notaJ.setBackground(Color.WHITE);
                    	notaJ.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 3));
                    	jPressed = true;
                    	break;
                    
                    case KeyEvent.VK_K : 
                    	notaK.setBackground(Color.WHITE);
                    	notaK.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                    	kPressed = true;
                    	break;
                    	
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                case KeyEvent.VK_D: 
                    notaD.setBackground(Color.RED);
                    dPressed = false;
                    break;
                case KeyEvent.VK_F: 
                    notaF.setBackground(Color.YELLOW);
                    fPressed = false;
                    break;
                case KeyEvent.VK_J: 
                    notaJ.setBackground(Color.MAGENTA);
                    jPressed = false;
                    break;
                case KeyEvent.VK_K: 
                    notaK.setBackground(Color.GREEN);
                    kPressed = false;
                    break;
                }
            }
        });

        // Para que reciba eventos de teclado
        requestFocusInWindow();
        
//        configurarNotas();
        
        notasJugador(resolucion);

    }
    
    
    
    public void notasJugador(ResolucionManager resolucion) {
    	JPanel panelSur = new JPanel();
    	panelSur.setOpaque(false);
    	panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.X_AXIS));
        contentPane.add(panelSur, BorderLayout.SOUTH);
    	
    	int ancho = resolucion.escalarX(30);
        int alto  = resolucion.escalarY(30);
        
    	JPanel notaD = new JPanel();
    	notaD.setPreferredSize(new Dimension(ancho, alto));
    	notaD.setMaximumSize(new Dimension(ancho, alto));
    	notaD.setBackground(Color.RED);
    	this.notaD = notaD;
    	
    	JPanel notaF = new JPanel();
    	notaF.setPreferredSize(new Dimension(ancho, alto));
    	notaF.setMaximumSize(new Dimension(ancho, alto));
    	notaF.setBackground(Color.YELLOW);
    	this.notaF = notaF;
    	
    	JPanel notaJ = new JPanel();
    	notaJ.setPreferredSize(new Dimension(ancho, alto));
    	notaJ.setMaximumSize(new Dimension(ancho, alto));
    	notaJ.setBackground(Color.MAGENTA);
    	this.notaJ = notaJ;
    	
    	JPanel notaK = new JPanel();
    	notaK.setPreferredSize(new Dimension(ancho, alto));
    	notaK.setMaximumSize(new Dimension(ancho, alto));
    	notaK.setBackground(Color.GREEN);
    	this.notaK = notaK;
    	
    	panelSur.add(Box.createHorizontalGlue());
//        panelCentral.add(Box.createVerticalGlue());
    	panelSur.add(notaD);
//        panelCentral.add(Box.createVerticalGlue());
    	panelSur.add(Box.createRigidArea(new Dimension(10, 0)));
    	panelSur.add(notaF);
//        panelCentral.add(Box.createVerticalGlue());
    	panelSur.add(Box.createRigidArea(new Dimension(10, 0)));
    	panelSur.add(notaJ);
//        panelCentral.add(Box.createVerticalGlue());
    	panelSur.add(Box.createRigidArea(new Dimension(10, 0)));
    	panelSur.add(notaK);
    	
    	panelSur.add(Box.createRigidArea(new Dimension(0, 250)));
//        panelCentral.add(Box.createVerticalGlue());
    	panelSur.add(Box.createHorizontalGlue());
    }
/*
    private void configurarNotas() {
        contentPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D: 
                    	notaD.setBackground(Color.WHITE);
                    	notaD.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    	dPressed = true;
                    	break;
                    
                    case KeyEvent.VK_F : 
                    	notaF.setBackground(Color.WHITE);
                    	notaF.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                    	fPressed = true;
                    	break;
                    
                    case KeyEvent.VK_J : 
                    	notaJ.setBackground(Color.WHITE);
                    	notaJ.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 3));
                    	jPressed = true;
                    	break;
                    
                    case KeyEvent.VK_K : 
                    	notaK.setBackground(Color.WHITE);
                    	notaK.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                    	kPressed = true;
                    	break;
                    	
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                case KeyEvent.VK_D: 
                    notaD.setBackground(Color.RED);
                    dPressed = false;
                    break;
                case KeyEvent.VK_F: 
                    notaF.setBackground(Color.YELLOW);
                    fPressed = false;
                    break;
                case KeyEvent.VK_J: 
                    notaJ.setBackground(Color.MAGENTA);
                    jPressed = false;
                    break;
                case KeyEvent.VK_K: 
                    notaK.setBackground(Color.GREEN);
                    kPressed = false;
                    break;
                }
            }
        });

        // Para que reciba eventos de teclado
        contentPane.requestFocusInWindow();
    }
    */
}
