using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace CG1.Ex07.Rasterization
{
    class Line
    {
        //Info: Internal representation for cell size.
        public Int32 CS;

        private void plot(Graphics g, int x, int y)
        {
            g.FillRectangle(new SolidBrush(Color.Black), new Rectangle(  x * CS,  y * CS, CS, CS));
        }

        //Info: As always - you can generate your own functions or change the template if necessary.
        public void RasterizeLineBresenham(Graphics g, int x1, int y1, int x2, int y2, Int32 CellSize)
        {
            CS = CellSize;
            //ToDo: Implement Bresenham algorithm for rasterization of line.
            //      You can change the implementation to DDA algorithm if you want. They are not that different.
            double d = 0;


            int dy = Math.Abs(y2 - y1);
            int dx = Math.Abs(x2 - x1);

            double dy2 = (dy << 1); // slope scaling factors to avoid floating
            double dx2 = (dx << 1); // point

            int ix = x1 < x2 ? 1 : -1; // increment direction
            int iy = y1 < y2 ? 1 : -1;

            if (dy <= dx)
            {
                for (; ; )
                {
                    plot(g, x1, y1);
                    if (x1 == x2)
                        break;
                    x1 += ix;
                    d += dy2;
                    if (d > dx)
                    {
                        y1 += iy;
                        d -= dx2;
                    }
                }
            }
            else
            {
                for (; ; )
                {
                    plot(g, x1, y1);
                    if (y1 == y2)
                        break;
                    y1 += iy;
                    d += dx2;
                    if (d > dy)
                    {
                        x1 += ix;
                        d -= dy2;
                    }
                }
            }
        }
    }
}
