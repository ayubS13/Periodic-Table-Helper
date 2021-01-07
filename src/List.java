/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ayubs
 */
public class List {
    public Element head; 
    public Element tail; 
    
    public List(){
        head = tail = null;
    }
    public void addElement(Element temp){ //this will add the element at the tail
       
        if(head == null){
            head = temp;
        }
        if(tail != null){
            tail.next = temp;
        }
        
        temp.next = null;
        temp.previous = tail;
        tail = temp;
    } 
    public int numNodes(){
        int count = 0;
        Element curr = head;
        while(curr != null){
            count++;
            curr = curr.next;
        }
        return count;
    }
    public void displayListForward(){
        //System.out.println("head next is: "+ head.next.getName());
        Element curr = head;
        if(curr == null){
            System.out.println("List is empty");
        }
        
        while(curr != null){
            System.out.println("head next is: "+ head.next.getName());
            System.out.println(curr.getName());
            curr = curr.next;
        }
    }
   
    public void deleteElement(Element node){
        if(head == null || node == null){
            return;
        }
        if(head == node){
            head = node.next;
        }
        if(node.next != null){
            node.next.previous = node.previous;
        }
        if(node.previous != null){
            node.previous.next = node.next;
        }
        return;
    }
    
    public void displayListBackward(){
        Element curr = tail;
        if(curr == null){
            System.out.println("List is empty");
        }
        while(curr != null){
            System.out.println(curr.getName());
            curr = curr.previous;
        }
    }
    public Element findElement(String name){
        Element found = null;
        //System.out.println(head.getName());
        Element curr = head;
        /*if(curr.getName().equals(name)){
            found = curr;
            return found;
        }*/
        //curr = curr.next;
        while(curr != null){
            if(curr.getName().equals(name)){
                found = curr;
            }
            curr = curr.next;
        }
        return found;
    }
}

