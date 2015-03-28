using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CG1.Ex01.Mathematics
{
	public static class MathEx
	{
		public static double DegToRad(double angleDeg)
		{
            //ToDo: This function will return radians
            return angleDeg / (180d / Math.PI);
		}

		public static double RadToDeg(double angleRad)
		{
            //ToDo: This function will return degrees
            return angleRad * (180d / Math.PI);
		}
	}
}
