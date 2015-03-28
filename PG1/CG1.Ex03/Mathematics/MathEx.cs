using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CG1.Ex03.Mathematics
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

        public static long Factiorial(int n){
            long result = 1;
            for ( long i = 1 ; i <=n; i++){
                result = result * i;
            }
            return result;
        }

        public static long CombinationNumber(int n, int k){
           /* long a = Factiorial(n) ;
            long b = (Factiorial(n - k) * Factiorial(k));
            return (int)(a / b) ;*/

            if (k > n) return 0;
            long r = 1;
            long d;
            for (d = 1; d <= k; d++)
            {
                r *= n--;
                r /= d;
            }
            return r;
        }
	}
}
