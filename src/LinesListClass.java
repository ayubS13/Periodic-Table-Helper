
import javax.swing.JComponent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ayubs
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JFrame;


public class LinesListClass extends JComponent{
    private class Line{
        int initialX, initialY, endX, endY;
        Color color;
        Line next;
        Line previous;

        public Line(int x1, int y1, int x2, int y2, Color color) {
            initialX = x1; initialY = y1; endX = x2; endY = y2;
            this.color = color;
            next = null;
            previous = null;
        }               
    }
    Line lines; //this is the singly linked list of lines
    
    Line head; //this is the head node
    Line tail; //this is the tail node
    public LinesListClass(){
        head = tail;       
    }
    
    
    
    public void addLine(int xval1, int yval1, int xval2, int yval2, Color color) {
        Line temp = new Line(xval1, yval1, xval2,yval2, color);
        //System.out.println("added a new Line" + xval1+","+yval1 +" " + xval2+"," +yval2);
    
        if(head == null){
            head = temp;
        }
        if(tail != null){
            tail.next = temp;
        }
        temp.next = null;
        temp.previous = tail;
        tail = temp;
                
        //repaint();
    }

    public void clearAllLines() {
        head = null;
        tail = null;
        //repaint();
    }
    
    public void printLines(Graphics g){
        Line curr = head;
        while(curr != null){
            g.setColor(curr.color);
            g.drawLine(curr.initialX, curr.initialY, curr.endX, curr.endY);
            //System.out.println("Drawing a line from:" + curr.initialX + "," + curr.initialY +"  " + curr.endX +"," + curr.endY);
            curr = curr.next;
        }
        
        this.repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Line curr = head;
        while(curr != null){
            g.setColor(curr.color);
            g.drawLine(curr.initialX, curr.initialY, curr.endX, curr.endY);
            curr = curr.next;
        }
    }
}
/*@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    for (Line line : lines) {
        g.setColor(line.color);
        g.drawLine(line.x1, line.y1, line.x2, line.y2);
    }
}*/

