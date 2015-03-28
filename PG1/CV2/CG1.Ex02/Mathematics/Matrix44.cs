﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CG1.Ex02.Mathematics
{
	public struct Matrix44
	{
		#region Properties

		public const Int32 Dim = 4;

		public Double M00, M01, M02, M03;
		public Double M10, M11, M12, M13;
		public Double M20, M21, M22, M23;
		public Double M30, M31, M32, M33;


        public static readonly Matrix44 Zero = new Matrix44(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

		public static Matrix44 Identity
		{
			get { return new Matrix44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1); }
		}

        public Matrix44 Transposed
        {
            get
            {
                return new Matrix44(
                    M00, M10, M20, M30,
                    M01, M11, M21, M31,
                    M02, M12, M22, M32,
                    M03, M13, M23, M33
                );
            }
        }

        public Double Determinant
        {
            get
            {
                return
                M03 * M12 * M21 * M30 - M02 * M13 * M21 * M30 - M03 * M11 * M22 * M30 + M01 * M13 * M22 * M30 +
                M02 * M11 * M23 * M30 - M01 * M12 * M23 * M30 - M03 * M12 * M20 * M31 + M02 * M13 * M20 * M31 +
                M03 * M10 * M22 * M31 - M00 * M13 * M22 * M31 - M02 * M10 * M23 * M31 + M00 * M12 * M23 * M31 +
                M03 * M11 * M20 * M32 - M01 * M13 * M20 * M32 - M03 * M10 * M21 * M32 + M00 * M13 * M21 * M32 +
                M01 * M10 * M23 * M32 - M00 * M11 * M23 * M32 - M02 * M11 * M20 * M33 + M01 * M12 * M20 * M33 +
                M02 * M10 * M21 * M33 - M00 * M12 * M21 * M33 - M01 * M10 * M22 * M33 + M00 * M11 * M22 * M33;
            }
        }

        public Matrix44 Inversed
        {
            get
            {
                Double idet = Determinant;

                if (idet == 0) return Zero;
                idet = 1 / idet;

                Matrix44 result = new Matrix44();

                result.M00 = idet * (M12 * M23 * M31 - M13 * M22 * M31 + M13 * M21 * M32 - M11 * M23 * M32 - M12 * M21 * M33 + M11 * M22 * M33);
                result.M01 = idet * (M03 * M22 * M31 - M02 * M23 * M31 - M03 * M21 * M32 + M01 * M23 * M32 + M02 * M21 * M33 - M01 * M22 * M33);
                result.M02 = idet * (M02 * M13 * M31 - M03 * M12 * M31 + M03 * M11 * M32 - M01 * M13 * M32 - M02 * M11 * M33 + M01 * M12 * M33);
                result.M03 = idet * (M03 * M12 * M21 - M02 * M13 * M21 - M03 * M11 * M22 + M01 * M13 * M22 + M02 * M11 * M23 - M01 * M12 * M23);
                result.M10 = idet * (M13 * M22 * M30 - M12 * M23 * M30 - M13 * M20 * M32 + M10 * M23 * M32 + M12 * M20 * M33 - M10 * M22 * M33);
                result.M11 = idet * (M02 * M23 * M30 - M03 * M22 * M30 + M03 * M20 * M32 - M00 * M23 * M32 - M02 * M20 * M33 + M00 * M22 * M33);
                result.M12 = idet * (M03 * M12 * M30 - M02 * M13 * M30 - M03 * M10 * M32 + M00 * M13 * M32 + M02 * M10 * M33 - M00 * M12 * M33);
                result.M13 = idet * (M02 * M13 * M20 - M03 * M12 * M20 + M03 * M10 * M22 - M00 * M13 * M22 - M02 * M10 * M23 + M00 * M12 * M23);
                result.M20 = idet * (M11 * M23 * M30 - M13 * M21 * M30 + M13 * M20 * M31 - M10 * M23 * M31 - M11 * M20 * M33 + M10 * M21 * M33);
                result.M21 = idet * (M03 * M21 * M30 - M01 * M23 * M30 - M03 * M20 * M31 + M00 * M23 * M31 + M01 * M20 * M33 - M00 * M21 * M33);
                result.M22 = idet * (M01 * M13 * M30 - M03 * M11 * M30 + M03 * M10 * M31 - M00 * M13 * M31 - M01 * M10 * M33 + M00 * M11 * M33);
                result.M23 = idet * (M03 * M11 * M20 - M01 * M13 * M20 - M03 * M10 * M21 + M00 * M13 * M21 + M01 * M10 * M23 - M00 * M11 * M23);
                result.M30 = idet * (M12 * M21 * M30 - M11 * M22 * M30 - M12 * M20 * M31 + M10 * M22 * M31 + M11 * M20 * M32 - M10 * M21 * M32);
                result.M31 = idet * (M01 * M22 * M30 - M02 * M21 * M30 + M02 * M20 * M31 - M00 * M22 * M31 - M01 * M20 * M32 + M00 * M21 * M32);
                result.M32 = idet * (M02 * M11 * M30 - M01 * M12 * M30 - M02 * M10 * M31 + M00 * M12 * M31 + M01 * M10 * M32 - M00 * M11 * M32);
                result.M33 = idet * (M01 * M12 * M20 - M02 * M11 * M20 + M02 * M10 * M21 - M00 * M12 * M21 - M01 * M10 * M22 + M00 * M11 * M22);

                return result;
            }
        }

		#endregion

		#region Init

		public Matrix44(
			Double m00, Double m01, Double m02, Double m03,
			Double m10, Double m11, Double m12, Double m13,
			Double m20, Double m21, Double m22, Double m23,
			Double m30, Double m31, Double m32, Double m33)
		{
			M00 = m00; M01 = m01; M02 = m02; M03 = m03;
			M10 = m10; M11 = m11; M12 = m12; M13 = m13;
			M20 = m20; M21 = m21; M22 = m22; M23 = m23;
			M30 = m30; M31 = m31; M32 = m32; M33 = m33;
		}

		#endregion

		#region Arithmetic Operations

		public static Matrix44 operator -(Matrix44 a)
		{
			return new Matrix44(
				-a.M00, -a.M01, -a.M02, -a.M03,
				-a.M10, -a.M11, -a.M12, -a.M13,
				-a.M20, -a.M21, -a.M22, -a.M23,
				-a.M30, -a.M31, -a.M32, -a.M33
			);
		}

		public static Matrix44 operator +(Matrix44 a, Matrix44 b)
		{
			return new Matrix44(
				a.M00 + b.M00, a.M01 + b.M01, a.M02 + b.M02, a.M03 + b.M03,
				a.M10 + b.M10, a.M11 + b.M11, a.M12 + b.M12, a.M13 + b.M13,
				a.M20 + b.M20, a.M21 + b.M21, a.M22 + b.M22, a.M23 + b.M23,
				a.M30 + b.M30, a.M31 + b.M31, a.M32 + b.M32, a.M33 + b.M33
			);
		}

		public static Matrix44 operator -(Matrix44 a, Matrix44 b)
		{
			return new Matrix44(
				a.M00 - b.M00, a.M01 - b.M01, a.M02 - b.M02, a.M03 - b.M03,
				a.M10 - b.M10, a.M11 - b.M11, a.M12 - b.M12, a.M13 - b.M13,
				a.M20 - b.M20, a.M21 - b.M21, a.M22 - b.M22, a.M23 - b.M23,
				a.M30 - b.M30, a.M31 - b.M31, a.M32 - b.M32, a.M33 - b.M33
			);
		}

		public static Matrix44 operator *(Matrix44 a, Matrix44 b)
		{
			return new Matrix44(
				a.M00 * b.M00 + a.M01 * b.M10 + a.M02 * b.M20 + a.M03 * b.M30,
				a.M00 * b.M01 + a.M01 * b.M11 + a.M02 * b.M21 + a.M03 * b.M31,
				a.M00 * b.M02 + a.M01 * b.M12 + a.M02 * b.M22 + a.M03 * b.M32,
				a.M00 * b.M03 + a.M01 * b.M13 + a.M02 * b.M23 + a.M03 * b.M33,

				a.M10 * b.M00 + a.M11 * b.M10 + a.M12 * b.M20 + a.M13 * b.M30,
				a.M10 * b.M01 + a.M11 * b.M11 + a.M12 * b.M21 + a.M13 * b.M31,
				a.M10 * b.M02 + a.M11 * b.M12 + a.M12 * b.M22 + a.M13 * b.M32,
				a.M10 * b.M03 + a.M11 * b.M13 + a.M12 * b.M23 + a.M13 * b.M33,
				
				a.M20 * b.M00 + a.M21 * b.M10 + a.M22 * b.M20 + a.M23 * b.M30,
				a.M20 * b.M01 + a.M21 * b.M11 + a.M22 * b.M21 + a.M23 * b.M31,
				a.M20 * b.M02 + a.M21 * b.M12 + a.M22 * b.M22 + a.M23 * b.M32,
				a.M20 * b.M03 + a.M21 * b.M13 + a.M22 * b.M23 + a.M23 * b.M33,
				
				a.M30 * b.M00 + a.M31 * b.M10 + a.M32 * b.M20 + a.M33 * b.M30,
				a.M30 * b.M01 + a.M31 * b.M11 + a.M32 * b.M21 + a.M33 * b.M31,
				a.M30 * b.M02 + a.M31 * b.M12 + a.M32 * b.M22 + a.M33 * b.M32,
				a.M30 * b.M03 + a.M31 * b.M13 + a.M32 * b.M23 + a.M33 * b.M33
			);
		}

		public static Vector4 operator *(Vector4 a, Matrix44 b)
		{
			return new Vector4(
				a.X * b.M00 + a.Y * b.M10 + a.Z * b.M20 + a.W * b.M30,
				a.X * b.M01 + a.Y * b.M11 + a.Z * b.M21 + a.W * b.M31,
				a.X * b.M02 + a.Y * b.M12 + a.Z * b.M22 + a.W * b.M32,
				a.X * b.M03 + a.Y * b.M13 + a.Z * b.M23 + a.W * b.M33
			);
		}

		public static Vector4 operator *(Matrix44 a, Vector4 b)
		{
			return new Vector4(
				a.M00 * b.X + a.M01 * b.Y + a.M02 * b.Z + a.M03 * b.W,
				a.M10 * b.X + a.M11 * b.Y + a.M12 * b.Z + a.M13 * b.W,
				a.M20 * b.X + a.M21 * b.Y + a.M22 * b.Z + a.M23 * b.W,
				a.M30 * b.X + a.M31 * b.Y + a.M32 * b.Z + a.M33 * b.W
			);
		}

		#endregion

        #region Transformations

        public static Matrix44 TransformMatrix(Vector4 pivot, Double deg, Vector4 scale)
        {
            return Matrix44.Translate(pivot) * Matrix44.RotateZ(deg) * Matrix44.Scale(scale);
        }

        public static Matrix44 Scale(Vector4 scaleVector)
        {
            Matrix44 result = Identity;

            result.M00 = scaleVector.X;
            result.M11 = scaleVector.Y;
            result.M22 = scaleVector.Z;

            return result;
        }

        public static Matrix44 Translate(Vector4 translateVector)
        {
            Matrix44 result = Identity;

            result.M03 = translateVector.X;
            result.M13 = translateVector.Y;
            result.M23 = translateVector.Z;

            return result;
        }

        public static Matrix44 RotateX(double angleDeg)
        {
            Double angleRad = MathEx.DegToRad(angleDeg);
            Matrix44 result = Identity;
            result.M11 = Math.Cos(angleRad);
            result.M12 = -Math.Sin(angleRad);
            result.M21 = Math.Sin(angleRad);
            result.M22 = Math.Cos(angleRad);

            return result;
        }

        public static Matrix44 RotateY(double angleDeg)
        {
            Double angleRad = MathEx.DegToRad(angleDeg);
            Matrix44 result = Identity;
            result.M00 = Math.Cos(angleRad);
            result.M02 = Math.Sin(angleRad);
            result.M20 = -Math.Sin(angleRad);
            result.M22 = Math.Cos(angleRad);

            return result;
        }

        public static Matrix44 RotateZ(double angleDeg)
        {
            Double angleRad = MathEx.DegToRad(angleDeg);
            Matrix44 result = Identity;
            result.M00 = Math.Cos(angleRad);
            result.M01 = -Math.Sin(angleRad);
            result.M10 = Math.Sin(angleRad);
            result.M11 = Math.Cos(angleRad);

            return result;
        }

        #endregion
	}
}
