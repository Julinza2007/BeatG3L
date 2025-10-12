	package GUI;
	
	import javax.swing.*;

import config.ResolucionManager;

import java.awt.*;
	
	public class PrecisionPuntaje extends JLabel {
	
		private static final long serialVersionUID = 1L;
		
		protected int puntos;
		protected int combo;
		protected int multiplicador;
		private int maxCombo;
		private int aciertos;
		private int totalNotas;
		private String estado;
		private ResolucionManager resolucion;
		
		public PrecisionPuntaje(int puntosIniciales, ResolucionManager resolucion) {
			this.resolucion = resolucion;
			this.puntos = puntosIniciales;
			this.combo = 0;
			this.multiplicador = 1;
			this.maxCombo = 0;
			this.estado = "";
		
		
			setFont(new Font("SansSerif", Font.PLAIN, Math.max(resolucion.escalarY(20), resolucion.escalarX(14))));
			setForeground(Color.WHITE);
			setHorizontalAlignment(SwingConstants.LEFT);
			actualizarTexto();
		}
			
			public void actualizarTexto() {
				setText(String.format(
						"Score: %d | Combo: %d | x%d %s | precision %.2f%%", puntos, combo, multiplicador, estado, precision()));
			}
		
		
			public void Perfect() {
				combo++;
				aciertos++;
				totalNotas++;
				if(combo > maxCombo) maxCombo = combo;
				if(combo % 10 == 0) multiplicador++;
				puntos += 100 * multiplicador;
				estado = "PERFECT";
				actualizarTexto();
				
			}
			
			public void Good() {
				combo++;
				aciertos++;
				totalNotas++;
				if (combo > maxCombo) maxCombo = combo;
				if(combo % 10 == 0) multiplicador++;
				puntos += 50 * multiplicador;
				estado = "GOOD";
				actualizarTexto();
			}
			
			public void Miss() {
				combo = 0;
				multiplicador = 1;
				totalNotas++;
				estado = "MISS";
				actualizarTexto();
			}
			
			public double precision() {
				if (totalNotas == 0) return 0.0;
				return (aciertos * 100.0) / totalNotas;
			}
			
			
			public void reiniciarCancion() {
				puntos = 0;
				combo = 0;
				multiplicador = 1;
				maxCombo  = 0;
				aciertos = 0;
				totalNotas = 0;
				estado = "";
				actualizarTexto();
			}
				
		
		public int getPuntos() {
			return puntos;
		}
		
		public int getCombo() {
			return combo;
		}
		
		public int getMultiplicador() {
			return multiplicador;
		}
		
		public int getMaxCombo() {
			return maxCombo;
		}
		
		public String getEstado() {
		 return estado; 
		 }
		
		
	}
