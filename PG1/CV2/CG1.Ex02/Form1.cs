using System;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using System.Drawing.Drawing2D;
using CG1.Ex02.Mathematics;
using CG1.Ex02.Geometry;
using CG1.Ex02;

namespace CG1.Ex02
{
    public partial class Form1 : Form
    {
        #region Properties
        
        BezierCurve BCurve = new BezierCurve();
        bool Down = false;
        Int32 Density = 25;

        #endregion

        #region Init

        public Form1()
        {
            InitializeComponent();
        }

        #endregion

        #region On Painting

        protected override void OnPaint(PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.InterpolationMode = InterpolationMode.NearestNeighbor;
            g.PixelOffsetMode = PixelOffsetMode.Half;
            BCurve.Draw(g);
        }

        #endregion

        #region GUI

        protected override void OnMouseDown(MouseEventArgs e)
        {
            Vector4 point = new Vector4(e.X, e.Y, 0, 1);

            //Info: If clicked point can be activated, then activate it and return true
            if (BCurve.SetActive(point))
            {
                Down = true;
                Invalidate();
            }

            //Info: Add new point to ControlPoints(if clicked point is already there select it). After each addition compute the curve again.
            if (!Down && e.Button == System.Windows.Forms.MouseButtons.Left)
            {
                BCurve.AddControlPoint(point);
                BCurve.Casteljau(Density);
                BCurve.SetActive(point);
            }

            //Info: Delete selected point and compute the curve again.
            else if (e.Button == System.Windows.Forms.MouseButtons.Right)
            {
                if (BCurve.SetActive(point))
                {
                    BCurve.DeletePoint();
                    if (BCurve.ControlPoints.Count() > 0)
                        BCurve.Casteljau(Density);
                }
            }
            Invalidate();
        }

        protected override void OnMouseUp(MouseEventArgs e)
        {
            Down = false;
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            Vector4 point = new Vector4(e.X, e.Y, 0, 1);
            if (Down)
            {
                //Info: If mouse is moving and button is down update selected point to new position.
                BCurve.UpdatePoint(point);
                BCurve.Casteljau(Density);
                Invalidate();
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            BCurve.AllClear();
            Invalidate();
        }

        private void trackBar1_ValueChanged(object sender, EventArgs e)
        {
            //Info: Change a quality / density of sampling - means how many points is in actual curve (CurvePoints).
            Density = trackBar1.Value;
            BCurve.Casteljau(Density);
            Invalidate();
        }

        #endregion

        private void Form1_Load(object sender, EventArgs e)
        {

        }
    }
}
