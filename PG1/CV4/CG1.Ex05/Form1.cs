using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing.Drawing2D;

using CG1.Ex05.Structure;
using CG1.Ex05.Mathematics;

namespace CG1.Ex05
{
    public partial class Form1 : Form
    {
        public MeshStructure Mesh = new MeshStructure();
        public Form1()
        {
            InitializeComponent();
            //ToDo: Uncomment following after you program is completed.
            //Mesh.Load(@"Meshes/example.off");
            //Invalidate();
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.InterpolationMode = InterpolationMode.HighQualityBicubic;
            g.PixelOffsetMode = PixelOffsetMode.Half;

            if (Mesh != null)
                Mesh.Draw(g);

        }

        protected override void OnMouseClick(MouseEventArgs e)
        {
            Vector4 point = new Vector4(e.X, e.Y, 0, 1);

            if (e.Button == System.Windows.Forms.MouseButtons.Left)
            {
                if (Mesh != null)
                    Mesh.TrySelect(point);
            }

            Invalidate();
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            switch (comboBox1.SelectedItem as String)
            {
                case "Free Example":
                    Mesh.Clear();
                    Mesh.Load(@"Meshes/example.off");
                    break;
                case "Triangulated Hexagon":
                    Mesh.Clear();
                    Mesh.Load(@"Meshes/hexagon.off");
                    break;
                case "Empty Hexagon":
                    Mesh.Clear();
                    Mesh.Load(@"Meshes/hexagon2.off");
                    break;
                case "Empty Square":
                    Mesh.Clear();
                    Mesh.Load(@"Meshes/square.off");
                    break;
            }
            Invalidate();
        }
    }
}
