using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Drawing;
using CG1.Ex05.Mathematics;

namespace CG1.Ex05.Structure
{
    public class MeshFace
    {
        /// <summary>
        /// One of the Bounding half edges.
        /// </summary>
        public HalfEdge Edge { get; set; }

        /// <summary>
        /// Center point of face. Maintained just for picking.
        /// </summary>
        private Vector4 center;

        /// <summary>
        /// Maintained just for picking.
        /// </summary>
        public Boolean IsSelected;

        public Vector4 Center
        {
            get { return center; }
        }

        #region Helper Methods

        /// <summary>
        /// Geometric center of standard convex polygon without holes.
        /// </summary>
        public void SetCenter()
        {
            IEnumerable<Point> points = Vertices();
            int sumX = 0;
            int sumY = 0;
            int k = 0;
            foreach ( Point p in points){
                sumX = sumX + p.X;
                sumY = sumY + p.Y;
                k++;
            }
            center = new Vector4(sumX/k, sumY/k, 0);
        }

        /// <summary>
        /// Return all vertices of polygon as Points. You can use it during drawing and during computation of polygon's center.
        /// </summary>
        public IEnumerable<Point> Vertices()
        {
            List<Point> result = new List<Point>();
            HalfEdge current = Edge;
            do {
                result.Add(new Point((int)current.Origin.Position.X, (int)current.Origin.Position.Y));
                current = current.Next;
            } while (current != Edge);
            return result;
        }

        #endregion
    }
}
