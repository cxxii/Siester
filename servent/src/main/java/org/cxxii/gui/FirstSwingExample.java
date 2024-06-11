package org.cxxii.gui;

import javax.swing.*;
public class FirstSwingExample {
    public static void main(String[] args) {
        JFrame f=new JFrame();//creating instance of JFrame

        JButton b=new JButton("click");//creating instance of JButton
        b.setBounds(130,100,100, 40);//x axis, y axis, width, height


        JButton ee = new JButton("Fff");
        f.add(b);//adding button in JFrame
        f.add(ee);//adding button in JFrame

        f.setSize(1100,500);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
    }
}