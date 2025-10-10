package config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import GUI.Fondo;
import GUI.Nota;
import GUI.NotasUsuario;

public abstract class CancionBase extends JPanel {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JPanel notaD;
    protected JPanel notaF;
    protected JPanel notaJ;
    protected JPanel notaK;
    
    private boolean dPressed = false;
    private boolean fPressed = false;
    private boolean jPressed = false;
    private boolean kPressed = false;
    private boolean nivelSuperado = false;
    public int BORDE_SUPERIOR_USUARIO;
    public int BORDE_INFERIOR_USUARIO;
    public int POSICION_GLOBAL_X_USUARIO;
    
    protected JPanel panelJuego;

    private Fondo contentPane;
    
    protected List<Nota> notasActivas = new ArrayList<>();
    
    protected abstract void construirCancion(ResolucionManager resolucion); // cada nivel define sus notas y ritmo

    public CancionBase(ResolucionManager resolucion) {
    	setLayout(new BorderLayout());
    	
        // Fondo principal
        contentPane = new Fondo(resolucion.getFondoOpciones());
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setOpaque(true);

        panelJuego = new JPanel(null); // layout nulo para posicionamiento absoluto
        panelJuego.setOpaque(false);
        
        
        /*
     // Obtén el punto (0, 0) de la notaUsuario
        Point topLeft = new Point(0, 0);

        // Convierte el punto (0, 0) de notaUsuario para que sea relativo a panelJuego
        Point ubicacionRelativaAJuego = SwingUtilities.convertPoint(
            notaUsuario, // El componente a ubicar
            topLeft,     // El punto dentro del componente (aquí, la esquina superior izquierda)
            panelJuego   // El sistema de coordenadas de destino
        );
        */
        
        contentPane.add(panelJuego, BorderLayout.CENTER);
        
        notasJugador(resolucion);
        

        
        add(contentPane, BorderLayout.CENTER);
        /*
        eliminarNotasPasadas(notaD);
        eliminarNotasPasadas(notaF);
        eliminarNotasPasadas(notaJ);
        eliminarNotasPasadas(notaK);
        */
        
        // El panel principal recibe eventos de teclado
        setFocusable(true);

        // --- KeyListener para detectar las teclas D, F, J, K ---
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        ((NotasUsuario) notaD).presionar();
                        dPressed = true;
                        verificarColision(notaD); 
                        break;
                    case KeyEvent.VK_F:
                        ((NotasUsuario) notaF).presionar();
                        fPressed = true;
                        verificarColision(notaF); 
                        break;
                    case KeyEvent.VK_J:
                        ((NotasUsuario) notaJ).presionar();
                        jPressed = true;
                        verificarColision(notaJ); 
                        break;
                    case KeyEvent.VK_K:
                        ((NotasUsuario) notaK).presionar();
                        kPressed = true;
                        verificarColision(notaK); 
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        ((NotasUsuario) notaD).soltar();
                        dPressed = false;
                        break;
                    case KeyEvent.VK_F:
                        ((NotasUsuario) notaF).soltar();
                        fPressed = false;
                        break;
                    case KeyEvent.VK_J:
                        ((NotasUsuario) notaJ).soltar();
                        jPressed = false;
                        break;
                    case KeyEvent.VK_K:
                        ((NotasUsuario) notaK).soltar();
                        kPressed = false;
                        break;
                }
            }
        });

        // Configurar las notas del jugador (los paneles de colores)


        // --- Asegurar que el foco llegue correctamente ---
        SwingUtilities.invokeLater(() -> {
            calcularBordesUsuario();
            requestFocusInWindow();
        });
    }

 
    
    public void agregarNota(Nota nota) {
        panelJuego.add(nota);
        notasActivas.add(nota); // Agrega la nota a tu lista de rastreo
        // Asegúrate de que las notas se muevan en un hilo o temporizador separado.
        for (Nota notaActiva : notasActivas){
        	System.out.println("Se agrego la nota" + notasActivas);
        }
    }
    
    private void calcularBordesUsuario() {
        // Si la notaD aún no se ha creado (aunque debería estarlo después de notasJugador), sal.
        if (notaD == null || panelJuego == null) return; 

        // Solo necesitamos calcular la posición de UNA de las notas (ej. notaD),
        // ya que todas están a la misma altura.
        
        Point ubicacionAbsoluta = SwingUtilities.convertPoint(
            notaD.getParent(), 
            notaD.getLocation(), 
            panelJuego
        );
        
        // Almacena los valores en las variables de instancia
        this.BORDE_SUPERIOR_USUARIO = ubicacionAbsoluta.y;
        this.BORDE_INFERIOR_USUARIO = ubicacionAbsoluta.y + notaD.getHeight();
        this.POSICION_GLOBAL_X_USUARIO = ubicacionAbsoluta.x;
        
        // Nota: ¡Este cálculo solo será preciso si se llama DESPUÉS de que panelJuego haya sido dibujado!
        // Lo manejaremos con SwingUtilities.invokeLater.
    }
    
 // En CancionBase.java
    private void verificarColision(JPanel notaUsuario) {
       
        // 2. Definir una TOLERANCIA mínima (necesaria para la velocidad del juego).
        // Si quieres máxima precisión, 15px-20px es un rango estricto.
        final int RANGO_TOLERANCIA_Y = 100;

        // 3. Bucle Inverso
        for (int i = notasActivas.size() - 1; i >= 0; i--) {
            Nota notaCayendo = notasActivas.get(i);
            
            // 4. FILTRO HORIZONTAL (CARRIL)
            // Se mantiene para asegurar el carril correcto
            
            if (Math.abs(notaCayendo.getX() - notaUsuario.getX()) > 50) {
            	System.out.println("NO ESTA ALINEADA");
                continue; 
            }
            
            
            // 5. Coordenadas de la NOTA QUE CAE
            int bordeInferiorCayendo = notaCayendo.getY() + notaCayendo.getHeight();
            
            System.out.println("el borde superior de la nota es" + notaCayendo.getY());
            System.out.println("el borde inferior de la nota es" + bordeInferiorCayendo);
            
            // 6. CHEQUEO DE GOLPE (HIT CHECK)
            // Condición: La parte inferior de la nota cayendo debe estar DENTRO del área de golpe del jugador.
            
            // La nota debe haber pasado el BORDE SUPERIOR del jugador.
            boolean yaPasoBordeSuperior = (notaCayendo.getY() + notaCayendo.getHeight()) >= (BORDE_SUPERIOR_USUARIO - RANGO_TOLERANCIA_Y);
            
            // La nota no debe haber pasado el BORDE INFERIOR del jugador (o se considera perdida).
            boolean noPasoBordeInferior = notaCayendo.getY() <= (BORDE_INFERIOR_USUARIO + RANGO_TOLERANCIA_Y);

            // Si ambas condiciones se cumplen, la nota está en el área donde se debe golpear.
            if (yaPasoBordeSuperior && noPasoBordeInferior) {
                
            	System.out.println("Se puede golpear la nota");
                // *** ¡GOLPE VÁLIDO! 🎉 ***
                // La nota se elimina justo cuando "toca" o "cruza" la notaUsuario.
                
                panelJuego.remove(notaCayendo); 
                notasActivas.remove(i);       
                panelJuego.repaint();         
                
                return; // Solo procesamos el golpe más cercano
            }
            /*
            // 7. LÓGICA DE NOTAS PERDIDAS
            // Si la nota ha caído completamente más allá del área de golpe sin ser tocada.
            if (notaCayendo.getY() > BORDE_INFERIOR_USUARIO + RANGO_TOLERANCIA_Y) { 
            	
            	System.out.println("se borro la nota");
                // ... [Lógica de nota perdida]
                panelJuego.remove(notaCayendo);
                notasActivas.remove(i);
                panelJuego.repaint();
           }
            */
            
        }
    }
    
    public void eliminarNotasPasadas(Nota notaCayendo) {
// 1. Definir el ÁREA DE GOLPE (hitbox) del jugador.
    	
    	//        System.out.println("el borde superior del usuario es" + BORDE_SUPERIOR_USUARIO);
//        System.out.println("el borde inferior del usuario es" + BORDE_INFERIOR_USUARIO);

        // 2. Definir una TOLERANCIA mínima (necesaria para la velocidad del juego).
        // Si quieres máxima precisión, 15px-20px es un rango estricto.
        final int RANGO_TOLERANCIA_Y = 100;

         
            
            // 5. Coordenadas de la NOTA QUE CAE
            int bordeInferiorCayendo = notaCayendo.getY() + notaCayendo.getHeight();
            
            
            // 7. LÓGICA DE NOTAS PERDIDAS
            // Si la nota ha caído completamente más allá del área de golpe sin ser tocada.
            if (notaCayendo.getY() > BORDE_INFERIOR_USUARIO + RANGO_TOLERANCIA_Y) { 
            	
            	System.out.println("se borro la nota");
                // ... [Lógica de nota perdida]
                panelJuego.remove(notaCayendo);
                panelJuego.repaint();
           }
            
            
        
    }

   
    private void notasJugador(ResolucionManager resolucion) {
        JPanel panelSur = new JPanel();
        panelSur.setOpaque(false);
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.X_AXIS));
        panelJuego.add(panelSur);
        
        

        int separacion = resolucion.escalarX(0);

        // === Crear las 4 notas fijas con tus imágenes ===
        notaD = new NotasUsuario(
            "/img/notas/notasUsuario/izquierda.png",
            "/img/notas/notasUsuario/izquierdaPresion.png",
            0, 0, resolucion
        );
        notaF = new NotasUsuario(
            "/img/notas/notasUsuario/abajo.png",
            "/img/notas/notasUsuario/abajoPresion.png",
            0, 0, resolucion
        );
        notaJ = new NotasUsuario(
            "/img/notas/notasUsuario/arriba.png",
            "/img/notas/notasUsuario/arribaPresion.png",
            0, 0, resolucion
        );
        notaK = new NotasUsuario(
            "/img/notas/notasUsuario/derecha.png",
            "/img/notas/notasUsuario/derechaPresion.png",
            0, 0, resolucion
        );

        // Añadir con separaciones
        panelSur.add(Box.createHorizontalGlue());
        panelSur.add(notaD);
        panelSur.add(Box.createRigidArea(new Dimension(separacion, 0)));
        panelSur.add(notaF);
        panelSur.add(Box.createRigidArea(new Dimension(separacion, 0)));
        panelSur.add(notaJ);
        panelSur.add(Box.createRigidArea(new Dimension(separacion, 0)));
        panelSur.add(notaK);
        panelSur.add(Box.createHorizontalGlue());

        // Espacio inferior (altura donde se ubican las flechas)
        panelSur.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(250))));
        
        int altoNotaUsuario = resolucion.escalarY(80) + resolucion.escalarY(250); 
        // Los 80px del componente NotasUsuario + los 250px del Box.createRigidArea
        
        SwingUtilities.invokeLater(() -> {
            int anchoJuego = panelJuego.getWidth();
            int altoJuego = panelJuego.getHeight();
            
            // Y donde panelSur debe empezar (casi al final de panelJuego)
            int posY = altoJuego - altoNotaUsuario; 
            int posX = 0;
            
            panelSur.setBounds(posX, posY, anchoJuego, altoNotaUsuario);
            
            panelJuego.revalidate();
            panelJuego.repaint();
        });
    }
    

    
    protected Fondo getContentPaneFondo() {
        return contentPane;
    }
    
    
    
}
