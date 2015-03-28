using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CG1.Ex04.Mathematics;
using System.Drawing;

namespace CG1.Ex04.Geometry
{
    public class Polygon
    {
        public List<Line> Lines = new List<Line>();
        public bool Closed = false;
        Vector4 Prev = new Vector4(-1, -1, -1);
        Vector4 First;

        
        public void TryAddVertex(Vector4 point)
        {
            if (Prev.X == -1)
            {
                First = point;
                Prev = point;
            }
            else
            {
                Vector4 d = point - First;
                if (d.Length < 20)
                {
                    Line line = new Line(Prev, First);
                    Lines.Add(line);
                    //Prev = point;
                    Closed = true;
                    Prev = new Vector4(-1, -1, -1);
                }
                else
                {
                    Line line = new Line(Prev, point);
                    Lines.Add(line);
                    Prev = point;
                }
                
            }
        }

        public void Draw(Graphics g)
        {
            foreach (Line line in Lines)
            {
                int x1 = (int)Math.Round(line.v0.X);
                int y1 = (int)Math.Round(line.v0.Y);
                int x2 = (int)Math.Round(line.v1.X);
                int y2 = (int)Math.Round(line.v1.Y);

                g.DrawLine(Pens.Green, new Point(x1, y1), new Point(x2, y2));
            }
        }
    }
}
