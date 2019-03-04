
package imagethreshold.java;


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;

/**
 *
 * @author TinotendaMatsika
 */
public class ImageThreshold extends Frame implements ActionListener{
    BufferedImage input;
    int width, height;
    TextField texThres, texOffset;
    ImageCanvas source, target;
    PlotCanvas2 plot;
    // Constructor
    public ImageThreshold(String name) {
	  super("Image Histogram");
	  // load image
	  try {
            input = ImageIO.read(new File(name));
	  }
	  catch ( Exception ex ) {
            ex.printStackTrace();
	  }
    width = input.getWidth();
	  height = input.getHeight();
	  // prepare the panel for image canvas.
	  Panel main = new Panel();
	  source = new ImageCanvas(input);
	  plot = new PlotCanvas2(256, 200);
	  target = new ImageCanvas(width, height);
	  //target.copyImage(input);
    target.resetImage(input);
	  main.setLayout(new GridLayout(1, 3, 10, 10));
	  main.add(source);
	  main.add(plot);
	  main.add(target);
	  // prepare the panel for buttons.
	  Panel controls = new Panel();
	  controls.add(new Label("Threshold:"));
	  texThres = new TextField("128", 2);
	  controls.add(texThres);
	  Button button = new Button("Manual Selection");
	  button.addActionListener(this);
	  controls.add(button);
	  button = new Button("Automatic Selection");
    button.addActionListener(this);
	  controls.add(button);
	  button = new Button("Otsu's Method");
	  button.addActionListener(this);
	  controls.add(button);
	  controls.add(new Label("Offset:"));
	  texOffset = new TextField("10", 2);
	  controls.add(texOffset);
	  button = new Button("Adaptive Mean-C");
	  button.addActionListener(this);
	  controls.add(button);
	  // add two panels
	  add("Center", main);
	  add("South", controls);
	  addWindowListener(new ExitListener());
	  setSize(width*2+400, height+100);
	  setVisible(true);
    }

    
    class ExitListener extends WindowAdapter {
	      public void windowClosing(WindowEvent e) {
            System.exit(0);
	      }
    }
    
    // Action listener for button click events
    public void actionPerformed(ActionEvent e) {
    // example -- compute the average color for the image
	      if ( ((Button)e.getSource()).getLabel().equals("Manual Selection") ) {
            int threshold;
            try {
                    threshold = Integer.parseInt(texThres.getText());
            } 
            catch (Exception ex) {
                        texThres.setText("128");
			                  threshold = 128;
            }
            plot.clearObjects();
            plot.addObject(new VerticalBar(Color.BLACK, threshold, 100));
	      }
        else if( ((Button)e.getSource()).getLabel().equals("Automatic Selection") ){
            int[] red = new int[256];
            int[] green = new int[256];
            int[] blue = new int[256];
            for (int y = 0, i= 0 ; y< height;y++){
                for(int x = 0; x < width; x++ , i++){
                    Color clr = new Color(input.getRGB(x, y));
                    red[clr.getRed()]++;
                    green[clr.getGreen()]++;
                    blue[clr.getBlue()]++;
                }
            }
            
            int redThres = 120;
            int newRedThres = redThres ;
            while(true){
                redThres = newRedThres;
                int average1 , average2;
                int total1 = 0 , total2 = 0;
                int counter1 = 0 , counter2 = 0;
                for(int a = 0 ; a < redThres ; a++){
                    counter1 += red[a];
                    total1 += a * red[a];
                }
                average1 = total1/counter1;
                
                for(int a = 255 ; a > redThres ;a--){
                    counter2 += red[a];
                    total2 += a * red[a];
                }
                average2 = total2/counter2;
                newRedThres = (average1 + average2) /2;
                if(Math.abs(redThres-newRedThres) <=5) break;
            }
            
            int greenThres = 120;
            int newGreenThres = greenThres;
            while(true){
                greenThres = newGreenThres;
                int average1 , average2;
                int total1 = 0 ,total2 = 0;
                int counter1 = 0 , counter2 = 0;
                for(int a = 0; a < greenThres; a++){
                    counter1 += green[a];
                    total1 += a * green[a];
                }
                average1 = total1/counter1;
                
                for(int a = 255; a > greenThres; a--){
                    counter2 += green[a];
                    total2 += a * green[a];     
                }
                average2 = total2/counter2;
                newGreenThres = (average1 + average2)/2;
                if(Math.abs(greenThres - newGreenThres) <=5) break;
                
            }
            
            int blueThres = 120;
            int newBlueThres = blueThres;
            while(true){
                blueThres = newBlueThres;
                int average1 , average2;
                int total1 = 0, total2 = 0;
                int counter1= 0 , counter2 = 0;
                for(int a = 0; a < blueThres; a++){
                    counter1 += blue[a];
                    total1 += a * blue[a];
                }
                average1 = total1/counter1;
                
                for(int a =255; a > blueThres; a--){
                    counter2 += blue[a];
                    total2 += a * blue[a];
                }
                average2 = total2/counter2;
                newBlueThres = (average1 + average2)/2;
                if(Math.abs(blueThres - newBlueThres) <=5)break;
                
            }
            
            
            int newRed;
            int newGreen;
            int newBlue;
            
            for ( int y=0, i=0 ; y<height ; y++ ) 
            {
                for ( int x=0 ; x<width ; x++, i++ ) 
                {  
                   Color clr = new Color(input.getRGB(x, y));
                   newRed = clr.getRed();
                   if(newRed > redThres){
                       newRed = 255;
                   }
                   else{
                       newRed = 0;
                   }
                   
                   newGreen = clr.getGreen();
                   if(newGreen > greenThres){
                       newGreen = 255;
                   }
                   else{
                       newGreen = 0;
                   }
                   
                    newBlue = clr.getBlue();
                    if(newBlue > blueThres){
                       newBlue = 255; 
                    }
                    else{
                        newBlue = 0;
                    }
			
                    int pixel = (newRed << 16) | (newGreen << 8) | newBlue;
                    target.image.setRGB(x, y,pixel);
                }
                
            }
	    target.repaint();
        }
    }
    
    
    public static void main(String[] args) {
	new ImageThreshold(args.length==1 ? args[0] : "fingerprint.png");
    }
}
