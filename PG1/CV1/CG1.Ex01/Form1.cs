using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using CG1.Ex01.Mathematics;
using System.Globalization;
namespace CG1.Ex01
{
    public partial class Form1 : Form
    {
        //ToDo: Define three new vectors U, V and W.

        //ToDo: Define three new matrices A, B and C.

        public Form1()
        {
            InitializeComponent();
        }

        /// <summary>
        /// Will try to parse String value to Double value.
        /// </summary>
        double Parse(String text)
        {
            try{
                Double res = Double.Parse(text);
                return res;
            }catch(Exception e){
                return 0;
            }
        }

        /// <summary>
        /// Read values from GUI and assign them to Matrices and Vectors from GUI.
        /// </summary>
        /// 

        Matrix44 A = Matrix44.Zero();
        Matrix44 B = Matrix44.Zero();
        Matrix44 C = Matrix44.Zero();

        Vector4 U = new Vector4(0, 0, 0);
        Vector4 V = new Vector4(0, 0, 0);
        Vector4 W = new Vector4(0, 0, 0);

        void ReadValues()
        {
            A = Matrix44.Zero();
            B = Matrix44.Zero();
            C = Matrix44.Zero();
            U = new Vector4(0, 0, 0);
            V = new Vector4(0, 0, 0);
            W = new Vector4(0, 0, 0);
            //////////////////////////
            A[0,0] = Parse(tbA00.Text);
            A[0,1] = Parse(tbA01.Text);
            A[0,2] = Parse(tbA02.Text);
            A[0,3] = Parse(tbA03.Text);
            A[1,0] = Parse(tbA10.Text);
            A[1,1] = Parse(tbA11.Text);
            A[1,2] = Parse(tbA12.Text);
            A[1,3] = Parse(tbA13.Text);
            A[2,0] = Parse(tbA20.Text);
            A[2,1] = Parse(tbA21.Text);
            A[2,2] = Parse(tbA22.Text);
            A[2,3] = Parse(tbA23.Text);
            A[3,0] = Parse(tbA30.Text);
            A[3,1] = Parse(tbA31.Text);
            A[3,2] = Parse(tbA32.Text);
            A[3,3] = Parse(tbA33.Text);

            B[0,0] = Parse(tbB00.Text);
            B[0,1] = Parse(tbB01.Text);
            B[0,2] = Parse(tbB02.Text);
            B[0,3] = Parse(tbB03.Text);
            B[1,0] = Parse(tbB10.Text);
            B[1,1] = Parse(tbB11.Text);
            B[1,2] = Parse(tbB12.Text);
            B[1,3] = Parse(tbB13.Text);
            B[2,0] = Parse(tbB20.Text);
            B[2,1] = Parse(tbB21.Text);
            B[2,2] = Parse(tbB22.Text);
            B[2,3] = Parse(tbB23.Text);
            B[3,0] = Parse(tbB30.Text);
            B[3,1] = Parse(tbB31.Text);
            B[3,2] = Parse(tbB32.Text);
            B[3,3] = Parse(tbB33.Text);

            U[0] = Parse(tbUX.Text);
            U[1] = Parse(tbUY.Text);
            U[2] = Parse(tbUZ.Text);
            U[3] = Parse(tbUW.Text);

            V[0] = Parse(tbVX.Text);
            V[1] = Parse(tbVY.Text);
            V[2] = Parse(tbVZ.Text);
            V[3] = Parse(tbVW.Text);
        }

        /// <summary>
        /// Write values from Matrices and Vectors and assign them back to GUI.
        /// </summary>
        void WriteValues()
        {
            //ToDo: Simply uncomment when vectors and matrices are declared.
            //ToDo: What is "F2" parameter of ToString function?
            
            tbWX.Text = W[0].ToString("F2");
            tbWY.Text = W[1].ToString("F2");
            tbWZ.Text = W[2].ToString("F2");
            
            tbC00.Text = C[0,0].ToString("F2");
            tbC01.Text = C[0,1].ToString("F2");
            tbC02.Text = C[0,2].ToString("F2");
            tbC03.Text = C[0,3].ToString("F2");
            tbC10.Text = C[1,0].ToString("F2");
            tbC11.Text = C[1,1].ToString("F2");
            tbC12.Text = C[1,2].ToString("F2");
            tbC13.Text = C[1,3].ToString("F2");
            tbC20.Text = C[2,0].ToString("F2");
            tbC21.Text = C[2,1].ToString("F2");
            tbC22.Text = C[2,2].ToString("F2");
            tbC23.Text = C[2,3].ToString("F2");           
        }

        /// <summary>
        /// Main function of today's seminar. Will run after we click on button 'Calculate'. 
        /// </summary>
        private void bCalc_Click(object sender, EventArgs e)
        {
            ReadValues();
            switch ( cbOp.Text ) {
                case "W = U + V": W = U + V; break;
                case "W = U - V": W = U - V; break;
                case "W = U / V": W = U / V; break;
                case "W = U ^ V": W = U ^ V; break;
                case "W = U % V": W = U % V; break;
                case "W.X = U * V": W.X = U * V; break;
                case "W.X = Length(U)": W.X = U.Length; break;
                case "W.X = A.Determinant": W.X = A.Determinant;  break;
                case "C = A + B": C = A + B; break;
                case "C = A - B": C = A - B; break;
                case "C = A * B": C = A * B; break;
                case "C = A.Transposed": C = A.Transposed; break ;
                case "C = A.Inversed": C = A.Inversed;  break;
                case "W = A * V": W = A * V; break;
                case "W = U * B": W = U * B;  break;
                case "C = Matrix44.Scale(U)": C = Matrix44.Scale(U); break;
                case "C = Matrix44.Translate(U)": C = Matrix44.Translate(U);  break;
                case "C = Matrix44.RotateX(U.X)": C = Matrix44.RotateX(U.X);  break;
                case "C = Matrix44.RotateY(U.X)": C = Matrix44.RotateY(U.X);  break;
                case "C = Matrix44.RotateZ(U.X)": C = Matrix44.RotateZ(U.X);  break; ;
            }

            if (cbOp.Text != ""){
                WriteValues();
            }
        }

        private void cbOp_SelectedIndexChanged(object sender, EventArgs e)
        {

        }
    }
}
