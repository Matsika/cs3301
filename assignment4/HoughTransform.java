package houghtransform.java;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.ArrayList;


public class HoughTransform extends Frame implements ActionListener {
    BufferedImage input;
    int width, height, diagonal;
    ImageCanvas source, target;
    TextField texRad, texThres;
    
    
    // Constructor
    public HoughTransform(String name) {
        super("Hough Transform");
        // load image
        try {
            input = ImageIO.read(new File(name));
        }
        catch ( Exception ex ) {
            ex.printStackTrace();
        }
            width = input.getWidth();
            height = input.getHeight();
            diagonal = (int)Math.sqrt(width * width + height * height);
            // prepare the panel for two images.
            Panel main = new Panel();
            source = new ImageCanvas(input);
            target = new ImageCanvas(input);
            main.setLayout(new GridLayout(1, 2, 10, 10));
            main.add(source);
            main.add(target);
            // prepare the panel for buttons.
            Panel controls = new Panel();
            Button button = new Button("Line Transform");
            button.addActionListener(this);
            controls.add(button);
            controls.add(new Label("Radius:"));
            texRad = new TextField("10", 3);
            controls.add(texRad);
            button = new Button("Circle Transform");
            button.addActionListener(this);
            controls.add(button);
            controls.add(new Label("Threshold:"));
            texThres = new TextField("25", 3);
            controls.add(texThres);
            button = new Button("Search");
            button.addActionListener(this);
            controls.add(button);
            // add two panels
            add("Center", main);
            add("South", controls);
            addWindowListener(new ExitListener());
            setSize(diagonal*2+100, Math.max(height,360)+100);
            setVisible(true);
        }
        
    class ExitListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
    
    // Action listener
    public void actionPerformed(ActionEvent e) {
        // perform one of the Hough transforms if the button is clicked.
        if ( ((Button)e.getSource()).getLabel().equals("Line Transform") ) {
            int[][] g = new int[360][diagonal+1];
            // insert your implementation for straight-line here.
            int maxTheta = 360;
            double moveTheta = (Math.PI*2)/maxTheta;
            int count=0;
            double[] sinValues = new double[maxTheta];
            double[] cosValues = new double[maxTheta];
            for(int x = 0; x < maxTheta; x++){
                double theta = x * moveTheta;
                sinValues[x] = Math.sin(theta);
                cosValues[x] = Math.cos(theta);
            }
            
            for (int x=0; x < width; x++) {
		for (int y=0; y < height; y++) {
                    if ((source.image.getRGB(x, y) & 0x000000ff) == 0) {
			for (int theta=0; theta < 360; theta++) {
                            int p = (int)((x)*cosValues[theta] + (y)*sinValues[theta])/2;
                            p += diagonal / 2;
                            if (p < 0 || p >= diagonal) {continue;}
				g[theta][p]++;
                            }
			}
                    count++;
		}
            }
            
            
            DisplayTransform(diagonal, 360, g);
        }
        else if ( ((Button)e.getSource()).getLabel().equals("Circle Transform") ) {
            int[][] g = new int[height][width];
            int radius = Integer.parseInt(texRad.getText());
            // insert your implementation for circle here.
		for(int rad = 0; rad < radius; rad++){
                for(int x =0; x < width; x++){
                    for(int y=0; y < height; y++){
                        if ((source.image.getRGB(x, y) & 0x000000ff) == 0){
                            for(int theta = 0; theta < 360; theta++){
                                int a = (int) Math.round(x - radius * Math.cos(theta * (Math.PI *2) / 180));
                                int b = (int) Math.round(y - radius * Math.sin(theta * (Math.PI *2) / 180));
                                int r = (int) Math.pow((x-a), 2) + (int) Math.pow((y-b), 2);

                                int rSquared = r*r;
                                int rr = (int) Math.sqrt(rSquared);
                                
                                if((b >=0) && (b < height) && (a >=0) && (a < width)){
                                    g[a][b]++;
                                }
                            } 
                        }
                    count++;
                    }
                }
            }
            DisplayTransform(width, height, g);
        }
    }
    
    // display the spectrum of the transform.
    public void DisplayTransform(int wid, int hgt, int[][] g) {
        target.resetBuffer(wid, hgt);
        for ( int y=0, i=0 ; y<hgt ; y++ )
            for ( int x=0 ; x<wid ; x++, i++ )
            {
		int value = g[y][x] > 255 ? 255 : g[y][x];
		target.image.setRGB(x, y, new Color(value, value, value).getRGB());
            }
	target.repaint();
	}
    
    
    public static void main(String[] args) {
	new HoughTransform(args.length==1 ? args[0] : "rectangle.png");
    }
}
        
