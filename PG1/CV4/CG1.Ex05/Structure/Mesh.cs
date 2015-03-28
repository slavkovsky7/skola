using System;
using System.Collections.Generic;
using System.Collections;
using System.Windows.Forms;
using System.Linq;
using System.Text;
using System.Drawing;
using System.IO;
using CG1.Ex05.Mathematics;

namespace CG1.Ex05.Structure
{
    public class MeshStructure
    {
        #region Properties

        private int edgeCount = 0, faceCount = 0, vertexCount = 0;
        private List<MeshVertex> vertices = new List<MeshVertex>();
        private List<HalfEdge> edges = new List<HalfEdge>();
        private List<MeshFace> faces = new List<MeshFace>();

        #endregion

        #region Get and Set Methods

        public List<MeshVertex> Vertices
        {
            get { return vertices; }
        }

        public List<MeshFace> Faces
        {
            get { return faces; }
        }

        public List<HalfEdge> Edges
        {
            get { return edges; }
        }

        public int VertexCount
        {
            get { return vertexCount; }
        }

        public int FaceCount
        {
            get { return faceCount; }
        }

        public int HalfEdgeCount
        {
            get { return edgeCount; }
        }

        #endregion

        #region Add Methods
        //Info: These methods are handy during loading / filling the structure.

        public bool AddVertex(MeshVertex vertex)
        {
            Vertices.Add(vertex);
            vertexCount++;

            return true;
        }

        public bool AddFace(MeshFace face)
        {
            Faces.Add(face);
            faceCount++;

            return true;
        }

        public bool AddHalfEdge(HalfEdge edge)
        {
            Edges.Add(edge);
            edgeCount++;

            return true;
        }

        #endregion

        #region Clear

        public bool Clear()
        {
            vertexCount = faceCount = edgeCount = 0;
            Vertices.Clear();
            Faces.Clear();
            Edges.Clear();

            return true;
        }

        #endregion

        #region Draw Methods

        /// <summary>
        /// Drawing of mesh. Use in checking our half edge structure.
        /// </summary>
        public void Draw(Graphics g)
        {
            //Info: If you will change template, you should implement very similar drawing. Or basically use this drawing please. I can easily chceck you assihnment then.
            Pen Blue = new Pen(Color.Blue, 2);
            Pen Green = new Pen(Color.Green, 2);
            Pen DefaultP = new Pen(Color.FromArgb(100, 100, 100));
            SolidBrush DefaultB = new SolidBrush(Color.FromArgb(50, 50, 50));

            //Info: At first, draw mesh itself. You can use iteration through edges / vertices / faces of mesh.
            MeshVertex v1 = new MeshVertex();
            foreach (HalfEdge e in edges)
            {
                g.DrawLine(DefaultP, (float)e.Direction.Position.X, (float)e.Direction.Position.Y, (float)e.Next.Direction.Position.X, (float)e.Next.Direction.Position.Y);
                g.FillEllipse(DefaultB, (float)e.Center.X - 3, (float)e.Center.Y - 3, 6, 6);
            }

            foreach (MeshVertex v in vertices)
                g.FillEllipse(DefaultB, (float)v.Position.X - 3, (float)v.Position.Y - 3, 6, 6);

            foreach (MeshFace f in faces)
            {
                g.FillEllipse(DefaultB, (float)f.Center.X - 3, (float)f.Center.Y - 3, 6, 6);
            }

            //Info: Then, you should draw selected elements. Each selected element has highlighted its property variables.
            //      Face: face polygon + edges of polygon + center of polygon
            //      Edge: Both half-edges of selected edge(if exist) + vertices + center
            //      Vertex: vertex position + leaving edge
            //Info: You can use iteration through connected lists.
            foreach (MeshFace f in faces)
            {
                if (f.IsSelected)
                {
                    g.FillPolygon(Brushes.LightBlue, f.Vertices().ToArray());
                    g.DrawEllipse(Pens.DarkGreen, (float)f.Center.X - 7, (float)f.Center.Y - 7, 14, 14);
                    g.FillEllipse(Brushes.Green, (float)f.Center.X - 5, (float)f.Center.Y - 5, 10, 10);

                    HalfEdge he = f.Edge;
                    do
                    {
                        g.DrawLine(Blue, (float)he.Direction.Position.X, (float)he.Direction.Position.Y, (float)he.Next.Direction.Position.X, (float)he.Next.Direction.Position.Y);
                        he = he.Next;
                    }
                    while (he != f.Edge);
                }
                else
                {
                    HalfEdge h = f.Edge;
                    do
                    {
                        if (h.IsSelected)
                        {
                            if (h.Opposite != null)
                            {
                                g.FillPolygon(Brushes.LightBlue, h.Opposite.Face.Vertices().ToArray());
                                g.FillEllipse(Brushes.Blue, (float)h.Opposite.Face.Center.X - 4, (float)h.Opposite.Face.Center.Y - 4, 8, 8);
                            }
                            g.FillPolygon(Brushes.LightBlue, h.Face.Vertices().ToArray());
                            g.FillEllipse(Brushes.Blue, (float)h.Face.Center.X - 4, (float)h.Face.Center.Y - 4, 8, 8);
                            g.DrawLine(Green, (float)h.Origin.Position.X, (float)h.Origin.Position.Y, (float)h.Direction.Position.X, (float)h.Direction.Position.Y);

                            g.FillEllipse(Brushes.Blue, (float)h.Direction.Position.X - 4, (float)h.Direction.Position.Y - 4, 8, 8);

                            g.DrawEllipse(Pens.DarkGreen, (float)h.Center.X - 7, (float)h.Center.Y - 7, 14, 14);
                            g.FillEllipse(Brushes.Green, (float)h.Center.X - 5, (float)h.Center.Y - 5, 10, 10);
                            g.DrawLine(Blue, (float)h.Next.Origin.Position.X, (float)h.Next.Origin.Position.Y, (float)h.Next.Direction.Position.X, (float)h.Next.Direction.Position.Y);
                        }

                        if (h.Direction.IsSelected)
                        {
                            g.DrawEllipse(Pens.DarkGreen, (float)h.Direction.Position.X - 6, (float)h.Direction.Position.Y - 6, 12, 12);
                            g.FillEllipse(Brushes.Green, (float)h.Direction.Position.X - 4, (float)h.Direction.Position.Y - 4, 8, 8);
                            g.DrawLine(Blue, (float)h.Direction.Leaving.Origin.Position.X, (float)h.Direction.Leaving.Origin.Position.Y, (float)h.Direction.Leaving.Direction.Position.X, (float)h.Direction.Leaving.Direction.Position.Y);
                        }
                        h = h.Next;
                    }
                    while (h != f.Edge);
                }
            }
        }

        /// <summary>
        /// Will de-select everything. Then it will try to select face / edge / vertex;
        /// </summary>
        public void TrySelect(Vector4 point)
        {
            //Info: de-select everything
            foreach (MeshFace f in faces)
            {
                f.IsSelected = false;
                HalfEdge he = f.Edge;
                do
                {
                    he.IsSelected = false;
                    he.Direction.IsSelected = false;
                    he = he.Next;
                }
                while (he != f.Edge);
            }

            //Info: Select something. If there are both edge and its opposite edge, both will be selected.
            foreach (MeshFace f in faces)
            {
                Vector4 d = point - f.Center;
                if (d.Length < 10)
                    f.IsSelected = !f.IsSelected;
            }

            foreach (HalfEdge e in edges)
            {
                Vector4 d = point - e.Center;
                if (d.Length < 10)
                    e.IsSelected = !e.IsSelected;
            }

            foreach (MeshVertex v in vertices)
            {
                Vector4 d = point - v.Position;
                if (d.Length < 10)
                    v.IsSelected = !v.IsSelected;
            }
        }

        #endregion

        #region Load And Fill Methods

        /// <summary>
        /// Load mesh from file.OFF - what is file.OFF? -> http://goo.gl/RqHoZ
        /// File: 1st line - vertices, faces, edges count
        ///       2nd+ line - vertices positions  
        ///       n-th+ line - first number is edge count per face and then face / edge indices
        /// </summary>
        public void Load(String filename)
        {
            //Info: You should use local mesh for loading. Then if everything is fine, copy it to this MeshStructure.
            //      As usual, you can change this template if you want.
            MeshStructure mesh = new MeshStructure();

            //Info: Variables for streaming from file.
            StreamReader reader;
            string line;
            char[] separators = new char[] { ' ', '\t' };
            int vertexCount = 0, faceCount = 0;

            try { reader = new StreamReader(filename); }
            catch (FileNotFoundException)
            {
                MessageBox.Show("File not found.");
                return;
            }

            //Info: Test if it is OFF file. - http://goo.gl/RqHoZ
            while ((line = reader.ReadLine()) != null)
                if (line == "OFF")
                    break;

            //Info: Start to read line after line.
            while ((line = reader.ReadLine()) != null)
            {
                //Info: This is example of reading values from file.
                string[] values = line.Split(separators, StringSplitOptions.RemoveEmptyEntries);

                //Info: This is example of using and converting values from file. We don't need to read number of edges - we will generate half-edges.
                vertexCount = Convert.ToInt32(values[0]);
                faceCount = Convert.ToInt32(values[1]);
               
                //ToDo: Finish an algorithm for filling the structure.
                //Hint: You can divide filling to a few parts.

                for (Int32 i = 0; i < vertexCount; i++)
                {
                    line = reader.ReadLine();
                    values = line.Split(separators, StringSplitOptions.RemoveEmptyEntries);
                    mesh.AddVertex(new MeshVertex(new Vector4(Int32.Parse(values[0]), Int32.Parse(values[1]), Int32.Parse(values[2])), null));
                    //ToDo: Load all vertices and their positions from file.
                }

                for (Int32 i = 0; i < faceCount; i++)
                {
                    //      You can use first number - edge count for current face. It is converted to int k. 
                    line = reader.ReadLine();
                    values = line.Split(separators, StringSplitOptions.RemoveEmptyEntries);
                    int k = Convert.ToInt32(values[0]);

                    MeshFace face = new MeshFace();
                    for (Int32 j = 1; j <= k; j++)
                    {
                        int vertexIndex = Convert.ToInt32(values[j]);

                        HalfEdge edge = new HalfEdge();
                        edge.Origin = mesh.Vertices[vertexIndex];
                        edge.Origin.Leaving = edge;

                        edge.Face = face;

                        if ( face.Edge == null ){
                            face.Edge = edge;
                        }else{
                            mesh.Edges[mesh.HalfEdgeCount - 1].Next = edge;
                            mesh.Edges[mesh.HalfEdgeCount - 1].Direction = edge.Origin;
                        }
                        mesh.AddHalfEdge(edge);
                    }
                    mesh.Edges[mesh.HalfEdgeCount - 1].Next = face.Edge;
                    mesh.Edges[mesh.HalfEdgeCount - 1].Direction = face.Edge.Origin;
                    mesh.AddFace(face);
                }

                foreach ( HalfEdge e1 in mesh.Edges){
                    foreach ( HalfEdge e2 in mesh.Edges){
                        if (e1.Equals(e2)){
                            e1.Opposite = e2;
                            e2.Opposite = e1;
                        }
                    }
                }
                

                for (int i = 0; i < mesh.edges.Count; i++ ){
                    mesh.edges[i].SetCenter();
                }
                for (int i = 0; i < mesh.faces.Count; i++) {
                    mesh.faces[i].SetCenter();
                }
                
                this.vertices = mesh.Vertices;
                this.vertexCount = mesh.VertexCount;
                this.edges = mesh.Edges;
                this.edgeCount = mesh.HalfEdgeCount;
                this.faces = mesh.Faces;
                this.faceCount = mesh.FaceCount;

                
            }
        }

        #endregion
    }
}
