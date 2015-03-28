import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class World {

	public static Random r = new Random(new Date().getTime());
	private long WAIT = 10;

	public Present presenter;
	public boolean running;	
	public int net[][];
    public MyAgentb Black;
    public MyAgentw White;
    public int blackwins;
    public int whitewins;
    public int nowin;
    public int games;
    boolean rungame;
    final int TIME_LIMIT = 100;
    boolean timeoutWhite = false;
    boolean timeoutBlack = false;    
    
    final static int WHITE_PLAYER = 1;
    final static int BLACK_PLAYER = 2;    
   
	List<Integer> possiblemoves;
	
	public World() {
		whitewins = 0;
		blackwins = 0;
		games = 0;
		nowin = 0;

		rungame= true;
		Black = new MyAgentb(this);
		White = new MyAgentw(this);
		possiblemoves = new ArrayList<Integer>();
		net = new int[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++) net[i][j] = 0;
		net[3][3] = WHITE_PLAYER;
		net[3][4] = BLACK_PLAYER;
		net[4][3] = BLACK_PLAYER;
		net[4][4] = WHITE_PLAYER;

	}
	
	public World(long wait) {
		whitewins =0;
		blackwins =0;
		games = 0;
		nowin = 0;
		
		this.WAIT=wait;
		rungame= true;
		Black = new MyAgentb(this);
		White = new MyAgentw(this);
		possiblemoves = new ArrayList<Integer>();
		net = new int[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++) net[i][j] = 0;
		net[3][3] = WHITE_PLAYER;
		net[3][4] = BLACK_PLAYER;
		net[4][3] = BLACK_PLAYER;
		net[4][4] = WHITE_PLAYER;

	}
	
	public void reset(){
		int b=0;
		int w=0;
		games++;
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++) {
				if(net[i][j] == 2) b++;
				if(net[i][j] == 1) w++;
				net[i][j] = 0;
			}
		net[3][3] = WHITE_PLAYER;
		net[3][4] = BLACK_PLAYER;
		net[4][3] = BLACK_PLAYER;
		net[4][4] = WHITE_PLAYER;
		possiblemoves.clear();
		if(b>w) blackwins++;
		if(b<w) whitewins++;
		if(b==w) nowin++;
		Black = new MyAgentb(this);
		White = new MyAgentw(this);
		rungame = true;
	}
	
	// vrati protihraca
	public int oponent(int player) {
		return player==WHITE_PLAYER?BLACK_PLAYER:WHITE_PLAYER;
	}
	
	// skontroluje ci x a y ukazuju do netu
	public boolean isOkCoord(int x, int y) {
		if (x<0) return false;
		if (y<0) return false;
		if (x>7) return false;
		if (y>7) return false;
		return true;
	}
	
	// skontroluje policko, ci tam moze player polozit kamen
	public boolean validmove(int policko, int player, int[][] net) {
		int x = policko / 10;       // stlpec
		int y = policko % 10;       // riadok
		int op = oponent(player);	// farba oponenta
		
		// smery na preskumanie 
		int dirx[] = { 0,  1,  1,  1,  0, -1, -1, -1};
		int diry[] = {-1, -1,  0,  1,  1,  1,  0, -1};
		
		// pokracujeme len ak policko je prazdne
		if (net[x][y]==1 || net[x][y]==2) return false;   // pozn.: 3 a 4 su napovedne policka
		
		// skontrolujeme, ci niektory smer nam dovoluje polozit kamen
		for (int d=0; d<dirx.length; d++) {
			int tx = x;		// pomocne premenne na pracu so suradnicami
			int ty = y;		// pomocne premenne na pracu so suradnicami
			
			// susedne policko musi byt opacnej farby
			tx += dirx[d];
			ty += diry[d];
			if (!isOkCoord(tx, ty) || net[tx][ty]!=op) continue;
			
			// este moze nasledovat zopar figurok oponenta
			tx += dirx[d];
			ty += diry[d];
			while (isOkCoord(tx, ty) && net[tx][ty]==op) {
				tx += dirx[d];
				ty += diry[d];
			}
			
			// teraz musi byt nasa figurka, ak tam je, tak pozicia je validna
			if (isOkCoord(tx, ty) && net[tx][ty]==player) return true;		
		}
		
		// ziadny smer nevyhovoval, returnujeme false
		return false;
	}

	// procedura pre daneho hraca vrati pole moznych tahov
	public void validmoves(int player){
		int policko = -1;
		possiblemoves.clear();
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++) {
				policko = (i*10)+j;
				if((net[i][j]==3)||(net[i][j]==4)) net[i][j]=0;
				if(validmove(policko,player, net)) { 
					possiblemoves.add(policko);
					net[i][j]=player+2;
				}
			}
	}
	
	public void move(int player){
		int x=0;
		int y=0;
		int policko = -1;  // index do pola moznych tahov
		Integer[] vm = possiblemoves.toArray(new Integer[possiblemoves.size()]); 
	
		int[] vmok = new int[possiblemoves.size()];
	    int i = 0;
	    for (Integer e : possiblemoves)  
	        vmok[i++] = e.intValue();
	    
	    int tt = 0;
		if(vm.length > 0) {
				if(player == WHITE_PLAYER) {
					long time = System.currentTimeMillis();
					policko = White.act(net,vmok,TIME_LIMIT);
					long endTime = System.currentTimeMillis();
					timeoutWhite = (endTime - time) > TIME_LIMIT;
					if (timeoutWhite){
						return;
					}
					if ((vm[policko]<0) && (vm[policko]>vmok.length-1)) {
						tt=-1;
					} else {
						tt =vm[policko];
						x=tt /10;
						y= tt -(10*x);
						net[x][y]=1;
					}
				
				// vypise chybovu hlasku
				}
			if(player == BLACK_PLAYER) {
				long time = System.currentTimeMillis();				
				policko = Black.act(net,vmok,TIME_LIMIT);
				long endTime = System.currentTimeMillis();
				timeoutBlack = (endTime - time) > TIME_LIMIT;
				if (timeoutBlack){
					return;
				}
				if ((vm[policko]<0) && (vm[policko]>vmok.length-1)) {
					tt=-1;
				} else{
					tt= vm[policko];
					x=tt /10;
					y= tt -(10*x);
					net[x][y]= 2;
				}
			}
			this.net = evalmove(player,x,y, this.net);
		}

	}	

	public void gameend() {	
		int check = 0;
		validmoves(1);
		if(possiblemoves.size()==0)  check++;
		validmoves(2);
		if(possiblemoves.size()==0)  check++;
		if(check==2 || timeoutBlack || timeoutWhite){
			rungame=false;
			if (timeoutBlack || count(WHITE_PLAYER) > count(BLACK_PLAYER)){
				System.out.println("player White wins");
			}else if(timeoutWhite || count(WHITE_PLAYER) < count(BLACK_PLAYER)){
				
			}else if(count(WHITE_PLAYER) == count(BLACK_PLAYER)){
				System.out.println("game is tied");
			}
		}
	}	

	private int count(int player) {
		int result = 0;
		for (int i = 0; i < net.length; i++) {
			for (int j = 0; j < net.length; j++) {
				result += net[i][j] == player? 1:0;
			}
		}
		return result;
	}

	// sparvi tah: polozi kamen hraca player na poziciu x, y a prefarbi kamene supera
	public int[][] evalmove(int player, int x, int y, int[][] net) {
		int op =oponent(player);  // oponent
		
		// smery na preskumanie 
		int dirx[] = { 0,  1,  1,  1,  0, -1, -1, -1};
		int diry[] = {-1, -1,  0,  1,  1,  1,  0, -1};
		
		// polozime kamen
		net[x][y] = player;
		
		// skontrolujeme kazdy smer, mozno treba prefarbovat
		for (int d=0; d<dirx.length; d++) {
			int tx = x;		// pomocne premenne na pracu so suradnicami
			int ty = y;		// pomocne premenne na pracu so suradnicami

			// musime mat zopar kamenov oponenta
			do {
				tx += dirx[d];
				ty += diry[d];
			} while (isOkCoord(tx, ty) && net[tx][ty]==op);
			
			// ak potom nasleduje nas kamen, tak prefarbujeme
			if (isOkCoord(tx, ty) && net[tx][ty]==player) {
				tx = x + dirx[d];
				ty = y + diry[d];
				
				while (isOkCoord(tx, ty) && net[tx][ty]==op) {
					net[tx][ty]=player;   // prefarbime
					tx += dirx[d];
					ty += diry[d];
				}
			}
		}
	    
	    return net;
	}
	
	public void runng(){
		int player = 1;
		while(rungame) {	    	
	    	validmoves(player);	
	    	clean();
	    	move(player); 
	    	gameend();
	    	player++;
	    	if (player==3) player=1;
	    }
	}
	
	public void run(){
		Thread t = new Thread(){
			public void run(){			
				int player = WHITE_PLAYER;
				
				try{										
				    while(rungame) {
				    	
				    	validmoves(player);
				    	presenter.redraw();
				    				    	
				    	sleep(WAIT);
				    	clean();
				    	move(player);
				    
				    	presenter.redraw();		    	
				    	
				    	sleep(WAIT);
				    	gameend();
				    	player++;
				    	if (player==3) player=1;
				    }
				} catch(Exception e){
					e.printStackTrace();					
				}
			    clean();
			    presenter.redraw();
			    
			    int wh=0;
			    int bl=0;
			    for(int i=0; i<8; i++)
			    	for(int j=0; j<8;j++) {
			    		if(net[i][j]==1) wh++;
			    		if(net[i][j]==2) bl++;
			    	}
			    System.out.println("white = "+wh);
			    System.out.println("black = "+bl);

			}
		};
		t.start();
	}
	
    public void clean(){
    	for(int i=0; i<8; i++)
			for(int j=0; j<8; j++) {
				if((net[i][j]==3)||(net[i][j]==4)) net[i][j]=0;
			}
    }

	public int[] getPossibleMoves(int[][] net, int player) { 
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		int policko = -1;

		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++) {
				policko = (i*10)+j;
				if((net[i][j]==3)||(net[i][j]==4)) net[i][j]=0;
				if(validmove(policko, player, net)) { 
					tmp.add(policko);
					net[i][j]=player+2;
				}
			}
		
		int[] result = new int[tmp.size()];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = tmp.get(i);
		}
			
		return result;		
	}

	public int[][] getResultingState(int[][] plocha, int tah, int player) {
		int x = tah / 10;
		int y = tah % 10;
		
		int[][] newPlocha = new int[plocha.length][plocha.length];
		
		for (int i = 0; i < newPlocha.length; i++) {
			System.arraycopy(plocha[i], 0, newPlocha[i], 0, newPlocha[i].length);
		}
		
		newPlocha[x][y] = player;
		
		return evalmove(player, x, y, newPlocha);
	}
}