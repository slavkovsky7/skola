using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CG1.Ex02.Mathematics;
using System.Drawing;

namespace CG1.Ex02.Geometry
{
    //ToDo: Implement all 'ToDo' statements. 
    //      Example resource for Bezier Curve is f.e. http://goo.gl/5HQFX
    //      Example resource for DeCasteljau algorithm is f.e. http://goo.gl/3oFla
    public class BezierCurve
    {
        public class Point
        {
            public Vector4 Position;
            public bool Selected;

            /// <summary>
            /// Creates a new Point with position. Point can be a ControlPoint or a CurvePoint. Also it can be selected / active.
            /// </summary>
            /// <param name="a"></param>
            /// <param name="b"></param>
            /// <param name="c"></param>
            /// <returns></returns>
            public Point(Vector4 pos, bool sel, bool con)
            {
                Position = pos;
                Selected = sel;
            }

            public PointF toPointf(int w ) {

                return new PointF((float)Position.X + w, (float)Position.Y + w);
            }
        }

        #region Properties
        
        //For simpler maintenance of points you can use these two Lists.
        public List<Point> ControlPoints = new List<Point>();
        public List<Point> CurvePoints = new List<Point>();

        #endregion

        #region Draw Methods

        /// <summary>
        /// Will draw all control points and all curve lines.
        /// </summary>
        /// <param name="a"></param>
        public void Draw(Graphics g)
        {
            for (int i = 0; i < CurvePoints.Count - 1; i++)
            {
                g.DrawLine(new Pen(Color.Red, 1), CurvePoints[i].toPointf(5), CurvePoints[i + 1].toPointf(5));
            }

            for (int i = 0 ; i < ControlPoints.Count - 1; i++){
                g.DrawLine(new Pen(Color.Black, 1), ControlPoints[i].toPointf(5), ControlPoints[i + 1].toPointf(5));
            }
            for (int i = 0 ; i < ControlPoints.Count; i++){
                Vector4 p = ControlPoints[i].Position;
                Color c = ControlPoints[i].Selected ? Color.Blue : Color.Black;
                g.DrawEllipse(new Pen(c, 2), new Rectangle((int)p.X, (int)p.Y, 10, 10));
            }
        }
        
        #endregion

        #region Computation

        /// <summary>
        /// Will compute curve's points. Number of points is due to quality of sampling
        /// </summary>
        /// <param name="a"></param>
        public void Casteljau(Double quality)
        {
            //bool firstVec = false;
            quality =  1d / quality;
            CurvePoints.Clear();
            double t = 0;
            while ( true ){
                bool doBreak = false;
                if (t > 1d){
                    doBreak = true;
                    t = 1.0d;
                }
                int n = ControlPoints.Count - 1;
                Vector4 vec = new Vector4(0,0,0);
                string str = "";
                for (int i = 0; i <= n; i++ ){
                    Vector4 pi = ControlPoints[i].Position;
                    vec = vec + ( ( ((double)MathEx.CombinationNumber(n ,i) ) * Math.Pow((1 - t), n - i) * Math.Pow(t, i) ) * pi );               
                   // str += MathEx.CombinationNumber(n,i)+"*(1-t)^"+(n-i)+"*t^("+i+")*P["+i+"] + ";
                }
                /*if (!firstVec){
                    Console.WriteLine(str);
                    Console.WriteLine(vec);
                    firstVec = true;
                }*/
                CurvePoints.Add(new Point(vec, false, false));
                t += quality;

                if (doBreak){
                    break;
                }
            }
        }

        #endregion

        #region Helper Methods

        /// <summary>
        /// Will add another control point.
        /// </summary>
        /// <param name="a"></param>
        public void AddControlPoint(Vector4 point)
        {
            if ( findPoint(point) == null ){
                ControlPoints.Add(new Point(point, true , false));
            }
        }

        /// <summary>
        /// Will deleted selected / active control point.
        /// </summary>
        /// <param name="a"></param>
        public void DeletePoint()
        {
            Point selected = getSelected();
            if ( selected != null ){
                ControlPoints.Remove(selected);
            }
        }

        /// <summary>
        /// Will update a position of selected / active control point to new 'point'.
        /// </summary>
        /// <param name="a"></param>
        public void UpdatePoint(Vector4 point)
        {
            Point selected = getSelected();
            if (selected != null ){
                selected.Position = point;
            }
        }

        

        private Point getSelected(){
           foreach (Point p in ControlPoints){
               if (p.Selected)
                   return p;
           }
           return null;
        }

        private Point findPoint(Vector4 point){
            foreach (Point p in ControlPoints){
                if ((p.Position - point).Length < 15 ){
                    return p;
                }
            }
            return null;
        }

        /// <summary>
        /// Will return true if some point can be selected. At a same time this point should become selected.
        /// </summary>
        /// <param name="a"></param>

        public bool SetActive(Vector4 point)
        {
            Point found = findPoint(point);
            if ( found != null){
                Point selected = getSelected();
                if ( selected != null ){
                    selected.Selected = false;
                }
                found.Selected = true;
                return true;
            }
            return false;
        }

        public void AllClear()
        {
            ControlPoints.Clear();
            CurvePoints.Clear();
        }

        #endregion
    }
}

