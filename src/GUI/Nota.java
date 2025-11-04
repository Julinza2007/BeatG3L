package GUI;

import java.awt.*;
import javax.swing.*;
import config.ResolucionManager;

public class Nota extends JPanel {

    private static final long serialVersionUID = 1L;

    // Parámetros básicos
    private int columna = 0;             // 0=izq, 1=abajo, 2=arriba, 3=der
    public  boolean esHold = false;      // CancionBase lo lee directo
    private boolean headScored = false;  // evitar doble score de cabeza

    // Imágenes
    private Image imgHead;  // tap / cabeza
    private Image imgBody;  // cuerpo (hold)
    private Image imgTail;  // cola  (hold)

    // Tamaños y cache de proporciones
    private int lado;       // ancho destino de la nota
    private int headH;      // alto destino de cabeza
    private int tailH;      // alto destino de cola
    private int bodySrcH;   // alto original de body
    private int bodySrcW;   // ancho original de body

    public Nota(int x, int y, int columna, boolean esHold, long duracionMs, double velocidadPxPorMs, ResolucionManager resolucion) {
        this.columna = Math.max(0, Math.min(3, columna));
        this.esHold = esHold;

        int base = 157 / 2;
        this.lado = resolucion.escalarUniformeMin(base, 80);

        String headPath, bodyPath = null, tailPath = null;
        switch (this.columna) {
            case 0 -> { headPath="/img/notas/izquierda.png";  bodyPath="/img/notas/izquierdaHold.png";  tailPath="/img/notas/izquierdaHoldTermina2.png"; }
            case 1 -> { headPath="/img/notas/abajo.png";       bodyPath="/img/notas/abajoHold.png";      tailPath="/img/notas/abajoHoldTermina2.png"; }
            case 2 -> { headPath="/img/notas/arriba.png";      bodyPath="/img/notas/arribaHold.png";     tailPath="/img/notas/arribaHoldTermina2.png"; }
            default -> { headPath="/img/notas/derecha.png";    bodyPath="/img/notas/derechaHold.png";    tailPath="/img/notas/derechaHoldTermina2.png"; }
        }

        imgHead = new ImageIcon(getClass().getResource(headPath)).getImage();
        if (esHold) {
            imgBody = new ImageIcon(getClass().getResource(bodyPath)).getImage();
            imgTail = new ImageIcon(getClass().getResource(tailPath)).getImage();
        }

        headH = escalarAltoPorAncho(imgHead, lado);
        if (esHold) {
            tailH = escalarAltoPorAncho(imgTail, lado);
            bodySrcW = imgBody.getWidth(null);
            bodySrcH = imgBody.getHeight(null);
            if (bodySrcW <= 0 || bodySrcH <= 0) { bodySrcW = 1; bodySrcH = 1; }
        }

        int altoPx;
        if (esHold) {
            int cuerpoPx = (int)Math.round(Math.max(0, duracionMs) * velocidadPxPorMs);
            altoPx = cuerpoPx + headH + tailH;
        } else {
            altoPx = headH;
        }

        setOpaque(false);
        setBounds(x, y, lado, altoPx);
    }

    private static int escalarAltoPorAncho(Image img, int anchoDestino) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        if (w <= 0 || h <= 0) return anchoDestino;
        return Math.max(1, Math.round(anchoDestino * (h / (float) w)));
    }

    public int getColumna() { return columna; }

    private boolean resuelta = false;
    public boolean fueResuelta() { return resuelta; }
    public void marcarResuelta() { resuelta = true; }

    /** Marca cabeza de hold una sola vez. */
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
            g.drawImage(imgHead, 0, 0, W, H, this);
            return;
        }

        int y = 0;

        // COLA (arriba) — flip vertical
        int tailSrcW = imgTail.getWidth(null);
        int tailSrcH = imgTail.getHeight(null);
        g.drawImage(imgTail,
                0, y, W, y + tailH,
                0, tailSrcH, tailSrcW, 0,
                this);
        y += tailH;

        // CUERPO tileado
        int alturaCuerpoDisponible = Math.max(0, H - tailH - headH);
        if (alturaCuerpoDisponible > 0) {
            int dibujado = 0;
            while (dibujado < alturaCuerpoDisponible) {
                int chunk = Math.min(bodySrcH, alturaCuerpoDisponible - dibujado);
                g.drawImage(
                    imgBody,
                    0, y + dibujado, W, y + dibujado + chunk,
                    0, 0, bodySrcW, chunk,
                    this
                );
                dibujado += chunk;
            }
            y += alturaCuerpoDisponible;
        }

        // CABEZA (abajo)
        int headSrcW = imgHead.getWidth(null);
        int headSrcH = imgHead.getHeight(null);
        g.drawImage(imgHead,
                0, H - headH, W, H,
                0, 0, headSrcW, headSrcH,
                this);
    }
    
    public int getHeadOffsetFromTop() {
        // Cantidad de píxeles desde el tope del componente hasta el inicio de la cabeza
        return getHeight() - headH;
    }

    
}
