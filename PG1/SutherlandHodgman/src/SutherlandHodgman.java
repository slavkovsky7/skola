import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;
import javax.swing.*;
 
public class SutherlandHodgman extends JFrame {
 
    SutherlandHodgmanPanel panel;
 
    public static void main(String[] args) {
        JFrame f = new SutherlandHodgman();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
 
    public SutherlandHodgman() {
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        panel = new SutherlandHodgmanPanel();
        content.add(panel, BorderLayout.CENTER);
        setTitle("SutherlandHodgman");
        pack();
        setLocationRelativeTo(null);
    }
}
 
class SutherlandHodgmanPanel extends JPanel {
    List<double[]> subject, clipper, result;
 
    public SutherlandHodgmanPanel() {
        setPreferredSize(new Dimension(600, 500));
 
        // these subject and clip points are assumed to be valid
        double[][] subjPoints = {{50,50}, {150,50}, {150,150}, {250,150},{250,250} };
 
        double[][] clipPoints = {{100,100}, {200,100}, {200,200}, {100,200} };
 
        subject = new ArrayList<>(Arrays.asList(subjPoints));
        result  = new ArrayList<>(subject);
        clipper = new ArrayList<>(Arrays.asList(clipPoints));
 
        clipPolygon();
    }
 
    
    double o(double d){    	
    	return ( d - 100) / 10;
    }
    
    private void clipPolygon() {
        int len = clipper.size();
        for (int i = 0; i < len; i++) {
 
            int len2 = result.size();
            List<double[]> input = result;
            result = new ArrayList<>(len2);
 
            double[] P= clipper.get(i);
            double[] Q = clipper.get((i + 1) % len );
 
            System.out.format("P=[%.2f,%.2f], Q=[%.2f,%.2f]\n", o(P[0]),o(P[1]),o(Q[0]),o(Q[1]));
            
            for (int j = 0; j < len2; j++) {
 
                double[] Ai = input.get(j);
                double[] Ai_1 = input.get((j + 1) % len2);
 
                System.out.format("   A[%d] = [%.2f,%.2f] \t", j+1, o(Ai[0]), o(Ai[1]));
                System.out.format("A[%d] = [%.2f,%.2f]\n", (j + 1) % len2 + 1, o(Ai_1[0]), o(Ai_1[1]));
                
                System.out.format("	or2(A[%d]) = %f\n", j +1, isInsideResult( Ai, P, Q) );
                System.out.format("	or2(A[%d] = %f\n", (j + 1) % len2 +1 , isInsideResult( Ai_1, P, Q) );
                
                if (isInside(Ai_1, P, Q)) {
                    if (!isInside( Ai, P, Q)){
                    	double[] inter  = intersection(P, Q, Ai, Ai_1);
                        result.add(intersection(P, Q, Ai, Ai_1));
                        System.out.format("    added C=[%.2f,%.2f]\n", o(inter[0]), o(inter[1]));
                    }
                    result.add(Ai_1);
                    System.out.format("    added1 A[%d]\n", (j + 1) % len2 + 1);
                } else if (isInside(Ai, P, Q)){
                    result.add(intersection(P, Q, Ai, Ai_1));
                    double[] inter  = intersection(P, Q, Ai, Ai_1);
                    System.out.format("    added2 C=[%.2f,%.2f]\n", o(inter[0]), o(inter[1]));
                }
            }
        }
        System.out.println("----final result---");
        int c = 1;
        for (double [] vec : result){
        	System.out.format("A[%d] = [%.2f,%.2f] \n", c, o(vec[0]), o(vec[1]));
        	c++;
        }
    }
 
    private boolean isInside(double[] p, double[] q, double[] r) {
    	return (p[0]*(q[1] - r[1]) + p[1]*(r[0] - q[0]) + q[0]*r[1] - q[1]*r[0] ) > 0; 
    }
    
    private double isInsideResult(double[] pp, double[] qq, double[] rr) {
    	double p[] = new double[2];
    	double q[] = new double[2];
    	double r[] = new double[2];
    	
    	p[0] = o(pp[0]);
    	p[1] = o(pp[1]);
    	q[0] = o(qq[0]);
    	q[1] = o(qq[1]);
    	r[0] = o(rr[0]);
    	r[1] = o(rr[1]);
    	
    	return (p[0]*(q[1] - r[1]) + p[1]*(r[0] - q[0]) + q[0]*r[1] - q[1]*r[0] ) ; 
    }
 
    private double[] intersection2(double[] A, double[] B, double[] P, double[] Q) {
        double A1 = B[1] - A[1];
        double B1 = A[0] - B[0];

        double A2 = Q[1] - P[1];
        double B2 = P[0] - Q[0];
        
        double C1 = A1 * A[0] + B1 * A[1];
        double C2 = A2 * P[0] + B2 * P[1];
 
        double det = A1 * B2 - A2 * B1;
        double x = (B2 * C1 - B1 * C2) / det;
        double y = (A1 * C2 - A2 * C1) / det;
 
        return new double[]{x, y};
    }
 
    private double[] intersection(double[] P, double[] Q, double[] A, double[] B) {
        double D11 = B[0] - A[0];
        double D21 = B[1] - A[1];
        double D12 = P[0] - Q[0];
        double D22 = P[1] - Q[1];

        double det = D11*D22 - D12*D21;
        
        double Dt11 = P[0] - A[0];
        double Dt21 = P[1] - A[1];
        double Dt12 = D12;
        double Dt22 = D22;

        double dett = Dt11*Dt22 - Dt12*Dt21;

        double t = dett / det;
        
        double x = A[0] + t*(D11);
        double y = A[1] + t*(D21);
 
        return new double[]{x, y};
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(80, 60);
        g2.setStroke(new BasicStroke(3));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawPolygon(g2, subject, Color.blue);
        drawPolygon(g2, clipper, Color.red);
        drawPolygon(g2, result, Color.green);
    }
 
    private void drawPolygon(Graphics2D g2, List<double[]> points, Color color) {
        g2.setColor(color);
        int len = points.size();
        Line2D line = new Line2D.Double();
        for (int i = 0; i < len; i++) {
            double[] p1 = points.get(i);
            double[] p2 = points.get((i + 1) % len);
            line.setLine(p1[0], p1[1], p2[0], p2[1]);
            g2.draw(line);
        }
    }
}