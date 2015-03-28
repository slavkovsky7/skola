using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Drawing;
using CG1.Ex05.Mathematics;

namespace CG1.Ex05.Structure
{
    public class MeshVertex
    {
        #region Properties

        public Vector4 Position { get; set; }

        /// <summary>
        /// One of the leaving half edges. It does not matter which half edge.
        /// </summary>
        public HalfEdge Leaving { get; set; }

        /// <summary>
        /// Boolean value maintained for picking.
        /// </summary>
        public Boolean IsSelected;

        #endregion

        #region Constructors

        public MeshVertex() { }

        public MeshVertex(Vector4 coordinates, HalfEdge leaving)
        {
            Position = coordinates;
            Leaving = leaving;
        }

        #endregion
    }
}
