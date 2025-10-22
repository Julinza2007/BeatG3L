package config;

public class ResolucionManager {

    // Resolución base (para escalado)
    private static final int BASE_WIDTH = 1920;
    private static final int BASE_HEIGHT = 1080;

    private int ancho;
    private int alto;
    private double scaleX;
    private double scaleY;

    public ResolucionManager(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;

        // Factores de escala en relación a la base
        this.scaleX = (double) ancho / BASE_WIDTH;
        this.scaleY = (double) alto / BASE_HEIGHT;
    }

    // Devuelve ruta del fondo según resolución
    public String getFondoMenuInicio() {
        if (ancho == 800 && alto == 600) return "/img/fondo/fondoMenu_800x600.jpg";
        if (ancho == 1280 && alto == 720) return "/img/fondo/fondoMenu_1280x720.jpg";
        if (ancho == 1366 && alto == 768) return "/img/fondo/fondoMenu_1366x768.jpeg";
        if (ancho == 1600 && alto == 900) return "/img/fondo/fondoMenu_1600x900.jpg";
        if (ancho == 1920 && alto == 1080) return "/img/fondo/fondoMenu_1920x1080.jpg";

        // Por defecto, usar la de mayor calidad
        return "/GUI/img/fondo/1_1920x1080_mejorada.jpg";
    }
    
    public String getFondoOpciones() {
        if (ancho == 800 && alto == 600) return "/img/fondo/2_800x600.png";
        if (ancho == 1280 && alto == 720) return "/img/fondo/2_1280x720.png";
        if (ancho == 1366 && alto == 768) return "/img/fondo/2_1366x768.png";
        if (ancho == 1600 && alto == 900) return "/img/fondo/2_1600x900.png";
        if (ancho == 1920 && alto == 1080) return "/img/fondo/2_1920x1080.png";

        // Por defecto, usar la de mayor calidad
        return "/GUI/img/fondo/2_1920x1080.png";
    }

    // Escalar ancho en relación a la resolución actual
    public int escalarX(int valor) {
        return (int) (valor * scaleX);
    }

    // Escalar alto en relación a la resolución actual
    public int escalarY(int valor) {
        return (int) (valor * scaleY);
    }

    // Getters
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public double getScaleX() { return scaleX; }
    public double getScaleY() { return scaleY; }
}
