// BV Ue1 WS2021/22 Vorgabe
//
// Copyright (C) 2021 by Klaus Jung
// All rights reserved.
// Date: 2021-07-14
 		   		     	

package bv_ws2122;

public class GaussFilter {
 		   		     	
	private double[][] kernel;
 		   		     	
	public double[][] getKernel() {
		return kernel;
	}

	public void apply(RasterImage src, RasterImage dst, int kernelSize, double sigma) {
 		   		     	
		// TODO: Implement a Gauss filter of size "kernelSize" x "kernelSize" with given "sigma"
		
		// Step 1: Allocate appropriate memory for the field variable "kernel" representing a 2D array.
		kernel = new double[kernelSize][kernelSize];
		
		// Step 2: Fill in appropriate values into the "kernel" array.
		int hotspot = (kernelSize/2);
		double sum = 0;
		for (int x = 0; x < kernelSize; x ++) {
			for (int y = 0; y < kernelSize; y++) {
				double d = (Math.sqrt((Math.pow(Math.abs(y-hotspot), 2) + (Math.pow(Math.abs(x-hotspot), 2))))); // sqrt(delta y^2 + delta x^2) (Abstand des aktuellen Pixels zum Hotspot
				kernel[x][y] = Math.pow(Math.E, ((-Math.pow(d, 2)) / (2 * Math.pow(sigma, 2))));
				sum = sum + kernel[x][y];
			}
		}
		// Hint:
		// Use g(d) = e^(- d^2 / (2 * sigma^2)), where d is the distance of a coefficient's position to the hot spot.
		// Note that in this comment e^ denotes the exponential function and ^2 the square. In Java ^ is a different operator. 
		
		// Step 3: Normalize the "kernel" such that the sum of all its values is one.
		for (int x = 0; x < kernelSize; x ++) {
			for (int y = 0; y < kernelSize; y++) {
				kernel[x][y] = kernel[x][y] / sum;
			}
		}	
		
		// Step 4: Apply the filter given by "kernel" to the source image "src". The result goes to image "dst".
		// Use "constant continuation" for boundary processing.
		
//		for(int posx = 0; posx < src.width; posx ++) {
//			for(int posy = 0; posy < src.height; posy ++) {
//				int pos = posy * src.width + posx;
//				int gray; // = src.argb[pos] & 0xff;
//				if(!(posx < hotspot && posx > src.width - hotspot 
//				   && posy < hotspot && posy > src.height - hotspot)) {
//					double newvalue = 0;
//					for(int k = - hotspot; k < hotspot; k++) {
//						for(int l = - hotspot; l < hotspot; l++) {
//							newvalue = newvalue + src.argb[(posx + k) + (posy + l) * src.width] * kernel[k+hotspot][l+hotspot];
//						}
//					}
//					gray = (int) newvalue;
//					dst.argb[pos] = 0xff000000 | gray << 16 | gray << 8 | gray;
//				} else {
//					dst.argb[pos] = 0xff000000; // | r << 16 | g << 8 | b;
//				}
//			}
//		} 
		//durch das src-Bild loopen
				for(int x = 0; x < src.width; x++) {
					for(int y = 0; y < src.height; y++) {
						int pos = y * src.width + x;
				/*		if(x < hotspot || x >= src.width - hotspot
								|| y < hotspot || y >= src.height - hotspot) {
							//Rand zunächst auf scharz setzen
							dst.argb[pos] = 0xff000000;	
						} else { 
				*/
							int newvalue = 0;
							int rn = 0;
							int gn = 0;
							int bn = 0;
							//durch den Kernel loopen
							for(int k = -hotspot; k <= hotspot; k++) {
								for(int l = -hotspot; l <= hotspot; l++) {
									//Randbehandlung
									int argb = 0;
									if (x+k >= src.width | x+k < 0 | y+l >= src.height| y+l < 0){
										//kernelelemente in -y richtung = -y + 1 wert
										if(x+k >= src.width && (y+l)<0) { //top right corner
											argb = src.argb[(src.width - 1)];
										}
										if(x+k >= src.width && (y+l)<src.height && (y+l)>=0) { //right side
											argb = src.argb[(src.width -1) + src.width * (y+l)];
										}
										if(x+k >= src.width && (y+l)>=src.height) { //bottom right corner
											argb = src.argb[(src.width - 1) + src.width * (src.height-1)];
										}
										if(y+l >= src.height && (x+k)<src.width && (x+k)>=0) { //bottom side
											argb = src.argb[(src.width -1) + src.width * (src.height-1)];
										}
										if(y+l >= src.height && (x+k)<0) { //bottom left corner
											argb = src.argb[1 + src.width * (src.height-1)];
										}
										if(x+k < 0 && (y+l)<src.height && (y+l)>=0) { //left side
											argb = src.argb[1 + src.width * (y+l)];
										}
										if(y+l < 0 && (x+k)<0) { //top left corner
											argb = src.argb[0];
										}
										if(y+l < 0 && (x+k)<src.width && (x+k)>=0) { //top side
											argb = src.argb[(x+k)];
										}
										//kernelelemente in -x richtung = -x + 1 wert
									}
									else {
										//newvalue = (int) (newvalue + src.argb[(x + k) + (y+l) * src.width] * kernel[k+hotspot][l+hotspot]);
										argb = src.argb[(x + k) + (y+l) * src.width];
										
									}
									int r = (argb >> 16) & 0xff;
									int g = (argb >>  8) & 0xff;
									int b =  argb & 0xff; 
									
									rn = (int) (Math.round(rn + r * kernel[k+hotspot][l+hotspot]));
									gn = (int) (Math.round(gn + g * kernel[k+hotspot][l+hotspot]));
									bn = (int) (Math.round(bn + b * kernel[k+hotspot][l+hotspot]));
									
									
								}
				//			}
								
							rn = Math.min(255, Math.max(0, rn));
							gn = Math.min(255, Math.max(0, gn));
							bn = Math.min(255, Math.max(0, bn));
							//grayscale
							newvalue = (rn+gn+bn)/3;
							
							//in das neue Bild einsetzen
							dst.argb[pos] = (0xff<<24) | (newvalue << 16) | (newvalue << 8) | newvalue;
							
						}
					}
				}
	}
		     	

}
 		   		     	




