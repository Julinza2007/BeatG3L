package GUI;

import java.awt.*;
import javax.swing.*;
import config.ResolucionManager;

public class Nota extends JPanel {

    private static final long serialVersionUID = 1L;

    // Parámetros básicos (mantengo simple)
    private int columna = 0;             // 0=izq, 1=abajo, 2=arriba, 3=der
    public  boolean esHold = false;      // CancionBase lo lee directo
    private boolean headScored = false;  // evitar doble score de cabeza

    // Imágenes
    private Image imgHead;  // tap / cabeza
    private Image imgBody;  // cuerpo (hold)
    private Image imgTail;  // cola  (hold)

    // Tamaños y cache de proporciones
    private int lado;       // ancho destino de la nota (cabeza/cuerpo/cola)
    private int headH;      // alto destino de cabeza (mantiene proporción del PNG)
    private int tailH;      // alto destino de cola   (mantiene proporción del PNG)
    private int bodySrcH;   // alto original del body (para “tilear”)
    private int bodySrcW;   // ancho original del body

    public Nota(int x, int y, int columna, boolean esHold, long duracionMs, double velocidadPxPorMs, ResolucionManager resolucion) {
        this.columna = Math.max(0, Math.min(3, columna));
        this.esHold = esHold;

        // base ~157px; uso mitad + escalado mínimo que ya venís usando
        int base = 157 / 2;
        this.lado = resolucion.escalarUniformeMin(base, 80);

        // Rutas por columna
        String headPath, bodyPath = null, tailPath = null;
        switch (this.columna) {
            case 0 -> { headPath="/img/notas/izquierda.png";  bodyPath="/img/notas/izquierdaHold.png";  tailPath="/img/notas/izquierdaHoldTermina.png"; }
            case 1 -> { headPath="/img/notas/abajo.png";       bodyPath="/img/notas/abajoHold.png";      tailPath="/img/notas/abajoHoldTermina.png"; }
            case 2 -> { headPath="/img/notas/arriba.png";      bodyPath="/img/notas/arribaHold.png";     tailPath="/img/notas/arribaHoldTermina.png"; }
            default -> { headPath="/img/notas/derecha.png";    bodyPath="/img/notas/derechaHold.png";    tailPath="/img/notas/derechaHoldTermina.png"; }
        }

        // Cargar imágenes
        imgHead = new ImageIcon(getClass().getResource(headPath)).getImage();
        if (esHold) {
            imgBody = new ImageIcon(getClass().getResource(bodyPath)).getImage();
            imgTail = new ImageIcon(getClass().getResource(tailPath)).getImage();
        }

        // Calcular alturas respetando proporciones del PNG
        headH = escalarAltoPorAncho(imgHead, lado);
        if (esHold) {
            tailH = escalarAltoPorAncho(imgTail, lado);
            bodySrcW = imgBody.getWidth(null);
            bodySrcH = imgBody.getHeight(null);
            if (bodySrcW <= 0 || bodySrcH <= 0) { bodySrcW = 1; bodySrcH = 1; }
        }

        // Altura final del componente
        int altoPx;
        if (esHold) {
            // altura = cuerpo(según duración) + head + tail
            int cuerpoPx = (int)Math.round(Math.max(0, duracionMs) * velocidadPxPorMs);
            altoPx = cuerpoPx + headH + tailH;
        } else {
            altoPx = headH; // el tap usa el alto proporcional de su PNG
        }

        setOpaque(false);
        setBounds(x, y, lado, altoPx);
    }

    private static int escalarAltoPorAncho(Image img, int anchoDestino) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        if (w <= 0 || h <= 0) return anchoDestino; // fallback cuadrado
        // Mantener proporción: alto = anchoDestino * (h/w)
        return Math.max(1, Math.round(anchoDestino * (h / (float) w)));
    }

    public int getColumna() { return columna; }
    
    private boolean resuelta = false;
    public boolean fueResuelta() { return resuelta; }
    public void marcarResuelta() { resuelta = true; }

    /** Marca cabeza de hold una sola vez; devuelve true si fue la primera. */
    public boolean marcarHeadSiNoFue() {
        if (headScored) return false;
        headScored = true;
        return true;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;

        // Pixel-art nítido
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int W = getWidth();
        int H = getHeight();

        if (!esHold) {
            // Tap simple
            g.drawImage(imgHead, 0, 0, W, H, this);
            return;
        }

     // --- Hold: COLA ARRIBA (invertida), CUERPO TILEADO, CABEZA ABAJO ---
        final int overlap = 0; // <-- Cambiar a 0 para eliminar la causa del artefacto visual.
        int y = 0;

        // 1) COLA (arriba) — FLIP VERTICAL
        int tailSrcW = imgTail.getWidth(null);
        int tailSrcH = imgTail.getHeight(null);
        // Usar un destino exacto: y + tailH, sin el + overlap
        g.drawImage(imgTail,
                0, y, W, y + tailH, // <-- Cambiar y + tailH + overlap a y + tailH
                0, tailSrcH, tailSrcW, 0,
                this);
        y += tailH; // avanzamos sin restar overlap para mantener alto total

        // 2) CUERPO TILEADO (sin estirar verticalmente)
        int alturaCuerpoDisponible = Math.max(0, H - tailH - headH);
        if (alturaCuerpoDisponible > 0) {
            // ... (variables bodySrcW, bodySrcH)

            int dibujado = 0;
            while (dibujado < alturaCuerpoDisponible) {
                int chunk = Math.min(bodySrcH, alturaCuerpoDisponible - dibujado);
                
                // Usar un destino exacto, sin el - overlap ni + overlap
                g.drawImage(
                    imgBody,
                    0, y + dibujado, W, y + dibujado + chunk, // <-- Cambiar a destino exacto: y + dibujado, W, y + dibujado + chunk
                    0, 0, bodySrcW, chunk,
                    this
                );
                dibujado += chunk;
            }
            y += alturaCuerpoDisponible;
        }

        // 3) CABEZA (abajo)
        // Usar un destino exacto: H - headH, sin el - overlap
        int headSrcW = imgHead.getWidth(null);
        int headSrcH = imgHead.getHeight(null);
        g.drawImage(imgHead,
                0, H - headH, W, H, // <-- Cambiar H - headH - overlap a H - headH
                0, 0, headSrcW, headSrcH,
                this);
    }

}