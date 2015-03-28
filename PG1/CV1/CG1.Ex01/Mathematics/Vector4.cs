using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CG1.Ex01.Mathematics
{
    // Info: We will use 'structs' instead of 'classes' here. Maybe this will explain why: http://goo.gl/8w6bo
    // Vector is defined in homogeneous coordinates to resolve affine transformatin problem
	public struct Vector4
	{
		#region Properties

		public static readonly Vector4 Zero = new Vector4(0, 0, 0, 0);
        //ToDo: Declare also 4 unit vectors UnitX, UnitY... 

        public double X;
        public double Y;
        public double Z;
        public double W;

		public Double Length
		{
            get
            {
                return Math.Sqrt(X * X + Y * Y + Z * Z + W * W);
            }
		}

		#endregion

		#region Init

		public Vector4(Double x, Double y, Double z, Double w = 0)
		{
            this.X = x;
            this.Y = y;
            this.Z = z;
            this.W = w;
		}

		#endregion

		#region Arithmetic Operations

		public static Vector4 operator +(Vector4 a, Vector4 b)
		{
            return new Vector4(a.X + b.X, a.Y + b.Y, a.Z + b.Z, a.W + b.W);
		}

		public static Vector4 operator -(Vector4 a, Vector4 b) {
            return new Vector4(a.X - b.X, a.Y - b.Y, a.Z - b.Z, a.W - b.W);
		}

		public static Vector4 operator *(Vector4 a, Double b) {
            return new Vector4(a.X * b, a.Y * b, a.Z * b, a.W * b );
		}

		public static Vector4 operator *(Double a, Vector4 b){
            return b * a;
		}

		/// <summary>
		/// Dot Product
		/// </summary>
		/// <param name="a"></param>
		/// <param name="b"></param>
		/// <returns></returns>
		public static Double operator *(Vector4 a, Vector4 b)
		{
            return a.X*b.X + a.Y * b.Y + a.Z * b.Z + a.W * b.W;
		}

		/// <summary>
		/// 3D Cross Product
		/// </summary>
		/// <param name="a"></param>
		/// <param name="b"></param>
		/// <returns></returns>
		public static Vector4 operator %(Vector4 a, Vector4 b)
		{
            return new Vector4(
                a.Y*b.Z - a.Z*b.Y,
                a.Z*b.X - a.X*b.Z,
                a.X*b.Y - a.Y*b.X
            );
		}


        public static Vector4 operator /(Vector4 a, double d){
            return new Vector4(a.X / d, a.Y / d, a.Z / d, a.W / d);
        }

        public static Vector4 operator /(double d, Vector4 a) {
            return a/d;
        }

        /// <summary>
        /// Projection of vector along another vector
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <returns></returns>
        //uplne debilny nazov, operator /??? Vsak to nedava zmysel.
        public static Vector4 operator /(Vector4 a, Vector4 b)
        {
            double bl = (b.Length);
            double s = (a * b) / bl;
            return s * (b / bl );  
        }

        /// <summary>
        /// Modulation Product
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <returns></returns>
        public static Vector4 operator ^(Vector4 a, Vector4 b)
        {
            return new Vector4(a.X * b.X, a.Y * b.Y, a.Z * b.Z, a.W * b.W);
        }
		#endregion

        public override string ToString() {
            return "[" + X + "," + Y + "," + Z + "," + W + "]";
        }

        public override bool Equals( object o )
        {
            Vector4 ov = (Vector4)o;
            return Assert.almostEqual(ov.X, this.X) && Assert.almostEqual(ov.Y, this.Y) && Assert.almostEqual(ov.Z, this.Z) && Assert.almostEqual(ov.W, this.W);
        }



        public double this[int i]
        {
            get {
                switch (i) {
                    case 0: return X;
                    case 1: return Y;
                    case 2: return Z;
                    case 3: return W;
                }
                throw new IndexOutOfRangeException();
            }

            set {
                switch (i)
                {
                    case 0: X = value; break;
                    case 1: Y = value; break;
                    case 2: Z = value; break;
                    case 3: W = value; break;
                }
            }
        }
	}
}
