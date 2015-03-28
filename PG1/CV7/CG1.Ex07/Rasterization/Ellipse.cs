using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;

namespace CG1.Ex07.Rasterization
{
    public class Ellipse
    {
        //Info: Internal representation for center of ellipse.
        public Int32 X0;
        public Int32 Y0;

        private int cellSize = 0;
        private void plot(Graphics g, int x, int y)
        {
            g.FillRectangle(new SolidBrush(Color.Black), new Rectangle((x * cellSize), (y * cellSize), cellSize, cellSize));
        }

        private double F(double x, double y, int a, int b)
        {
            return Math.Pow(b, 2) * Math.Pow(x, 2) + Math.Pow(a, 2)
                    * Math.Pow(y, 2) - Math.Pow(a, 2) * Math.Pow(b, 2);
        }

        //Info: As always - you can generate your own functions or change the template if necessary.
        public void BresenEllipse(Graphics g, Int32 CellSize, Int32 x0, Int32 y0, Int32 a, Int32 b )
        {
            cellSize = CellSize;
            //ToDo: Implement Bresenham algorithm for rasterization of ellipse.
            //Ellipse is not symmetric in all 4 axises!
            int x = 0;
            int y = b;

            plot(g, x0+x,y0 + y);
            plot(g, x0 + x, y0 + -y);

            while (2 * b * b * x < 2 * a * a * y)
            {
                if (F(x + 1, y - 0.5, a, b) > 0)
                    y--;
                x++;
                plot(g, x0 +  x, y0 +  y);
                plot(g, x0 + -x, y0 +  y);
                plot(g, x0 +  x, y0 + -y);
                plot(g, x0 + -x, y0 + -y);
            }
            while (y >= 0)
            {
                if (F(x + 0.5, y - 1, a, b) <= 0)
                    x++;
                y--;
                plot(g, x0 +  x, y0 + y);
                plot(g, x0 + -x, y0 + y);
                plot(g, x0 +  x, y0 + -y);
                plot(g, x0 + -x, y0 + -y);
            }

        }
    }
}
