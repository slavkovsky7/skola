using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CG1.Ex05.Mathematics
{
	public static class MathEx
	{
		public static double DegToRad(double angleDeg)
		{
			return Math.PI / 180 * angleDeg;
		}

		public static double RadToDeg(double angleRad)
		{
			return 180 / Math.PI * angleRad;
		}
	}
}
