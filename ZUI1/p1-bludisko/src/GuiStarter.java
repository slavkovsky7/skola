
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GuiStarter extends Frame implements Present{
	private World world;	
	private static Random r = new Random(System.currentTimeMillis());
	private JPanel area;
	private Button b;	
	private Image img_wall = null;
	private Image img_wall2 = null;
	private Image img_wall3 = null;	
	private Image img_clean = null;
	private Image img_clean2 = null;
	private Image img_clean3 = null;	
	private Image img_dirt = null;
	private Image img_dirt2 = null;
	private Image img_dirt3 = null;	
	private Image img_agent = null;
	private Image img_agent2 = null;
	
	public static final int RECT_SIZE = 20;
	
	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		args = new String[] { "MapaLOL.txt","10", "2" };
		if (args.length < 3) return;
		
		GuiStarter s = new GuiStarter();
		
		String file = args[0];
		long w = Long.parseLong(args[1]);
		int p = Integer.parseInt(args[2]);		
		s.world = new World(file,w,p);

		MyAgent agent = new MyAgent(s.world.net.length, s.world.net[0].length);
		s.world.addAgent(agent);											

		s.world.presenter = s;		
		s.init();
		
		s.setVisible(true);
	}

	public void redraw(){
		area.repaint();
	}

	public void init(){
		  //this.setTitle("PLEASE WAIT!");
		  b = new Button("Run");
		  setSize((world.net[0].length+1)*RECT_SIZE,(world.net.length+3)*RECT_SIZE );					  	  
		  //  repaint();		  			  		  
	      Panel p;
	      p = new Panel();
	      p.setSize((world.net.length+1)*RECT_SIZE,(world.net[0].length+1)*RECT_SIZE+30);
	      p.setLayout(new BorderLayout());	      
	      p.add(BorderLayout.NORTH,b);
	      b.addActionListener(new ActionListener(){
			synchronized public void actionPerformed(ActionEvent arg0) {
				b.setEnabled(false);
				world.run();				
			}
	      });

		Toolkit toolkit = getToolkit();
		// create the image using the toolkit
		img_wall = toolkit.createImage("bin/img/img_wall.jpg");
		img_wall2 = toolkit.createImage("bin/img/img_wall2.jpg");		
		img_wall3 = toolkit.createImage("bin/img/img_wall3.jpg");		
		img_clean = toolkit.createImage("bin/img/img_clean.jpg");
		img_clean2 = toolkit.createImage("bin/img/img_clean2.jpg");
		img_clean3 = toolkit.createImage("bin/img/img_clean3.jpg");		
		img_dirt = toolkit.createImage("bin/img/img_dirt.jpg");
		img_dirt2 = toolkit.createImage("bin/img/img_dirt2.jpg");
		img_dirt3 = toolkit.createImage("bin/img/img_dirt3.jpg");		
		img_agent = toolkit.createImage("bin/img/img_agent.jpg");
		img_agent2 = toolkit.createImage("bin/img/img_agent2.jpg");		
		
		area = new JPanel(){
			public void paint(Graphics g) {
				BufferedImage bi = null;
				boolean monitor = false;
				boolean ok = false;
				
				if (world.save){
					bi = new BufferedImage(world.getWidth()*RECT_SIZE, world.getHeight()*RECT_SIZE, BufferedImage.TYPE_INT_RGB);
					g = bi.createGraphics();					
					monitor = true;
				}
				
				for (int i = 0; i < world.net.length; i++) {
					for (int j = 0; j < world.net[i].length; j++) {
						if (world.net[i][j] == World.WALL){
							
							if(i >= world.ay - world.getPerceptSize() && i <= world.ay + world.getPerceptSize() && j >= world.ax - world.getPerceptSize() && j<= world.ax + world.getPerceptSize()){
								g.drawImage(img_wall,RECT_SIZE*j,RECT_SIZE*i,null);								
							} else if (world.v[i][j]){
								g.drawImage(img_wall2,RECT_SIZE*j,RECT_SIZE*i,null);
							} else{
								g.drawImage(img_wall3,RECT_SIZE*j,RECT_SIZE*i,null);
							}							
						}
						if (world.net[i][j] == World.CLEAN){
							if(i >= world.ay - world.getPerceptSize() && i <= world.ay + world.getPerceptSize() && j >= world.ax - world.getPerceptSize() && j<= world.ax + world.getPerceptSize()){
								g.drawImage(img_clean,RECT_SIZE*j,RECT_SIZE*i,null);								
							} else if (world.v[i][j]){
								g.drawImage(img_clean2,RECT_SIZE*j,RECT_SIZE*i,null);
							} else{
								g.drawImage(img_clean3,RECT_SIZE*j,RECT_SIZE*i,null);
							}						
						}
						if (world.net[i][j] == World.DIRTY){
							if(i >= world.ay - world.getPerceptSize() && i <= world.ay + world.getPerceptSize() && j >= world.ax - world.getPerceptSize() && j<= world.ax + world.getPerceptSize()){
								g.drawImage(img_dirt,RECT_SIZE*j,RECT_SIZE*i,null);								
							} else if (world.v[i][j]){
								g.drawImage(img_dirt2,RECT_SIZE*j,RECT_SIZE*i,null);
							} else{
								g.drawImage(img_dirt3,RECT_SIZE*j,RECT_SIZE*i,null);
							}						
						} 
						if (world.anet[i][j] == World.AGENT) {
							if (world.net[i][j] == World.DIRTY)
								g.drawImage(img_agent2,RECT_SIZE*j,RECT_SIZE*i,null);								
							else
								g.drawImage(img_agent,RECT_SIZE*j,RECT_SIZE*i,null);							
						}
					}
				}

				g.setColor(Color.RED);
				g.drawRect(RECT_SIZE*(world.ax - world.getPerceptSize()), RECT_SIZE*(world.ay - world.getPerceptSize()), RECT_SIZE*(1+2*world.getPerceptSize()), RECT_SIZE*(1+2*world.getPerceptSize()));				

				ok = true;
				if (world.save){
					try{
						if (!monitor || !ok){
							return;
						}

						ImageIO.write(bi,"png", new File("screen.png"));
						world.save = false;

						System.out.println("screen successfully created");
					//	System.exit(0);
					}catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		};
	      
	      p.add(BorderLayout.CENTER,area);
	      add(p);
	      addWindowListener(new WindowAdapter(){
	    	 public void windowClosing(WindowEvent arg0) {
	    		System.exit(0);
	    	
	    	} 
	      });
	}

}