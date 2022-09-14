/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3coveradder;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Andrea
 */
public class MyMouseListener implements MouseListener{

    private SharedInteger currentNumber;
    private List<Integer> relatedSongs;
    private JLabel relatedLabel;
    private String coverPath;
    
    public MyMouseListener(SharedInteger currentNumber, JLabel relatedLabel, String coverPath){
        this.currentNumber = currentNumber;
        this.relatedLabel = relatedLabel;
        relatedSongs = new ArrayList<>();
        this.coverPath = coverPath;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        if(SwingUtilities.isLeftMouseButton(me)) {
            relatedSongs.add(currentNumber.getNumber());
            currentNumber.increment();
        } else if(SwingUtilities.isRightMouseButton(me)) {
            currentNumber.decrement();
            relatedSongs.remove((Integer)currentNumber.getNumber());
        }
        
        StringBuilder sb = new StringBuilder();
        for(Integer i : relatedSongs){
            sb.append(i).append(" ");
        }
        relatedLabel.setText(sb.toString());
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
    
    public void reset(){
        relatedSongs = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(Integer i : relatedSongs){
            sb.append(i).append(" ");
        }
        relatedLabel.setText(sb.toString());
    }

    public List<Integer> getRelatedSongs() {
        return relatedSongs;
    }

    public String getCoverPath() {
        return coverPath;
    }
}
