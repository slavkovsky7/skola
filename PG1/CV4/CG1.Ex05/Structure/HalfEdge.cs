using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CG1.Ex05.Mathematics;

namespace CG1.Ex05.Structure
{
    //Info: For more information about half-edge you can use f.e. http://goo.gl/yz2vp
    //      How to navigate through the mesh? - http://goo.gl/zh91H
    public class HalfEdge
    {
        
        /// <summary>
        /// Vertex this edge points to
        /// </summary>
        public MeshVertex Direction { get; set; }

        /// <summary>
        /// Vertex this edge will leave. This is not in default structure and theory. It is used for easier acces to mesh.
        /// </summary>
        public MeshVertex Origin { get; set; }

        /// <summary>
        /// The face it belongs to
        /// </summary>
        public MeshFace Face { get; set; }

        /// <summary>
        /// The opposite half edge
        /// </summary>
        public HalfEdge Opposite { get; set; }

        /// <summary>
        /// The next half edge inside the face (ordered counter-clockwise)
        /// </summary>
        public HalfEdge Next { get; set; }

        /// <summary>
        /// Center of the half edge.
        /// </summary>
        private Vector4 center;

        /// <summary>
        /// Maintained just for picking.
        /// </summary>
        public Boolean IsSelected;

        public HalfEdge() { }

        public HalfEdge(MeshVertex origin, MeshVertex direction, MeshFace face, HalfEdge twin, HalfEdge next)
        {
            Origin = origin;
            Direction = direction;
            Face = face;
            Opposite = twin;
            Next = next;
        }

        public Vector4 Center
        {
            get { return center; }
        }

        public void SetCenter()
        {
            Vector4 v1 = Origin.Position;
            Vector4 v2 = Direction.Position;
            Vector4 unit = (v1 - v2) * (1 / (v1 - v2).Length);
            center = v2 + unit * (((v1 - v2).Length / 2));
        }

        public override bool Equals(object obj) {
            HalfEdge other = (HalfEdge)obj;
            Vector4 od = other.Direction.Position;
            Vector4 oo = other.Origin.Position;

            Vector4 td = this.Direction.Position;
            Vector4 to = this.Origin.Position;

            return 
                (od.Equals(td) && oo.Equals(to) ) ||
                (od.Equals(to) && oo.Equals(td));
        }
    }
}
