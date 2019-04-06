import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

// Main class
public class CornerDetection extends Frame implements ActionListener {
	BufferedImage input;
	int width, height;
	double sensitivity=.1;
	int threshold=20;
	ImageCanvas source, target;
	CheckboxGroup metrics = new CheckboxGroup();
	// Constructor
	public CornerDetection(String name) {
		super("Corner Detection");
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
		target = new ImageCanvas(width, height);
		main.setLayout(new GridLayout(1, 2, 10, 10));
		main.add(source);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Derivatives");
		button.addActionListener(this);
		controls.add(button);
		// Use a slider to change sensitivity
		JLabel label1 = new JLabel("sensitivity=" + sensitivity);
		controls.add(label1);
		JSlider slider1 = new JSlider(1, 25, (int)(sensitivity*100));
		slider1.setPreferredSize(new Dimension(50, 20));
		controls.add(slider1);
		slider1.addChangeListener(changeEvent -> {
			sensitivity = slider1.getValue() / 100.0;
			label1.setText("sensitivity=" + (int)(sensitivity*100)/100.0);
		});
		button = new Button("Corner Response");
		button.addActionListener(this);
		controls.add(button);
		JLabel label2 = new JLabel("threshold=" + threshold);
		controls.add(label2);
		JSlider slider2 = new JSlider(0, 100, threshold);
		slider2.setPreferredSize(new Dimension(50, 20));
		controls.add(slider2);
		slider2.addChangeListener(changeEvent -> {
			threshold = slider2.getValue();
			label2.setText("threshold=" + threshold);
		});
		button = new Button("Thresholding");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Non-max Suppression");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Display Corners");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(Math.max(width*2+100,850), height+110);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {
		// generate Moravec corner detection result
		if ( ((Button)e.getSource()).getLabel().equals("Derivatives") )
			derivatives();
	}
	public static void main(String[] args) {
		new CornerDetection(args.length==1 ? args[0] : "signal_hill.png");
	}

	// moravec implementation
	void derivatives() {
		int l, t, r, b, dx, dy;
		Color clr1, clr2;
		int gray1, gray2;

		for ( int q=0 ; q<height ; q++ ) {
			t = q==0 ? q : q-1;
			b = q==height-1 ? q : q+1;
			for ( int p=0 ; p<width ; p++ ) {
				l = p==0 ? p : p-1;
				r = p==width-1 ? p : p+1;
				clr1 = new Color(source.image.getRGB(l,q));
				clr2 = new Color(source.image.getRGB(r,q));
				gray1 = clr1.getRed() + clr1.getGreen() + clr1.getBlue();
				gray2 = clr2.getRed() + clr2.getGreen() + clr2.getBlue();
				dx = (gray2 - gray1) / 3;
				clr1 = new Color(source.image.getRGB(p,t));
				clr2 = new Color(source.image.getRGB(p,b));
				gray1 = clr1.getRed() + clr1.getGreen() + clr1.getBlue();
				gray2 = clr2.getRed() + clr2.getGreen() + clr2.getBlue();
				dy = (gray2 - gray1) / 3;
				dx = Math.max(-128, Math.min(dx, 127));
				dy = Math.max(-128, Math.min(dy, 127));
				target.image.setRGB(p, q, new Color(dx+128, dy+128, 128).getRGB());
			}
		}
		target.repaint();
	}
	
	//harrison implementation
        public int[][][] Dog(){
            int a2, a1, b2, b1, c1, c2,d1, d2,dx,dy;
            
            int[][][] x2_y2_xy = new int [width][height][3];
            for(int q=0; q < height; q++){
                b1 = q==0 ? q : q-1;
                b2 = q<=1 ? q : q-2;
                d1 = q== height-1 ? q : q+1;
                d2 = q== height-2 ? q : q+2;
                for(int p=0; p < width; p++){
                    a1 = p==0 ? p : p-1;
                    a2 = p<=1 ? p : p-2;
                    c1 = p == width-1 ? p : p+1;
                    c2 = p >= width-2 ? p : p+2;
                    
                    Color clr_a2d2 = new Color(source.image.getRGB(a2, d2));
                    Color clr_a2d1 = new Color(source.image.getRGB(a2, d1));
                    Color clr_a2q = new Color(source.image.getRGB(a2, q));
                    Color clr_a2b1 = new Color(source.image.getRGB(a2, b1));
                    Color clr_a2b2 = new Color(source.image.getRGB(a2, b2));
                    
                    Color clr_a1d2 = new Color(source.image.getRGB(a1, d2));
                    Color clr_a1d1 = new Color(source.image.getRGB(a1, d1));
                    Color clr_a1q = new Color(source.image.getRGB(a1, q));
                    Color clr_a1b1 = new Color(source.image.getRGB(a1, b1));
                    Color clr_a1b2 = new Color(source.image.getRGB(a1, b2));
                    
                    Color clr_pd2 = new Color(source.image.getRGB(p, d2));
                    Color clr_pd1 = new Color(source.image.getRGB(p, d1));
                    Color clr_pq = new Color(source.image.getRGB(p, q));
                    Color clr_pb1 = new Color(source.image.getRGB(p, b1));
                    Color clr_pb2 = new Color(source.image.getRGB(p, b2));
                    
                    Color clr_c1d2 = new Color(source.image.getRGB(c1, d2));
                    Color clr_c1d1 = new Color(source.image.getRGB(c1, d1));
                    Color clr_c1q = new Color(source.image.getRGB(c1, q));
                    Color clr_c1b1 = new Color(source.image.getRGB(c1, b1));
                    Color clr_c1b2 = new Color(source.image.getRGB(c1, b2));
                    
                    Color clr_c2d2 = new Color(source.image.getRGB(c2, d2));
                    Color clr_c2d1 = new Color(source.image.getRGB(c2, d1));
                    Color clr_c2q = new Color(source.image.getRGB(c2, q));
                    Color clr_c2b1 = new Color(source.image.getRGB(c2, b1));
                    Color clr_c2b2 = new Color(source.image.getRGB(c2, b2));
                    
                    int gray_a2d2 = (clr_a2d2.getRed() + clr_a2d2.getGreen() + clr_a2d2.getBlue())/3;
                    int gray_a2d1 = (clr_a2d1.getRed() + clr_a2d1.getGreen() + clr_a2d1.getBlue())/3;
                    int gray_a2q = (clr_a2q.getRed() + clr_a2q.getGreen() + clr_a2q.getBlue())/3;
                    int gray_a2b1 = (clr_a2b1.getRed() + clr_a2b1.getGreen() + clr_a2b1.getBlue())/3;
                    int gray_a2b2 = (clr_a2b2.getRed() + clr_a2b2.getGreen() + clr_a2b2.getBlue())/3;
                    
                    int gray_a1d2 = (clr_a1d2.getRed() + clr_a1d2.getGreen() + clr_a1d2.getBlue())/3;
                    int gray_a1d1 = (clr_a1d1.getRed() + clr_a1d1.getGreen() + clr_a1d1.getBlue())/3;
                    int gray_a1q = (clr_a1q.getRed() + clr_a1q.getGreen() + clr_a1q.getBlue())/3;
                    int gray_a1b1 = (clr_a1b1.getRed() + clr_a1b1.getGreen() + clr_a1b1.getBlue())/3;
                    int gray_a1b2 = (clr_a1b2.getRed() + clr_a1b2.getGreen() + clr_a1b2.getBlue())/3;
                    
                    int gray_pd2 = (clr_pd2.getRed() + clr_pd2.getGreen() + clr_pd2.getBlue())/3;
                    int gray_pd1 = (clr_pd1.getRed() + clr_pd1.getGreen() + clr_pd1.getBlue())/3;
                    int gray_pq = (clr_pq.getRed() + clr_pq.getGreen() + clr_pq.getBlue())/3;
                    int gray_pb1 = (clr_pb1.getRed() + clr_pb1.getGreen() + clr_pb1.getBlue())/3;
                    int gray_pb2 = (clr_pb2.getRed() + clr_pb2.getGreen() + clr_pb2.getBlue())/3;
                    
                    int gray_c1d2 = (clr_c1d2.getRed() + clr_c1d2.getGreen() + clr_c1d2.getBlue())/3;
                    int gray_c1d1 = (clr_c1d1.getRed() + clr_c1d1.getGreen() + clr_c1d1.getBlue())/3;
                    int gray_c1q = (clr_c1q.getRed() + clr_c1q.getGreen() + clr_c1q.getBlue())/3;
                    int gray_c1b1 = (clr_c1b1.getRed() + clr_c1b1.getGreen() + clr_c1b1.getBlue())/3;
                    int gray_c1b2 = (clr_c1b2.getRed() + clr_c1b2.getGreen() + clr_c1b2.getBlue())/3;
                    
                    int gray_c2d2 = (clr_c2d2.getRed() + clr_c2d2.getGreen() + clr_c2d2.getBlue())/3;
                    int gray_c2d1 = (clr_c2d1.getRed() + clr_c2d1.getGreen() + clr_c2d1.getBlue())/3;
                    int gray_c2q = (clr_c2q.getRed() + clr_c2q.getGreen() + clr_c2q.getBlue())/3;
                    int gray_c2b1 = (clr_c2b1.getRed() + clr_c2b1.getGreen() + clr_c2b1.getBlue())/3;
                    int gray_c2b2 = (clr_c2b2.getRed() + clr_c2b2.getGreen() + clr_c2b2.getBlue())/3;
                    
                    dx = (gray_c2b2 + 4 * gray_c2b1 + 7* gray_c2q + 4 * gray_c2d1 + gray_c2d2
                            + 2 * gray_c1b2 + 10 * gray_c1b1 + 17 * gray_c1q + 10 * gray_c1d1 + 2 * gray_c1d2
                            -2 * gray_a1b2 + 10 * gray_a1b1 - 17 * gray_a1q + 10 * gray_a1d1 + 2 * gray_a1d2
                            - gray_a2d2 - 4 * gray_a2d1 - 7 * gray_a2q - 4 * gray_a2d1 - gray_a2d2)/116;
                    
                    dy = (gray_c2b2 + 4 * gray_c2b1 + 7* gray_c2q + 4 * gray_c2d1 + gray_c2d2
                            + 2 * gray_c1b1 + 10 * gray_c1b1 + 17 * gray_pd1 + 10 * gray_c1d1 + 2 * gray_a2b1
                            -2 * gray_c2d1 + 10 * gray_c2d1 - 17 * gray_a1q + 10 * gray_a1d1 + 2 * gray_a1d2
                            - gray_c2d2 - 4 * gray_c1d2 - 7 * gray_pd2 - 4 * gray_a1d2 - gray_a2d2)/116;
                    
    
                    
                    x2_y2_xy[p][q][0] = dx * dx;
                    x2_y2_xy[p][q][1] = dy * dy;
                    x2_y2_xy[p][q][2] = Math.abs(dx*dy);
                }
                
                
            }
            
            return x2_y2_xy;
            
        }
	
	void dog(){
            int l, t, r, b, dx, dy;
            Color clr1, clr2;
            int gray1, gray2;
            for(int q =0; q < height;q++){
                t = q==0 ? q : q-1;
		b = q==height-1 ? q : q+1;
                for(int p=0; p < width; p++){
                    l = p==0 ? p : p-1;
                    r = p==width-1 ? p : p+1;
                    clr1 = new Color(source.image.getRGB(l,q));
                    clr2 = new Color(source.image.getRGB(r,q));
                    gray1 = clr1.getRed() + clr1.getGreen() + clr1.getBlue();
                    gray2 = clr2.getRed() + clr2.getGreen() + clr2.getBlue();
                    dx = (gray2 - gray1) / 3;
                    clr1 = new Color(source.image.getRGB(p,t));
                    clr2 = new Color(source.image.getRGB(p,b));
                    gray1 = clr1.getRed() + clr1.getGreen() + clr1.getBlue();
                    gray2 = clr2.getRed() + clr2.getGreen() + clr2.getBlue();
                    dy = (gray2 - gray1) / 3;
                    dx = Math.max(-128, Math.min(dx, 127));
                    dy = Math.max(-128, Math.min(dy, 127));
                    ix2[p][q] = dx * dx;
                    iy2[p][q] = dy * dy;
                    ixy[p][q] = dx * dy;
                    int red,green,blue;
                    red = ix2[p][q];
                    green = iy2[p][q];
                    blue = ixy[p][q];
                    if(red > 255){red = 255;}
                    if(red < 0) {red = 0;}
                    if(green > 255){green = 255;}
                    if(green < 0){green = 0;}
                    if(blue > 255){blue = 255;}
                    if(blue < 0){blue = 0;}
                    target.image.setRGB(p,q,new Color(red,green,blue).getRGB());
                    
                }
                
            }
            target.repaint();
            
        }
}
