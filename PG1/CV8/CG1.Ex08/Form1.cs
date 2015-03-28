using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using CG1.Ex08.Mathematics;
using CG1.Ex08.Geometry;
using CG1.Ex08.Rasterization;
using System.Drawing.Drawing2D;

namespace CG1.Ex08
{
    public partial class Form1 : Form
    {
        private Bitmap Image = new Bitmap(50, 50);
        private Int32 PixelSize = 10;
        private Polygon Poly = new Polygon();

        public Form1()
        {
            InitializeComponent();
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            g.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.NearestNeighbor;
            g.PixelOffsetMode = System.Drawing.Drawing2D.PixelOffsetMode.Half;
            g.DrawImage(Image, 0, 0, PixelSize * Image.Width, PixelSize * Image.Height);
        }

        protected override void OnMouseClick(MouseEventArgs e)
        {
            Vector4 position = new Vector4(e.X / PixelSize, e.Y / PixelSize, 0, 1);
            if (e.Button == System.Windows.Forms.MouseButtons.Left)
            {
                if (!Poly.Closed)
                {
                    //Poly.TryAddVertex(position);
                    Poly.Draw(Image);
                }
                else if (Poly.Closed)
                {
                    Poly = new Polygon();
                    //Poly.TryAddVertex(position);
                    Poly.Draw(Image);
                    Clear();
                }
          
                /*Clear();
                Poly = new Polygon();

                Poly.TryAddVertex(new Vector4(9, 12,0));
                Poly.TryAddVertex(new Vector4(9, 28, 0));
                Poly.TryAddVertex(new Vector4(26, 34, 0));
                Poly.TryAddVertex(new Vector4(38, 15, 0));
                Poly.TryAddVertex(new Vector4(34, 8, 0));
                Poly.TryAddVertex(new Vector4(23, 19, 0));

                Poly.TryAddVertex(new Vector4(9, 12, 0));*/

                /*Poly.TryAddVertex(new Vector4(10, 10, 0));
                Poly.TryAddVertex(new Vector4(10, 30, 0));
                Poly.TryAddVertex(new Vector4(30, 30, 0));
                Poly.TryAddVertex(new Vector4(30, 10, 0));
                Poly.TryAddVertex(new Vector4(25, 5, 0));
                Poly.TryAddVertex(new Vector4(20, 15, 0));
                Poly.TryAddVertex(new Vector4(15 , 5, 0));
                Poly.TryAddVertex(new Vector4(10, 10, 0));*/

                /*Poly.TryAddVertex(new Vector4(10, 10, 0));
                Poly.TryAddVertex(new Vector4(15, 5, 0));
                Poly.TryAddVertex(new Vector4(20, 20, 0));
                Poly.TryAddVertex(new Vector4(25, 15, 0));
                Poly.TryAddVertex(new Vector4(25, 30, 0));
                Poly.TryAddVertex(new Vector4(10, 30, 0));
                Poly.TryAddVertex(new Vector4(10, 10, 0));*/
                Poly.Draw(Image);
            }
            if (e.Button == System.Windows.Forms.MouseButtons.Right)
            {
                if ((rbScan.Checked) && Poly.Closed)
                {
                    Poly.Scan(Image);
                    Poly.Draw(Image);
                }
            }
            Invalidate();
        }

        private void rbScan_CheckedChanged(object sender, EventArgs e)
        {
            Poly = new Polygon();
            Clear();
            Invalidate();
        }

        private void rbFlood_CheckedChanged(object sender, EventArgs e)
        {
            Poly = new Polygon();
            Clear();
            Invalidate();
        }

        protected void Clear()
        {
            Graphics g = Graphics.FromImage(Image);
            g.FillRectangle(Brushes.White, 0, 0, Image.Width, Image.Height);
            Invalidate();
        }
    }
}
