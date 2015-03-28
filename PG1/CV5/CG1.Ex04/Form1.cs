using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using CG1.Ex04.Mathematics;
using CG1.Ex04.Geometry;
using CG1.Ex04.Clipping;
using System.Drawing.Drawing2D;

namespace CG1.Ex04
{
    public partial class Form1 : Form
    {
        #region Properties

        //Info: Clipping polygon points
        public Point StartPoint = new Point();
        public Point EndPoint = new Point();

        //Info: List of lines
        public List<Line> Lines = new List<Line>();
        bool NewPolygon = false;
        Polygon CyrusPoly = new Polygon();

        //Info: Positions for dragging the clipping polygon
        bool first = true;
        Vector4 FirstPoint = new Vector4();

        //Info: Clipping algorithms
        CohenSutherland Cohen = new CohenSutherland();
        CyrusBeck Cyrus = new CyrusBeck();

        #endregion

        #region Init

        public Form1()
        {
            InitializeComponent();
        }

        #endregion

        #region GUI

        protected override void OnPaint(PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.InterpolationMode = InterpolationMode.HighQualityBicubic;
            g.PixelOffsetMode = PixelOffsetMode.Half;

            //Info: Draw lines
            foreach (Line line in Lines)
            {
                line.Draw(g);
            }

            //Info: Draw clipping polygon for Cohen-Sutherland
            if (rbCohen.Checked)
            {
                Point[] points = new Point[5];
                points[0] = new Point(StartPoint.X, StartPoint.Y);
                points[1] = new Point(EndPoint.X, StartPoint.Y);
                points[2] = new Point(EndPoint.X, EndPoint.Y);
                points[3] = new Point(StartPoint.X, EndPoint.Y);
                points[4] = new Point(StartPoint.X, StartPoint.Y);
                g.DrawLines(Pens.Green, points);
            }

            //Info: Draw Cyrus polygon
            if (rbCyrus.Checked)
                CyrusPoly.Draw(g);
        }

        protected override void OnMouseClick(MouseEventArgs e)
        {
            Vector4 position = new Vector4(e.X, e.Y, 0, 1);
            if (e.Button == System.Windows.Forms.MouseButtons.Left)
            {
                if (first)
                {
                    //Info: Click first point of line
                    FirstPoint = position;
                    first = false;
                }
                else
                {
                    //Info: Click second point of line and add it to list of Lines
                    Lines.Add(new Line(FirstPoint, position));
                    first = true;
                }
            }

            if ((e.Button == System.Windows.Forms.MouseButtons.Right) && (rbCyrus.Checked))
            {
                if (!CyrusPoly.Closed)
                {
                    CyrusPoly.TryAddVertex(position);
                }
                else if (CyrusPoly.Closed)
                {
                    //Info: If polygon is closed start clipping
                    //      If you made another click - create new polygon
                    if (NewPolygon)
                    {
                        CyrusPoly = new Polygon();
                        NewPolygon = false;
                    }
                    CyrusPoly.TryAddVertex(position);
                }
                if (CyrusPoly.Closed)
                {
                    //Info: Start clipping and set min/max box and Lines to CS algorithm
                    Lines = Cyrus.StartClipping(CyrusPoly, Lines);
                    NewPolygon = true;
                }
            }

            if ((e.Button == System.Windows.Forms.MouseButtons.Right) && (rbCohen.Checked))
            {
                //Info: Stop dragging the CS polygon
                first = true;

                //Info: Start clipping and set min/max box and Lines to CS algorithm
                Lines = Cohen.StartClipping(StartPoint, EndPoint, Lines);
            }
            Invalidate();
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            Point position = new Point(e.X, e.Y);
            if ((e.Button == System.Windows.Forms.MouseButtons.Right) && (rbCohen.Checked))
            {
                if (first)
                {
                    //Info: Set StartPoint of clipping polygon
                    StartPoint = position;
                    first = false;
                }
                else
                {
                    //Info: Update EndPoint of clipping polygon
                    EndPoint = position;
                }
            }
            Invalidate();
        }

        private void rbCohen_CheckedChanged(object sender, EventArgs e)
        {
            Invalidate();
        }

        private void rbCyrus_CheckedChanged(object sender, EventArgs e)
        {
            Invalidate();
        }

        #endregion
    }
}
