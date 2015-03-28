using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CG1.Ex04.Mathematics;
using System.Drawing;

namespace CG1.Ex04.Geometry
{
    public class Line
    {
        public Vector4 v0 = new Vector4();
        public Vector4 v1 = new Vector4();

        public bool allClipped = false;

        public Vector4 Direction
        {
            get { return v1 - v0; }
        }

        
        
        public Line(Vector4 p0, Vector4 p1)
        {
            v0 = p0;
            v1 = p1;
        }

        
        public void Draw(Graphics g)
        {
            int x1 = (int)Math.Round(v0.X);
            int y1 = (int)Math.Round(v0.Y);
            int x2 = (int)Math.Round(v1.X);
            int y2 = (int)Math.Round(v1.Y);


            g.DrawLine(Pens.Black, new Point(x1, y1), new Point(x2, y2));
            g.FillEllipse(Brushes.Blue, x1 - 2, y1 - 2, 4, 4);
            g.DrawEllipse(Pens.DarkBlue, x1 - 2, y1 - 2, 4, 4);
            g.FillEllipse(Brushes.Blue, x2 - 2, y2 - 2, 4, 4);
            g.DrawEllipse(Pens.DarkBlue, x2 - 2, y2 - 2, 4, 4);
        }
        
        
    }
}
