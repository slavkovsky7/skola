using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using CG1.Ex04.Mathematics;
using CG1.Ex04.Geometry;
using System.Windows.Forms;

namespace CG1.Ex04.Clipping
{
    public class CyrusBeck
    {
        
        //Info: Copy lines from GUI to clipping algorithm
        public List<Line> Lines { get; set; }

        //ToDo: Implement C-B clipping algorithm. Create you own methods etc. Use Line.cs, Polygon.cs and Vector4.cs. Comment you solution
        //      You should copy lines to local Lines - clip them and then return already clipped lines.
        //      As materials you can use your lectures > http://goo.gl/SuqxS
        //                               or additional materials > http://goo.gl/3kVUD

        double computeOrientation(Polygon poly ){
            Vector4 u = poly.Lines[0].Direction;
            Vector4 v = poly.Lines[1].Direction;
            double result = u.X * v.Y - u.Y * v.X;
            result = result / Math.Abs(result);
            return result;
        }

        public bool clip(ref Line line, Polygon poly ){
            double t0 = 0d;
            double t1 = 1d;
            Vector4 P = line.Direction;

            const double epsilon = 0.0001d;

            foreach( Line edge in poly.Lines){
                Vector4 enormalized  = edge.Direction * (1/edge.Direction.Length);
                double orientation = computeOrientation(poly);
                Vector4 n = Vector4.Zero;
                if ( orientation > 0){
                    n = new Vector4(-enormalized.Y, enormalized.X, 0); 
                }else{
                    n = new Vector4(enormalized.Y, -enormalized.X, 0); 
                }
                Vector4 F = edge.v0;
                Vector4 Q = line.v0 - F;
                
                double pn = P*n;
                double qn = Q*n;
                //ak je rovne nule
                if ( Math.Abs(pn) < epsilon ){
                    if (qn < 0) return false;
                }else{
                    double computedT = -qn / pn;
                    if ( pn < 0 ){
                        if (computedT < t0 ){
                            return false;
                        }
                        if ( computedT < t1){
                            t1 = computedT;
                        }
                    }else if ( pn > 0 ){
                        if ( computedT > t1 ){
                            return false;
                        }
                        if ( computedT > t0 ){
                            t0 = computedT;
                        }
                    }
                }
            }

            if (t0 < t1 ){
                if (t1 < 1){
                    line.v1 = line.v0 + t1*P;
                }
                if (t0 > 0){
                    line.v0 = line.v0 + t0 *P;
                }
            }else{
                return false;
            }
            return true;
        }

        public List<Line> StartClipping(Polygon Poly, List<Line> lines)
        {
            //Info: Copy lines from GUI to clipping algorithm
            Lines = new List<Line>();
            for (int i = 0 ; i < lines.Count; i ++){
                Line l = lines[i];
                bool r = clip(ref l, Poly);
                if (r){
                    Lines.Add(l);
                }
            }
            return Lines;
        }
    }
}

