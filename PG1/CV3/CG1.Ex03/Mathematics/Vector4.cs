using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CG1.Ex03.Mathematics
{
	public struct Vector4
	{
		#region Properties

		public static readonly Vector4 Zero = new Vector4(0, 0, 0, 0);
		public static readonly Vector4 UnitX = new Vector4(1, 0, 0, 0);
		public static readonly Vector4 UnitY = new Vector4(0, 1, 0, 0);
		public static readonly Vector4 UnitZ = new Vector4(0, 0, 1, 0);
		public static readonly Vector4 UnitW = new Vector4(0, 0, 0, 1);

		public double X;
		public double Y;
		public double Z;
		public double W;

		public Double Length
		{
			get { return Math.Sqrt(X * X + Y * Y + Z * Z + W * W); }
		}

		#endregion

		#region Init

		public Vector4(Double x, Double y, Double z, Double w = 0)
		{
			X = x;
			Y = y;
			Z = z;
			W = w;
		}

		#endregion

        #region Arithmetic Operations

        public static Vector4 operator +(Vector4 a, Vector4 b)
        {
            return new Vector4(a.X + b.X, a.Y + b.Y, a.Z + b.Z, a.W + b.W);
        }

        public static Vector4 operator -(Vector4 a, Vector4 b)
        {
            return new Vector4(a.X - b.X, a.Y - b.Y, a.Z - b.Z, a.W - b.W);
        }

        public static Vector4 operator *(Vector4 a, Double b)
        {
            return new Vector4(a.X * b, a.Y * b, a.Z * b, a.W * b);
        }

        public static Vector4 operator *(Double a, Vector4 b)
        {
            return new Vector4(a * b.X, a * b.Y, a * b.Z, a * b.W);
        }

        /// <summary>
        /// Dot Product
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <returns></returns>
        public static Double operator *(Vector4 a, Vector4 b)
        {
            return a.X * b.X + a.Y * b.Y + a.Z * b.Z + a.W * b.W;
        }

        /// <summary>
        /// 3D Cross Product
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <returns></returns>
        public static Vector4 operator %(Vector4 a, Vector4 b)
        {
            return new Vector4(a.Y * b.Z - a.Z * b.Y, a.Z * b.X - a.X * b.Z, a.X * b.Y - a.Y * b.X, 0);
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


        public override bool Equals(object o)
        {
            Vector4 ov = (Vector4)o;
            return almostEqual(ov.X, this.X) && almostEqual(ov.Y, this.Y) && almostEqual(ov.Z, this.Z) && almostEqual(ov.W, this.W);
        }

        public static bool almostEqual(double a, double b)
        {
            return Math.Abs(a - b) < 0.01;
        }

        public override string ToString()
        {
            
            return "["+X+","+Y+","+Z+"]";
        }

        #endregion

	}
}
