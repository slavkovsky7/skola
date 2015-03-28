using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using CG1.Ex07.Rasterization;
using System.Drawing.Drawing2D;

namespace CG1.Ex07
{
    public partial class Form1 : Form
    {
        #region Properties

        public OperationEnum Op = OperationEnum.SecondPoint;
        public enum OperationEnum
        {
            FirstPoint,
            SecondPoint,
        }

        Circle CircleRaster = new Circle();
        Ellipse EllipseRaster = new Ellipse();
        Line LineRaster = new Line();

        //Info: GridCellSize
        public Int32 GCS = 16;
        //Info: GridCellCount
        public Int32 GCC = 50;

        //Info: First Point
        public Int32 x0 = 20, y0 = 10;
        //Info: Second Point
        public Int32 x1 = 15, y1 = 5;
        //Info: Radius (in case of circle and ellipse)
        public Int32 r = 0;
        public Int32 dx = 0, dy = 0;
        public bool drawImplicit = true;

        public Form1()
        {
            InitializeComponent();
            rbLine.Checked = true;
            tbZoom.Value = 16;
        }

        #endregion

        #region GUI

        protected override void OnPaint(PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.InterpolationMode = InterpolationMode.HighQualityBicubic;
            g.PixelOffsetMode = PixelOffsetMode.Half;

            //Info: Draw default grid
            if (cbDrawGrid.Checked)
                for (Int32 i = 0; i <= GCC; i++)
                {
                    g.DrawLine(Pens.LightGray, 0, i * GCS, GCC * GCS, i * GCS);
                    g.DrawLine(Pens.LightGray, i * GCS, 0, i * GCS, GCC * GCS);
                }

            //Info: Default properties for algorithms
            dx = Math.Abs(x1 - x0);
            dy = Math.Abs(y1 - y0);
            r = (int)Math.Sqrt(dx * dx + dy * dy);

            //Info: Recompute current rasterization
            if (rbLine.Checked)
            {
                LineRaster.RasterizeLineBresenham(g, x0, y0, x1, y1, GCS);
                if (drawImplicit)
                    g.DrawLine(Pens.Red, GCS * (x0 + 0.5f), GCS * (y0 + 0.5f), GCS * (x1 + 0.5f), GCS * (y1 + 0.5f));
            }
            else if (rbCircle.Checked)
            {
                CircleRaster.MidpointCircle(g, GCS, r, x0, y0);
                if (drawImplicit)
                    g.DrawEllipse(Pens.Red, GCS * x0 - (int)r * GCS + GCS / 2, GCS * y0 - (int)r * GCS + GCS / 2, (int)r * 2 * GCS, (int)r * 2 * GCS);
            }
            else if (rbEllipse.Checked)
            {
                EllipseRaster.BresenEllipse(g, GCS, x0, y0, dx, dy);
                if (drawImplicit)
                    g.DrawEllipse(Pens.Red, GCS * x0 - (int)dx * GCS + GCS / 2, GCS * y0 - (int)dy * GCS + GCS / 2, (int)dx * 2 * GCS, (int)dy * 2 * GCS);
            }

            if (drawImplicit)
                g.DrawLine(Pens.Red, GCS * x0 + GCS / 2, GCS * y0 + GCS / 2, GCS * x1 + GCS / 2, GCS * y1 + GCS / 2);
            g.FillEllipse(Brushes.Green, GCS * x1 + 1, GCS * y1 + 1, GCS - 1, GCS - 1);
            g.FillEllipse(Brushes.Red, GCS * x0 + 1, GCS * y0 + 1, GCS - 1, GCS - 1);
        }

        protected override void OnMouseClick(MouseEventArgs e)
        {
            int x = e.X / GCS;
            int y = e.Y / GCS;

            if ((e.Button & System.Windows.Forms.MouseButtons.Left) == System.Windows.Forms.MouseButtons.Left)
            {
                switch (Op)
                {
                    case OperationEnum.FirstPoint:
                        x0 = x;
                        y0 = y;
                        Op = OperationEnum.SecondPoint;
                        break;

                    case OperationEnum.SecondPoint:
                        x1 = x;
                        y1 = y;
                        Op = OperationEnum.FirstPoint;
                        break;
                }
            }

            Invalidate();
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            int x = e.X / GCS;
            int y = e.Y / GCS;

            if (Op == OperationEnum.SecondPoint)
            {
                x1 = x;
                y1 = y;
            }

            Invalidate();
        }

        private void rbCircle_CheckedChanged(object sender, EventArgs e)
        {
            Invalidate();
        }

        private void rbEllipse_CheckedChanged(object sender, EventArgs e)
        {
            Invalidate();
        }

        private void tbZoom_ValueChanged(object sender, EventArgs e)
        {
            GCS = tbZoom.Value;
            GCC = Width / GCS;
            Invalidate();
        }

        private void cbImplicit_CheckedChanged(object sender, EventArgs e)
        {
            drawImplicit = (cbImplicit.Checked) ? true : false;
            Invalidate();
        }

        private void Form1_SizeChanged(object sender, EventArgs e)
        {
            GCC = Width / GCS;
            Invalidate();
        }

        private void cbDrawGrid_CheckedChanged(object sender, EventArgs e)
        {
            Invalidate();
        }

        #endregion

        private void Form1_Load(object sender, EventArgs e)
        {

        }
    }
}
