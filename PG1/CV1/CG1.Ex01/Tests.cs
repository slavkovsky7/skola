using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using CG1.Ex01.Mathematics;
using System.Windows.Forms;

namespace CG1.Ex01
{
    public class TestFailedException : Exception {
        public TestFailedException(string msg): base(msg){}
    }

    class Assert
    {
        public static int TestCount = 0;
        public static int TestsOK = 0;
        public static void AreEqual(Object a , Object b, string testName = "" ){
            TestCount++;
            if ( !a.Equals(b)){
                throw new TestFailedException("Test" + testName + " failed :" + a.ToString() + " != " + b.ToString());
            }
            TestsOK++;
        }

        public static bool almostEqual(double a, double b)
        {
            return Math.Abs(a - b) < 0.01;
        }

        public static void AreEqual(double a, double b, string testName = "")
        {
            TestCount++;
            if (!almostEqual(a, b) )
            {
                throw new TestFailedException("Test" + testName + " failed :" + a.ToString() + " != " + b.ToString());
            }
            TestsOK++;
        }

        public static void AreNotEqual(Object a, Object b, string testName = "")
        {
            TestCount++;
            if (a.Equals(b))
            {
                throw new TestFailedException("Test " + testName + " failed :" + a.ToString() + " == " + b.ToString());
            }
            TestsOK++;
        }
    }

    class TestSet{
        public TestSet() {
            try{
                TestVector4();
                TestMatrix();
            }catch(TestFailedException ex){
                MessageBox.Show(ex.Message + Environment.NewLine + "----------------------------------------------------------" + Environment.NewLine + ex.StackTrace);
                return;
            }
            MessageBox.Show("All " +  Assert.TestCount + " tests are OK ");
        }

        public void TestVector4() {
            Assert.AreEqual(new Vector4(2, 3, 4, 5), new Vector4(2, 3, 4, 5));

            Assert.AreEqual(new Vector4(1, 2, 3, 4) + new Vector4(3, 4, 5, 6), new Vector4(4, 6, 8, 10));
            Assert.AreEqual(new Vector4(3, 4, 5, 6) - new Vector4(1, 2, 3, 4), new Vector4(2, 2, 2, 2));

            Assert.AreEqual(new Vector4(3, 4, 5, 6) * new Vector4(1, 2, 3, 4), (double)3 + 4 * 2 + 3 * 5 + 6 * 4);
            Assert.AreEqual(new Vector4(1, 2, 3, 4) * new Vector4(3, 4, 5, 6), (double)3 * 1 + 4 * 2 + 3 * 5 + 6 * 4);

            Assert.AreEqual(new Vector4(3, 4, 5, 6) ^ new Vector4(1, 2, 3, 4), new Vector4(3 * 1, 4 * 2, 3 * 5, 4 * 6));
            Assert.AreEqual(new Vector4(1, 2, 3, 4) ^ new Vector4(3, 4, 5, 6), new Vector4(3 * 1, 4 * 2, 3 * 5, 4 * 6));

            Assert.AreEqual(new Vector4(3, 3, 3).Length, 5.196152422706632);
            Assert.AreEqual(new Vector4(5, 0, 0).Length, 5);


            Assert.AreEqual(new Vector4(1, 2, 3, 4) * 3, new Vector4(3, 6, 9, 12));
            Assert.AreEqual(3.0 * (new Vector4(1, 2, 3, 4)), new Vector4(3, 6, 9, 12));

            Assert.AreEqual(new Vector4(3, 6, 9, 12) / 3, new Vector4(1, 2, 3, 4) );
            Assert.AreEqual(3 / new Vector4(3, 6, 9, 12), new Vector4(1, 2, 3, 4) );


            Assert.AreEqual(new Vector4(1, 2, 3) % new Vector4(4, 5, 6), new Vector4(-3, 6, -3));
            Assert.AreEqual(new Vector4(1, 2, 3, 6) % new Vector4(4, 5, 6, 7), new Vector4(-3, 6, -3));

            Assert.AreEqual(new Vector4(1, -2, 3) % new Vector4(-4, 7, 0), new Vector4(-21, -12, -1));
            Assert.AreEqual(new Vector4(1, 0, 0) % new Vector4(0, 1, 0), new Vector4(0, 0, 1));


            Assert.AreEqual(new Vector4(5, 6, 7) / new Vector4(1, 0, 0) , new Vector4(5,0,0));
            Assert.AreEqual(new Vector4(5, 3, -1) / new Vector4(1, 2, -6), new Vector4(17d/41d, 34d/41d, -102d/41d));
        }

        public void TestMatrix() { 
            Matrix44 m1 = new Matrix44( 
                2,1,4,-5,
                1,3,4,-5,
                1,4,-8,4,
                4,2,3,-8
            );


            Matrix44 m2 = new Matrix44(
                14,4, 5,-5,
                1,-3, 4, 5,
                9, 7, 5, 1,
               -1,-2, 3, 4
            );


            Assert.AreEqual(2 * m1, m1 + m1);
            Assert.AreEqual(3 * m2, m2 + m2 + m2);

            Assert.AreEqual(m1 - m1, Matrix44.Zero() );
            Assert.AreEqual(m2 - m2, Matrix44.Zero() );


            Assert.AreEqual(m1.Transposed, new Matrix44(
                2, 1, 1, 4,
                1, 3, 4, 2,
                4, 4, -8, 3,
               -5, -5, 4, -8));

            Assert.AreEqual(m2.Transposed, new Matrix44(
                14, 1, 9, -1,
                4, -3, 7, -2,
                5, 4, 5, 3,
               -5, 5, 1, 4));

            Assert.AreEqual(m1 * m2, new Matrix44(
                    70, 43, 19, -21,
                    58, 33, 22, -6,
                   -58,-72, -7, 23,
                    93, 47, 19, -39));

            Assert.AreEqual(m1 * new Vector4(14, 4, 5, -5), new Vector4(17, 36, 17, -30));
            Assert.AreEqual(m1 * new Vector4(14, -3, 1, -5), new Vector4(6, -1, 21, -11));

            Matrix44 m3 = new Matrix44(
                1, 2, 25, 0,
                5, 5, 5, 0,
                4, 3, 5, 0,
                0, 0, 0, 1
            );
            Vector4 u = new Vector4(12, 5, 2, 0);
            Assert.AreEqual(u * m3, new Vector4(72, 95, 73));
            Assert.AreEqual(m3 * u, new Vector4(45, 55, 335));

           Assert.AreEqual( Matrix44.SarrusRule( new double[,] { {4,5,8},{-1,2,4},{5,3,7}} ), 39 );

           Assert.AreEqual(m1.Determinant, 122);

           Matrix44 m1i = (1d/122d)*new Matrix44(
               176,-72,34,-48,
               27,25,17,-24,
               102,-14,10,-50,
               133,-35,25,-64);

           Assert.AreEqual(m1.Inversed, m1i);
           Assert.AreEqual( m1.Inversed * m1, Matrix44.Identity );


           Matrix44 ms1 = new Matrix44(
               2,0,0,0,
               0,3,0,0,
               0,0,-5,0,
               0,0,0,1);
           Assert.AreEqual(ms1, Matrix44.Scale(new Vector4(2, 3, -5)));

           Matrix44 ms2 = new Matrix44(
               4, 0, 0, 0,
               0, 6, 0, 0,
               0, 0, -5, 0,
               0, 0, 0, 1);
           Assert.AreEqual(ms2, Matrix44.Scale(new Vector4(4, 6, -5)));
    

           Matrix44 mt1 = new Matrix44(
               1, 0, 0, 2,
               0, 1, 0, 3,
               0, 0, 1, -5,
               0, 0, 0, 1);
           Assert.AreEqual(mt1, Matrix44.Translate(new Vector4(2, 3, -5)));
           Matrix44 mt2 = new Matrix44(
               1, 0, 0, 2,
               0, 1, 0, 7,
               0, 0, 1, 5,
               0, 0, 0, 1);
           Assert.AreEqual(mt2, Matrix44.Translate(new Vector4(2, 7, 5)));



           Matrix44 mRx1 = new Matrix44(
               1,0,0,0,
               0,0.98,-0.17,0,
               0,0.17,0.98,0,
               0,0,0,1
           );

           Matrix44 mRx2 = new Matrix44(
               1, 0, 0, 0,
               0, 0.82, -.57, 0,
               0, 0.57, 0.82, 0,
               0, 0, 0, 1
           );

           Assert.AreEqual(mRx1, Matrix44.RotateX(10) );
           Assert.AreEqual(mRx2, Matrix44.RotateX(35) );

           Matrix44 mRy1 = new Matrix44(
               0.98 , 0     , 0.17  , 0,
               0    , 1     , 0     , 0,
              -0.17 , 0     , 0.98  , 0,
               0    , 0     , 0     , 1
           );

           Matrix44 mRy2 = new Matrix44(
               0.82, 0, 0.57, 0,
               0, 1, 0, 0,
              -0.57, 0, 0.82, 0,
               0, 0, 0, 1
           );

           Assert.AreEqual(mRy1, Matrix44.RotateY(10));
           Assert.AreEqual(mRy2, Matrix44.RotateY(35));


           Matrix44 mRz1 = new Matrix44(
               0.98,-0.17,0, 0,
               0.17, 0.98, 0, 0,
               0, 0, 1, 0,
               0, 0, 0, 1
            );

           Matrix44 mRz2 = new Matrix44(
               0.82, -0.57,0,0,
               0.57, 0.82, 0, 0,
               0, 0, 1, 0,
               0, 0, 0, 1
           );

           Assert.AreEqual(mRz1, Matrix44.RotateZ(10));
           Assert.AreEqual(mRz2, Matrix44.RotateZ(35));
        }
    }
}
