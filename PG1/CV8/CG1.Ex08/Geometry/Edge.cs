using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CG1.Ex08.Mathematics;
using CG1.Ex08.Rasterization;
using System.Drawing;

namespace CG1.Ex08.Geometry
{
    public class Edge : IComparable
    {
        public Vector4 v1;
        public Vector4 v2;
        //ToDo: Define also other edge properties - due to scanline algorithm
        public double valX;
        public int minY;
        public int maxY;
        public double oneOverM;

        Line line = new Line();
        public bool Active = false;

        public Edge(Vector4 p1, Vector4 p2) 
        {
            v1 = p1;
            v2 = p2;
            minY = (int)Math.Min(p1.Y, p2.Y);
            maxY = (int)Math.Max(p1.Y, p2.Y);
            valX = p1.Y < p2.Y ? p1.X : p2.X;
            oneOverM = (p1.X - p2.X) / (p1.Y - p2.Y); 
        }

        public void Draw(Bitmap Image)
        {
            //Info: You should rasterize each edge - due to flood algorithms
            line.RasterizeLineBresenham(Image, (int)v1.X, (int)v1.Y, (int)v2.X, (int)v2.Y,  Color.Black);
        }

        public override string ToString(){
            return String.Format("|{0,0:0.0} | {1,0:0.00} | {2,0:0.00} | {3,0:0.00} | ", minY, maxY, valX, oneOverM );
        }

        public int CompareTo(object obj)
        {
            if (obj is Edge)
            {
                Edge other = (Edge)obj;

                if (!Active){
                    int compMinY = this.minY.CompareTo(other.minY);
                    if (compMinY != 0) return compMinY;
                }
                /*int compMaxY = this.maxY.CompareTo(other.maxY);  
                if (compMaxY != 0) return compMaxY;*/

                int compValX = this.valX.CompareTo(other.valX);
                if (compValX != 0) return compValX;

                int compM = this.oneOverM.CompareTo(oneOverM);
                return compM;
            }
            throw new ArgumentException("Object is not a Edge");
        }
    }
}
