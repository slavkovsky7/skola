using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CG1.Ex08.Mathematics;
using CG1.Ex08.Rasterization;
using CG1.Ex08.Geometry;
using System.Drawing;

namespace CG1.Ex08.Geometry
{
	public class Polygon {
        //Info: Properties from last exercises - clipping
        public bool Closed = false;
        private List<Edge> Edges = new List<Edge>();
        private Vector4 Prev = new Vector4(-1, -1, -1);
        private Vector4 First;

		public void TryAddVertex(Vector4 point)
		{
            if(Prev.X == -1)
            {
                First = point;
                Prev = point;
            }
            else
            {
                Vector4 d = point - First;
                if (d.Length < 5)
                {
                    Edge e = new Edge(Prev, First);
                    Edges.Add(e);
                    Closed = true;
                    Prev = new Vector4(-1, -1, -1);
                }
                else
                {
                    Edge e = new Edge(Prev, point);
                    Edges.Add(e);
                    Prev = point;
                }
            }
		}


        private List<Edge> preProcess(List<Edge> input ){
            List<Edge> result = new List<Edge>();
            foreach (Edge e in input){
                if ( e.v1.Y != e.v2.Y ){
                    result.Add(new Edge(e.v1, e.v2));
                }
            }
            return result;
        }

        private void getActiveEdges(List<Edge> global, List<Edge> active , int scanLine){
            List<Edge> result = new List<Edge>();
            foreach (Edge e in global){
                if (e.minY <= scanLine ){
                    active.Add(e);
                }
            }
            foreach (Edge e in active){
                global.Remove(e);
            }
        }

        private void updateActiveEdges(List<Edge> active, int scanLine ){
            List<Edge> toRemove = new List<Edge>();
            foreach (Edge e in active){
                e.valX += e.oneOverM;
                e.Active = true;
                if (e.maxY <= scanLine){
                    toRemove.Add(e);
                }
            }
 
            foreach (Edge e in toRemove){
                active.Remove(e);
            }
            active.Sort();
        }

        public void Scan(Bitmap Image)
        {
            List<Edge> global = preProcess(Edges);
            global.Sort();    

            int scanLine = global[0].minY;
            List<Edge> activeEdges = new List<Edge>();
            getActiveEdges(global, activeEdges, scanLine);
            while (activeEdges.Count > 1 || global.Count > 0 ){
               
                if (scanLine == 10){
                    Console.WriteLine("asdasd");
                }
                drawScanLine(activeEdges, Image, scanLine);
                getActiveEdges(global, activeEdges, scanLine);
                
                updateActiveEdges(activeEdges, scanLine);
                scanLine++;
            }; 
    

            // "     123.5"
            //ToDo: Implement scanline algorithm
            //Info: Here you can start to scan the polygon
        }

        private void drawScanLine(List<Edge> active, Bitmap image, int scanLine){
            using (Graphics g = Graphics.FromImage(image))
            {
                for (int i = 0; i < active.Count - 1; i+=2)
                {
                    Point p1 = new Point((int) Math.Ceiling(active[i].valX), scanLine);
                    Point p2 = new Point((int)active[i + 1].valX, scanLine);
                    new Line().RasterizeLineBresenham(image, p1.X, p1.Y, p2.X, p2.Y, Color.Azure);            
                }
            }
   
        }

		public void Draw(Bitmap Image)
		{
            foreach (Edge e in Edges)
                e.Draw(Image);
		}

	}
}
