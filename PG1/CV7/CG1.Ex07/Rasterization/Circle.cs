using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace CG1.Ex07.Rasterization
{
    public class Circle
    {
        //Info: Internal representation for center of circle.
        public Int32 X0;
        public Int32 Y0;
        private int cellSize = 0;
        private void plot(Graphics g, int x, int y)
        {
            g.FillRectangle(new SolidBrush(Color.Black), new Rectangle( (x * cellSize), (y * cellSize), cellSize, cellSize));
        }
        //Info: As always - you can generate your own functions or change the template if necessary.
        public void MidpointCircle(Graphics g, Int32 CellSize, Int32 r, Int32 x0, Int32 y0)
        {
			if (r == 4){
				int b = 10;
			}
            //X0 =
            //ToDo: Implement Midpoint algorithm for rasterization of circle.
            //      You can change the implementation to Bresenham algorithm if you want.
            cellSize = CellSize;
            int x = r;
            int y = 0;
            int radiusError = 1 - x;

            while (x >= y)
            {
                plot(g, x + x0, y + y0);
				plot(g, x + x0,-y + y0);
				plot(g,-x + x0,-y + y0);
				plot(g,-x + x0, y + y0);

                plot(g, y + x0, x + y0);
				plot(g, y + x0,-x + y0);
                plot(g,-y + x0, x + y0);
                plot(g,-y + x0,-x + y0);
  

                y++;
                if (radiusError < 0)
                {
                    radiusError += 2 * y + 1;
                }
                else
                {
                    x--;
                    radiusError += 2 * (y - x + 1);
                }
            }
        }
    }
}
