package smoothingfilter.java;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
/**
 *
 * @author TinotendaMatsika
 */
public class SmoothingFilter extends Frame implements ActionListener{
    BufferedImage input;
    ImageCanvas source,target;
    TextField texSigma;
    int width,height;
    //Construcot
    public SmoothingFilter(String name){
        super("Smoothing Filters");
        //load image
        try 
        {
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
	target = new ImageCanvas(input);
	main.setLayout(new GridLayout(1, 2, 10, 10));
	main.add(source);
	main.add(target);
	// prepare the panel for buttons.
	Panel controls = new Panel();
	Button button = new Button("Add noise");
	button.addActionListener(this);
	controls.add(button);
	button = new Button("5x5 mean");
	button.addActionListener(this);
	controls.add(button);
	controls.add(new Label("Sigma:"));
	texSigma = new TextField("1", 1);
	controls.add(texSigma);
	button = new Button("5x5 Gaussian");
	button.addActionListener(this);
	controls.add(button);
	button = new Button("5x5 median");
	button.addActionListener(this);
	controls.add(button);
	button = new Button("5x5 Kuwahara");
	button.addActionListener(this);
	controls.add(button);
	// add two panels
	add("Center", main);
	add("South", controls);
	addWindowListener(new ExitListener());
	setSize(width*2+100, height+100);
	setVisible(true);   
    }
    class ExitListener extends WindowAdapter{
        public void windowClosing(WindowEvent e) {
            System.exit(0);
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // example -- add random noise
	if ( ((Button)e.getSource()).getLabel().equals("Add noise") ) {
            Random rand = new Random();
            int dev = 64;
            for ( int y=0, i=0 ; y<height ; y++ )
		for ( int x=0 ; x<width ; x++, i++ ) {
                    Color clr = new Color(source.image.getRGB(x, y));
                    int red = clr.getRed() + (int)(rand.nextGaussian() * dev);
                    int green = clr.getGreen() + (int)(rand.nextGaussian() * dev);
                    int blue = clr.getBlue() + (int)(rand.nextGaussian() * dev);
                    red = red < 0 ? 0 : red > 255 ? 255 : red;
                    green = green < 0 ? 0 : green > 255 ? 255 : green;
                    blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;
                    source.image.setRGB(x, y, (new Color(red, green, blue)).getRGB());
		}
            source.repaint();
        }
        else if( ((Button)e.getSource()).getLabel().equals("5x5Mean") ){
            
            int[][] f = new int[height][width];
            for(int i =0; i< height;i++){
                for(int j=0; j<width;j++){
                   f[i][j] = source.image.getRGB(j, i);
                }
            }
            int[][] output = new int[height][width];
            int[][] g = new int[height][width];
            
            
            for(int q=5; q <height-5;q++){
                for(int p=5; p <width-5;p++){
                    
                    int sum=0;
                    for(int v=-5; v<=5;v++)
                        for(int u=-5; u<=5;u++){
                          // int pixel = f[q+u][p+v];
                           sum += f[q+u][p+v];      
                    g[q][p] = sum/((2*5+1)*(2*5+1)); 
                        } 
                }
  
            }
           

            for(int y=0, i=0 ; y<height ; y++)
                for(int x=0 ; x<width ; x++, i++){
                int newPixel = g[y][x];  
                target.image.setRGB(x,y,newPixel);
                }  
            target.repaint(); 
        }
        else if(((Button)e.getSource()).getLabel().equals("5x5Median")){
            int[] red = new int[26] ;
            int[] green = new int[26];
            int[] blue = new int[26];
            
            
            //the boundaries on which the filter can work in
            int xMin,xMax,yMin,yMax;
            
            
            for ( int y=0, i=0 ; y<height ; y++ ){
		for ( int x=0 ; x<width ; x++, i++ ) {
                   
                xMin = x - (25/2);
                xMax = x + (25/2);
                yMin = y - (25/2);
                yMax = y + (25/2);
                
                //we must handle special cases outside the border
                if(xMin < 0)
                    xMin =0;
                if(xMax >(width-1))
                    xMax = width -1;
                if(yMin < 0)
                    yMin =0;
                if(yMax > (height-1))
                    yMax = height -1;
                
                int size = (xMax-xMin+1) * (yMax-yMin+1);
                int v =0;
                int[] f= new int[size];
                for(int n = xMin;n < xMin;n++)
                    for(int m = yMin;m < yMin;m++){
                       f[v] = source.image.getRGB(x, y);
                    }
                
                for(int p=0; p < f.length;p++){
                    Color clr = new Color(source.image.getRGB(x, y));
                    red[p] = clr.getRed() + f[p];
                    green[p] = clr.getGreen() + f[p];
                    blue[p] = clr.getBlue() + f[p];
                }
                Arrays.sort(red);
                Arrays.sort(green);
                Arrays.sort(blue);
                
                int newRed = red[13];
                int newGreen = green[13];
                int newBlue = blue[13];
                target.image.setRGB(x, y,(new Color(newRed,newGreen,newBlue)).getRGB());
                }
            
            }
            target.repaint();
            
           
        }
        else if(((Button)e.getSource()).getLabel().equals("Kuwahara")){
            int output[][]= new int[height][width];
            
            int w = 2;
            for(int y=0,i=0;y<height;y++)
                for(int x=0;x<width;x++){
                    
                }
            
        }
    }
    
    public static void main(String[] args) {
	new SmoothingFilter(args.length==1 ? args[0] : "baboon.png");
    }
    
    
}
