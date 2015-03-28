using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using CG1.Ex03.Geometry;
using CG1.Ex03.Mathematics;
using System.Drawing.Drawing2D;

namespace CG1.Ex03
{
    public partial class Form1 : Form
    {
        //Maybe we want to create more than just one polyline.
        public List<PolyLine> PolyLines = new List<PolyLine>();

        public Vector4 lastMousePos = new Vector4(0, 0, 0, 1);

        public Form1()
        {
            InitializeComponent();
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.InterpolationMode = InterpolationMode.HighQualityBicubic;
            g.PixelOffsetMode = PixelOffsetMode.Half;

            foreach (var polyLine in PolyLines)
            {
                polyLine.Draw(g);
            }
        }

        protected override void OnMouseClick(MouseEventArgs e)
        {
            Vector4 point = new Vector4(e.X, e.Y, 0, 1);

            if (e.Button == System.Windows.Forms.MouseButtons.Left)
            {
                //Add new polyline and a new pivot
                if ((Control.ModifierKeys & Keys.Control) == Keys.Control)
                {
                    UnselectAll();
                    PolyLine newPolyLine = new PolyLine(point,  true);
                    PolyLines.Add(newPolyLine);
                }
                //Add new vertex to selected polyline
                else if ((Control.ModifierKeys & Keys.Alt) == Keys.Alt)
                {
                    PolyLine selectedPolyLine = null;
                    foreach (var polyLine in PolyLines)
                    {
                        if (polyLine.IsSelected) selectedPolyLine = polyLine;
                        polyLine.IsSelected = false;
                    }

                    if (selectedPolyLine != null)
                    {
                        selectedPolyLine.IsSelected = true;
                        selectedPolyLine.AddVertex(point);
                    }
                }
                else
                {
                    //Select more than one polyline
                    if ((Control.ModifierKeys & Keys.Shift) != Keys.Shift) 
                        UnselectAll();

                    foreach (var polyLine in PolyLines)
                    {
                        polyLine.TrySelect(point);
                    }
                }
            }

            Invalidate();
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            Vector4 mousePos = new Vector4(e.X, e.Y, 0, 1);
            Vector4 mousePosDelta = mousePos - lastMousePos;

            //I Transform, You Transform...
            if (e.Button == System.Windows.Forms.MouseButtons.Right)
            {
                foreach (var polyLine in PolyLines)
                {
                    if (!polyLine.IsSelected) continue;

                    if ((Control.ModifierKeys & Keys.Control) == Keys.Control)
                    {
                        //Rotation is always around Z-axis. We are in 2D after all.
                        polyLine.OrientationZDeg += mousePosDelta.X;
                    }
                    else if ((Control.ModifierKeys & Keys.Shift) == Keys.Shift)
                    {
                        //Value factor is often used as scale factor.
                        double factor = (mousePos - polyLine.Pivot).Length / (lastMousePos - polyLine.Pivot).Length;
                        polyLine.Scale *= factor;
                    }
                    else
                    {
                        //Translate means to move polyline's pivot. We can use vectors here. Our mathematical library is usefull for something - yoohoo :)
                        polyLine.Pivot += mousePosDelta;
                    }
                }

                Invalidate();
            }

            lastMousePos = mousePos;
        }

        #region Helper Methods

        protected void UnselectAll()
        {
            foreach (var polyLine in PolyLines)
            {
                polyLine.IsSelected = false;
            }
        }

        #endregion

    }
}
