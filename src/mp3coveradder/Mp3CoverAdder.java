package mp3coveradder;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Mp3CoverAdder {
    
    private static final String COVERS_FOLDER = "C:\\Users\\Andrea\\Desktop\\c";
    private static final String SONGS_FOLDER = "C:\\Users\\Andrea\\Desktop\\s";
    private static final String TEMP_SONG = "C:\\Users\\Andrea\\Desktop\\temp.mp3";
    
    private static JFrame window;
    private static File coversFolder;
    private static File[] songsFolder;
    private static SharedInteger currentNumber;
    
    public static void main(String[] args) throws Exception{
        setLookAndFeel();
        
        coversFolder = new File(COVERS_FOLDER);
        songsFolder = new File(SONGS_FOLDER).listFiles();
        currentNumber = new SharedInteger(1);
        
        //GRAPHICS
        createWindow();
    }
    
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {}
    }
    
    public static void createWindow() throws IOException{
        window = new JFrame("Music Cover Adder");
        
        int i = 1;
        StringBuilder sb = new StringBuilder("<html>");
        for(File f : songsFolder){
            sb.append(i).append(") ");
            i++;
            sb.append(f.getName()).append("<br>");
        }
        sb.append("</html>");
        
        JLabel songsLabel = new JLabel(sb.toString());
        songsLabel.setFont(songsLabel.getFont().deriveFont(25f));
        songsLabel.setVerticalAlignment(JLabel.TOP);
        
        List<JLabel> coversLabels = new ArrayList();
        for(File f : coversFolder.listFiles()){
            ImageIcon image = new ImageIcon(Files.readAllBytes(f.toPath()));
            JLabel label = new JLabel(new ImageIcon(resizeImageSmooth(image.getImage(), 150, 150)));
            label.setFont(label.getFont().deriveFont(20f));
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            label.addMouseListener(new MyMouseListener(currentNumber, label, f.getPath()));
            coversLabels.add(label);
        }
        
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridLayout(1,3));
        
        JScrollPane songsScrollPane = new JScrollPane();
        songsScrollPane.setViewportView(songsLabel);
        songsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        songsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        songsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        
        JPanel coversContainerPanel = new JPanel(new GridLayout(0,4));
        for(JLabel l : coversLabels){
            coversContainerPanel.add(l);
        }
        
        JScrollPane coversScrollPane = new JScrollPane();
        coversScrollPane.setViewportView(coversContainerPanel);
        coversScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        coversScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        coversScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel buttonsContainerPanel = new JPanel(new GridLayout(2, 1));
        JButton resetButton = new JButton("RESET");
        JButton saveButton = new JButton("SAVE");
        resetButton.setFont(resetButton.getFont().deriveFont(25f));
        saveButton.setFont(saveButton.getFont().deriveFont(25f));
        resetButton.setForeground(Color.RED);
        saveButton.setForeground(new Color(34,177,76));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                for(JLabel l : coversLabels){
                    ((MyMouseListener)l.getMouseListeners()[0]).reset();
                }
                currentNumber.setNumber(1);
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    ImageResizer.resizeImagesInFolder(coversFolder);
                } catch (IOException ex) {}
                
                for(JLabel l : coversLabels){
                    MyMouseListener mml = ((MyMouseListener)l.getMouseListeners()[0]);
                    for(Integer songNumber : mml.getRelatedSongs()){
                        try {
                            File song = songsFolder[songNumber-1];
                            Mp3File mp3file = new Mp3File(song);
                            File cover = new File(mml.getCoverPath());
                            setCoverToSong(mp3file, song.getPath(), cover);
                        } catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException ex) {
                            Logger.getLogger(Mp3CoverAdder.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(null, "Done");
                System.exit(0);
            }
        });
        buttonsContainerPanel.add(resetButton);
        buttonsContainerPanel.add(saveButton);
        
        containerPanel.add(songsScrollPane);
        containerPanel.add(coversScrollPane);
        containerPanel.add(buttonsContainerPanel);
        
        window.add(containerPanel);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
    
    public static void setCoverToSong(Mp3File mp3file, String songFileName, File cover) throws IOException, NotSupportedException{
        cover = new File(cover.getPath().replace(".jpg", ".png").replace(".jpeg", ".png"));
        byte[] bytes = Files.readAllBytes(cover.toPath());
        
        ID3v2 id3v2;
        if(mp3file.hasId3v2Tag()){
            id3v2 = mp3file.getId3v2Tag();
        } else {
            id3v2 = new ID3v24Tag();
            mp3file.setId3v2Tag(id3v2);
        }
        
        id3v2.clearAlbumImage();
        id3v2.setAlbumImage(bytes, "image/png");
        saveMp3(mp3file, songFileName);
    }
    
    public static void saveMp3(Mp3File mp3file, String songFileName) throws IOException, NotSupportedException{
        mp3file.save(TEMP_SONG);
        File song = new File(songFileName);
        song.delete();
        File temp = new File(TEMP_SONG);
        temp.renameTo(song);
    }
    
    public static Image resizeImageSmooth(final Image image, int width, int height) {
        ImageIcon resized = new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(resized.getImage(), 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}
