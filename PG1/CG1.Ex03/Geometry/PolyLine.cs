using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CG1.Ex03.Mathematics;
using System.Drawing;

namespace CG1.Ex03.Geometry
{
	public class Vertex
	{
        //Position of vertex
        public Vector4 Position;
	}

	public class PolyLine
	{

        public PolyLine(Vector4 pivot , bool selected ){
            this.Pivot = pivot;
            this.IsSelected = selected;
        }

        #region Properties

        //Pivot vector of polyline
        public Vector4 Pivot = new Vector4(0, 0, 0, 0);
        
        //Default scale vector
        public Vector4 Scale = new Vector4(1, 1, 1, 1);
        
        //Polyline's vertices
        public List<Vertex> Vertices = new List<Vertex>();

        public double OrientationZDeg = 0;
        public Boolean IsSelected = false;
        
        #endregion

        #region Draw Polyline

        /// <summary>
        /// Draw polyline
        /// </summary>
        /// <param name="a"></param>
        public void Draw(Graphics g)
        {
            Vertex p0 = null;

            //ToDo: Create a transform matrix. This matrix should include all transformations you applied on this polyline. 
            //      Use Pivot, OsrientationZDeg and Scale

            Matrix44 transformationMatrix = Matrix44.Translate(Pivot) * Matrix44.Scale(Scale) * Matrix44.RotateZ(this.OrientationZDeg);


            //ToDo: Apply this new transform matrix to pivot axises
            Vector4 xAxis = transformationMatrix * new Vector4(20, 0, 0, 1) ;
            Vector4 yAxis = transformationMatrix * new Vector4(0, 20, 0, 1) ;

            //Draw a polyline pivot and two axises in X and Y direction.
            g.DrawLine(Pens.Red, (float)Pivot.X, (float)Pivot.Y, (float)xAxis.X, (float)xAxis.Y);
            g.DrawLine(Pens.Green, (float)Pivot.X, (float)Pivot.Y, (float)yAxis.X, (float)yAxis.Y);
            g.FillEllipse((IsSelected ? Brushes.Yellow : Brushes.Blue), (float)Pivot.X - 6, (float)Pivot.Y - 6, 12, 12);

            //Draw lines of polyline. Be aware because there is one ToDo inside!
            foreach (Vertex v in Vertices)
            {
                Vertex p1 = new Vertex();
                p1.Position = transformationMatrix * new Vector4(v.Position.X, v.Position.Y, v.Position.Z, 1);


                //ToDo: Use transform matrix to transform position of each polyline's vertex

                if (p0 != null)
                    g.DrawLine(Pens.Black, (float)p0.Position.X, (float)p0.Position.Y, (float)p1.Position.X, (float)p1.Position.Y);
                g.FillEllipse(Brushes.Black, (float)p1.Position.X - 4, (float)p1.Position.Y - 4, 8, 8);

                p0 = p1;
            }
        }

        #endregion

        #region Helper Methods

        /// <summary>
        /// Add new vertex.
        /// </summary>
        /// <param name="a"></param>
        public void AddVertex(Vector4 point)
        {
            Vertex v = new Vertex();
            v.Position = point - Pivot;
            //ToDo: Change a position of vertex v to a position of local polyline vertex.
            //      Vertex v is a local vertex of polyline. And polyline is global object of Form1.
            //      So what will be a position of such vertex?

            
            Vertices.Add(v);
        }

        public void TrySelect(Vector4 point)
        {
            if ( (Pivot - point).Length < 20.0){
                IsSelected = true;
            }
        }

        #endregion
	}
}
