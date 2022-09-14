/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3coveradder;

/**
 *
 * @author Andrea
 */
public class SharedInteger {
    private int number;
    
    public SharedInteger(int number){
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
    public void increment(){
        number++;
    }
    
    public void decrement(){
        if(number>0){
            number--;
        }
    }
}
