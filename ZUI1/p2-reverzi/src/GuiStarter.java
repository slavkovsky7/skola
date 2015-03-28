import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GuiStarter extends Frame implements Present{

	public World world;
	public static JPanel area;
	Button run;	
	private Image img_clean = null;
	private Image img_black = null;
	private Image img_white = null;
	private Image img_blackh = null;
	private Image img_whiteh= null;	

	public static void main(String[] args) throws Throwable {
		/*if (args.length < 1){
			printUsage();
			return;
		}*/
		
		GuiStarter s = new GuiStarter();
		long w = 0;
		s.world = new World(w);
				
		s.world.presenter = s;
	    s.init();
        s.setVisible(true);	           
	}


	private static void printUsage() {
		System.out.println("one argument needed");
		System.out.println("GuiStarter <WAIT-TIME>");		
	}

	public void redraw(){
		area.repaint();
	}

	public void initng(int pocethier){
		for(int i=0; i<pocethier; i++) {
			world.runng();
			world.reset();
		}
		System.out.println("games = "+world.games);
		System.out.println("black wins = "+world.blackwins);
		System.out.println("white wins = "+world.whitewins);
		System.out.println("draw = "+world.nowin);
	}

	public void init(){

		  setSize(522,567);
		  repaint();
	      Panel p;
	      p = new Panel();
	      p.setSize(590,610);
	      p.setLayout(new BorderLayout());
	      run = new Button("Run");	      

	      p.add(BorderLayout.NORTH, run);      

	      run.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				run.setEnabled((false));
				setTitle("Running");
				world.run();
//				world.check();				
			}

	      });      

		Toolkit toolkit = getToolkit();
		// create the image using the toolkit
		img_black = toolkit.createImage("resorces/img/cierny.jpg");	
		img_white = toolkit.createImage("resorces/img/biely.jpg");		
		img_clean = toolkit.createImage("resorces/img/prazdne.jpg");
		img_whiteh = toolkit.createImage("resorces/img/bielyhint.jpg");	
	    img_blackh = toolkit.createImage("resorces/img/ciernyhint.jpg");
	    		
		area = new JPanel(){
			public void paint(Graphics g) {

				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if (world.net[i][j] == 0){
							g.drawImage(img_clean,i*63,j*63,null);	
						}
						if (world.net[i][j] == 1){
							g.drawImage(img_white,i*63,j*63,null);	
						} 
						if (world.net[i][j] == 2){
							g.drawImage(img_black,i*63,j*63,null);						
						}						
						if (world.net[i][j] == 3){
							g.drawImage(img_whiteh,i*63,j*63,null);	
						}
						if (world.net[i][j] == 4){
							g.drawImage(img_blackh,i*63,j*63,null);	
						}

					}
				}
			}
		};
		
		 p.add(BorderLayout.CENTER,area);
	      add( p);
	      addWindowListener(new WindowAdapter(){
	    	 public void windowClosing(WindowEvent arg0) {
	    		System.exit(0);
	    	
	    	} 
	      });
	      repaint();
	}
	
}
