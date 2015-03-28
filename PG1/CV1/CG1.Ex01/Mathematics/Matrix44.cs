using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CG1.Ex01.Mathematics
{
	public struct Matrix44
	{
		#region Properties

		public const Int32 Dim = 4;

        public static Matrix44 Zero() { 
            return new Matrix44(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }

		public static Matrix44 Identity
		{
            get
            {
                return new Matrix44(
                    1,0,0,0,
                    0,1,0,0,
                    0,0,1,0,
                    0,0,0,1);
            }
		}

        public Matrix44 Transposed
        {
            get
            {
                Matrix44 result = Matrix44.Zero();
                for (int y = 0; y < this.matValues.Length; y++)
                {
                    for (int x = 0; x < this.matValues.Length; x++)
                    {
                        result[x,y] = this[y,x]; 
                    }
                }
                return result;
            }
        }

        public static double SarrusRule(double[,] a){
            return 
                a[0, 0] * a[1, 1] * a[2, 2]+
                a[0, 1] * a[1, 2] * a[2, 0]+
                a[0, 2] * a[1, 0] * a[2, 1]-
                a[2, 0] * a[1, 1] * a[0, 2]-
                a[2, 1] * a[1, 2] * a[0, 0]-
                a[2, 2] * a[1, 0] * a[0, 1];
        }

        private double[,] getSubMatrix(int ix, int jy) {
            double[,] m3x3 = new double[3, 3];
            int yc = 0; 
            for (int y = 0; y < 4; y++){
                if (y != jy){
                    int xc = 0;
                    for (int x = 0; x < 4; x++){
                        if (x != ix){
                            m3x3[xc, yc] = this[x, y];
                            xc++;
                        }
                    }
                    yc++;
                }
            }
            return m3x3;
        }

        public Double Determinant
        {
            get
            {
                double result = 0;
                for (int i = 0; i < 4; i++) {
                    double[,] m3x3 = getSubMatrix(i, 0);
                    result += this[i,0] * Math.Pow(-1, i) * SarrusRule(m3x3);
                }
                return result;
            }
        }

        public Matrix44 Inversed
        {
            get
            {
                Matrix44 result = Matrix44.Zero();
                double det = this.Determinant;
                if (det == 0){
                    throw new InvalidOperationException("Cannot compute inversed matrix");
                }
                for (int y = 0; y < 4; y++){
                    for (int x = 0; x < 4; x++){
                        result[x, y] = (Math.Pow(-1, x + y) * SarrusRule(getSubMatrix(y, x)) ) / det;
                    }
                }
                return result;
            }
        }

		#endregion

		#region Init

        private double[][] matValues;
		public Matrix44(
			Double m00, Double m01, Double m02, Double m03,
			Double m10, Double m11, Double m12, Double m13,
			Double m20, Double m21, Double m22, Double m23,
			Double m30, Double m31, Double m32, Double m33)
		{
            matValues = new double[4][];
            matValues[0] = new double[] {m00, m01, m02, m03};
            matValues[1] = new double[] { m10, m11, m12, m13 };
            matValues[2] = new double[] { m20, m21, m22, m23 };
            matValues[3] = new double[] { m30, m31, m32, m33 };
		}


        private Matrix44(Matrix44 other){
            matValues = new double[other.matValues.Length][];
            for (int i = 0; i < matValues.Length; i++) {
                matValues[i] = new double[other.matValues[i].Length];
                Array.Copy(matValues[i], other.matValues[i], other.matValues.Length);
            }    
        }

        public double this[int x,int y]
        {
            get{ return matValues[y][x];}
            set { matValues[y][x] = value;  }
        }

		#endregion

		#region Arithmetic Operations


		public static Matrix44 operator -(Matrix44 a)
		{
           Matrix44 result = Matrix44.Zero();
           for (int y = 0; y < a.matValues.Length; y++) {
               for (int x = 0; x < a.matValues.Length; x++) {
                  result[x,y] =  -1*a[x,y];
               } 
           }
           return result;
		}

		public static Matrix44 operator +(Matrix44 a, Matrix44 b)
		{
            Matrix44 result = Matrix44.Zero();
            for (int y = 0; y < a.matValues.Length; y++)
            {
                for (int x = 0; x < a.matValues.Length; x++)
                {
                    result[x,y] = a[x,y]  + b[x,y] ;
                }
            }
            return result;
		}

		public static Matrix44 operator -(Matrix44 a, Matrix44 b)
		{
            Matrix44 result = Matrix44.Zero();
            for (int y = 0; y < a.matValues.Length; y++)
            {
                for (int x = 0; x < a.matValues.Length; x++)
                {
                    result[x,y] = 0;
                }
            }
            return result;
		}

		public static Matrix44 operator *(Matrix44 a, Matrix44 b)
		{
            Matrix44 result = Matrix44.Zero();
            for (int y = 0; y < a.matValues.Length; y++)
            {
                for (int x = 0; x < a.matValues.Length; x++)
                {
                    result[x,y] = 0;
                    for (int i = 0; i < a.matValues.Length; i++)
                    {
                        result[x,y] += a[i,y] * b[x,i];
                    }
                }
            }
            return result;
		}

		public static Vector4 operator *(Vector4 a, Matrix44 b)
		{
            Vector4 result = Vector4.Zero;
            for (int y = 0; y < b.matValues.Length; y++)
            {
                result[y] = 0;
                for (int x = 0; x < b.matValues.Length; x++)
                {
                    result[y] += b[x, y] * a[x];
                }
            }
            return result;
		}


		public static Vector4 operator *(Matrix44 a, Vector4 b)
		{
            Vector4 result = Vector4.Zero;
            for (int x = 0; x < a.matValues.Length; x++)
            {
                result[x] = 0;
                for (int y = 0; y < a.matValues.Length; y++)
                {
                    result[x] += b[y] * a[x, y];
                }
            }
            return result;
		}


        public static Matrix44 operator *(double a, Matrix44 b)
        {
            Matrix44 result = Matrix44.Zero();
            for (int x = 0; x < b.matValues.Length; x++)
            {
                for (int y = 0; y < b.matValues.Length; y++)
                {
                    result[x,y] += a * b[x, y];
                }
            }
            return result;
        }

         public static Matrix44 operator *(Matrix44 a, double b){
             return b * a;
         }


		#endregion

        #region Transformations

        public static Matrix44 Scale(Vector4 scaleVector)
        {
            Matrix44 result = Matrix44.Identity;
            result[0, 0] = scaleVector[0];
            result[1, 1] = scaleVector[1];
            result[2, 2] = scaleVector[2];
            return result;
        }

        public static Matrix44 Translate(Vector4 translateVector)
        {
            Matrix44 result = Matrix44.Identity;
            result[3, 0] = translateVector[0];
            result[3, 1] = translateVector[1];
            result[3, 2] = translateVector[2];
            return result;
        }

        public static Matrix44 RotateX(double angleDeg)
        {
            angleDeg = MathEx.DegToRad(angleDeg);
            Matrix44 result = Matrix44.Identity;
            result[1, 1] = Math.Cos(angleDeg);
            result[2, 1] = -Math.Sin(angleDeg);
            result[1, 2] = Math.Sin(angleDeg);
            result[2, 2] = Math.Cos(angleDeg);
            return result;
        }

        public static Matrix44 RotateY(double angleDeg)
        {
            angleDeg = MathEx.DegToRad(angleDeg);
            Matrix44 result = Matrix44.Identity;
            result[0, 0] = Math.Cos(angleDeg);
            result[2, 0] = Math.Sin(angleDeg);
            result[0, 2] = -Math.Sin(angleDeg);
            result[2, 2] = Math.Cos(angleDeg);
            return result;
        }

        public static Matrix44 RotateZ(double angleDeg)
        {
            angleDeg = MathEx.DegToRad(angleDeg);
            Matrix44 result = Matrix44.Identity;
            result[0, 0] = Math.Cos(angleDeg);
            result[1, 0] = -Math.Sin(angleDeg);
            result[0, 1] = Math.Sin(angleDeg);
            result[1, 1] = Math.Cos(angleDeg);
            return result;
        }

        public override bool Equals(object o)
        {
            Matrix44 om = (Matrix44)o;
            for (int y = 0; y < this.matValues.Length; y++){
                for (int x = 0; x < this.matValues.Length; x++){
                    if (!Assert.almostEqual(this[y, x] , om[y, x])) {
                        return false;
                    }
                }
            }
            return true;
        }

        public override string ToString() {
            string result = "{";
            for (int y = 0; y < this.matValues.Length; y++){
                result += "{";
                for (int x = 0; x < this.matValues.Length; x++){
                    result += this[x,y];
                    if (x < this.matValues.Length - 1){
                        result += ",";
                    }
                }
                result += "}";
                if (y < this.matValues.Length - 1) {
                    result += ",";
                }
            }
            result +="}";
            return result;
        }

        #endregion
	}
}
