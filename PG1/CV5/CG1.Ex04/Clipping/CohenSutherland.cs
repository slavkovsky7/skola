using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using CG1.Ex04.Mathematics;
using CG1.Ex04.Geometry;

namespace CG1.Ex04.Clipping
{
    class CohenSutherland
    {
        //Info: Binary constants
        int INSIDE = 0;
        int LEFT = 1;
        int BOTTOM = 2;
        int RIGHT  = 4;
        int TOP = 8;
        
        //Info: Clipping polygon min and max
        double xmin = 0, ymin = 0, xmax = 0, ymax = 0;

        //Info: Copy lines from GUI to clipping algorithm
        public List<Line> Lines { get; set; }

        //ToDo: Implement C-S clipping algorithm. Create you own methods etc. Use Line.cs and Vector4.cs. Comment you solution
        //      You should copy lines to local Lines - clip them and then return already clipped lines.
        //      As materials you can use your lectures > http://goo.gl/SuqxS
        //                               or additional materials > http://goo.gl/3kVUD

        int ComputeOutCode(double x, double y) {
            int code;
            code = INSIDE;          
            if (x < xmin)           
                code |= LEFT;
            else if (x > xmax)      
                code |= RIGHT;
            if (y < ymin)           
                code |= BOTTOM;
            else if (y > ymax)      
                code |= TOP;

            return code;
        }

        private Line Clip(Line line) {
            int outCode1 = ComputeOutCode(line.v0.X, line.v0.Y);
            int outCode2 = ComputeOutCode(line.v1.X, line.v1.Y);
            bool accept = false;


            double x0 = line.v0.X;
            double x1 = line.v1.X;
            double y0 = line.v0.Y;
            double y1 = line.v1.Y;

            while(true){
                if ( (outCode1 | outCode2) == 0 ){
                    accept = true;
                    break;
                }else if ( (outCode1 & outCode2) != 0 ){
                    break;
                }else{
                    double x = 0, y = 0;
                    
                    int outcodeOut = outCode1 != 0 ? outCode1 : outCode2;

                    if ( (outcodeOut & TOP) != 0) {           
                        x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
                        y = ymax;
                    } else if ( (outcodeOut & BOTTOM) != 0 ) { 
                        x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
                        y = ymin;
                    } else if ((outcodeOut & RIGHT) != 0) {  
                        y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
                        x = xmax;
                    } else if ((outcodeOut & LEFT) != 0) {   
                        y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
                        x = xmin;
                    }

                    if (outcodeOut == outCode1) {
                        x0 = x;
                        y0 = y;
                        outCode1 = ComputeOutCode(x0, y0);
                    } else {
                        x1 = x;
                        y1 = y;
                        outCode2 = ComputeOutCode(x1, y1);
                    }
                }
            }

            if (accept) {
                return new Line( new Vector4(x0, y0, 0), new Vector4(x1, y1, 0 ) );
            }
            return null;
        }

        public List<Line> StartClipping(Point StartPoint, Point EndPoint, List<Line> lines)
        {
            //Info: Set min and max values
            xmax = (StartPoint.X > EndPoint.X) ? StartPoint.X : EndPoint.X;
            xmin = (StartPoint.X < EndPoint.X) ? StartPoint.X : EndPoint.X;
            ymax = (StartPoint.Y > EndPoint.Y) ? StartPoint.Y : EndPoint.Y;
            ymin = (StartPoint.Y < EndPoint.Y) ? StartPoint.Y : EndPoint.Y;

            Lines = new List<Line>();

            foreach ( Line l in lines ){
                Line clipped = Clip(l);
                if ( clipped != null){
                    Lines.Add(clipped);
                }
            }
            //ToDo: Here the algorithm could start

            return Lines;
        }
    }
}
