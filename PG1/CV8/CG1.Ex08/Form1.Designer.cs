namespace CG1.Ex08
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.panel1 = new System.Windows.Forms.Panel();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.textBox1 = new System.Windows.Forms.TextBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.rbScan = new System.Windows.Forms.RadioButton();
            this.rbFlood = new System.Windows.Forms.RadioButton();
            this.panel1.SuspendLayout();
            this.groupBox3.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // panel1
            // 
            this.panel1.BackColor = System.Drawing.SystemColors.Control;
            this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.panel1.Controls.Add(this.groupBox3);
            this.panel1.Controls.Add(this.groupBox1);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Right;
            this.panel1.Location = new System.Drawing.Point(500, 0);
            this.panel1.Name = "panel1";
            this.panel1.Padding = new System.Windows.Forms.Padding(10);
            this.panel1.Size = new System.Drawing.Size(187, 498);
            this.panel1.TabIndex = 5;
            // 
            // groupBox3
            // 
            this.groupBox3.Controls.Add(this.textBox1);
            this.groupBox3.Location = new System.Drawing.Point(13, 92);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(157, 69);
            this.groupBox3.TabIndex = 4;
            this.groupBox3.TabStop = false;
            this.groupBox3.Text = "Help";
            // 
            // textBox1
            // 
            this.textBox1.BackColor = System.Drawing.SystemColors.Control;
            this.textBox1.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.textBox1.Location = new System.Drawing.Point(14, 19);
            this.textBox1.Multiline = true;
            this.textBox1.Name = "textBox1";
            this.textBox1.Size = new System.Drawing.Size(172, 44);
            this.textBox1.TabIndex = 1;
            this.textBox1.Text = "1) Select method\r\n2) Left clicks - draw polygon\r\n3) Right click - fill polygon";
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.rbScan);
            this.groupBox1.Controls.Add(this.rbFlood);
            this.groupBox1.Location = new System.Drawing.Point(11, 9);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(159, 63);
            this.groupBox1.TabIndex = 2;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Algorithms";
            // 
            // rbScan
            // 
            this.rbScan.AutoSize = true;
            this.rbScan.ImeMode = System.Windows.Forms.ImeMode.NoControl;
            this.rbScan.Location = new System.Drawing.Point(16, 19);
            this.rbScan.Name = "rbScan";
            this.rbScan.Size = new System.Drawing.Size(73, 17);
            this.rbScan.TabIndex = 2;
            this.rbScan.TabStop = true;
            this.rbScan.Text = "Scan Line\r\n";
            this.rbScan.UseVisualStyleBackColor = true;
            this.rbScan.CheckedChanged += new System.EventHandler(this.rbScan_CheckedChanged);
            // 
            // rbFlood
            // 
            this.rbFlood.AutoSize = true;
            this.rbFlood.ImeMode = System.Windows.Forms.ImeMode.NoControl;
            this.rbFlood.Location = new System.Drawing.Point(16, 40);
            this.rbFlood.MinimumSize = new System.Drawing.Size(120, 0);
            this.rbFlood.Name = "rbFlood";
            this.rbFlood.Size = new System.Drawing.Size(120, 17);
            this.rbFlood.TabIndex = 3;
            this.rbFlood.TabStop = true;
            this.rbFlood.Text = "Flood Fill (Bonus)";
            this.rbFlood.UseVisualStyleBackColor = true;
            this.rbFlood.CheckedChanged += new System.EventHandler(this.rbFlood_CheckedChanged);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.ControlLight;
            this.ClientSize = new System.Drawing.Size(687, 498);
            this.Controls.Add(this.panel1);
            this.DoubleBuffered = true;
            this.Name = "Form1";
            this.Text = "CG1.Ex08 - Virtual Knitting & Floods ";
            this.panel1.ResumeLayout(false);
            this.groupBox3.ResumeLayout(false);
            this.groupBox3.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.GroupBox groupBox3;
        private System.Windows.Forms.TextBox textBox1;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.RadioButton rbScan;
        private System.Windows.Forms.RadioButton rbFlood;
    }
}

