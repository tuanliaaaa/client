package View;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;

import DTO.Dto;
import ShareData.ShareData;
import View.CallStream.ShowYourCamera.ShowMyCamera;
public class CallStream extends JFrame {      
    private JPanel Controllpanel;
    private JPanel ListUserStreamPanel;
    private JPanel UserStreamAndSettingPanel;
    private JPanel Userpanel;
    private JButton jButton1;

    private JLabel jLabel5;
    
    private Webcam webcam;
    private Clip clip;
    private Socket clientSocket;
    private ShareData shareData;
    private ReentrantLock lock;
    private Dto receivedObject;
    private ImageIcon im;
    public CallStream(Socket clientSocket,ShareData shareData,ReentrantLock lock,Dto receivedObject) {
        this.clientSocket=clientSocket;
        this.shareData=shareData;
        this.lock=lock;
        this.receivedObject=receivedObject;
        playRingtone();
        initComponents();
        // jLabel1.setText("đang gọi hội thoại "+receivedObject.getBody().get("conservationID").toString());
        ShowMyCamera  showMyCamera= new ShowMyCamera();
        showMyCamera.start();
        ShowYourCamera  showYourCamera= new ShowYourCamera();
        showYourCamera.start();
    }
    public class ShowYourCamera extends Thread{
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                 if(shareData.getVideoReceivedList().size()!=0){    
                    clip.stop();    
                    ListUserStreamPanel.removeAll();           
                    for(int i=0;i<shareData.getVideoReceivedList().size();i++){
                       if(shareData.getVideoReceivedList().get(i).getImageReceived()!=null){
                            if(i>=ListUserStreamPanel.getComponents().length){
                                JPanel jPanel1 = new JPanel();
                                JLabel jLabel1 = new JLabel();
                                jPanel1.setLayout(new java.awt.BorderLayout());
                                jLabel1.setIcon((ImageIcon)shareData.getVideoReceivedList().get(i).getImageReceived().getBody().get("videoStream")); // NOI18N
                                jLabel1.setPreferredSize(new java.awt.Dimension(100, 100));
                                jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);
                                ListUserStreamPanel.add(jPanel1);                            
                               ListUserStreamPanel.revalidate();
                                ListUserStreamPanel.repaint();
                            }else{
                                 try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                System.out.println(shareData.getVideoReceivedList().get(i));                
                                JPanel jPanel1 = new JPanel();
                                JLabel jLabel1 = new JLabel();
                                jPanel1.setLayout(new java.awt.BorderLayout());
                                jLabel1.setIcon((ImageIcon)shareData.getVideoReceivedList().get(i).getImageReceived().getBody().get("videoStream")); // NOI18N
                                jLabel1.setPreferredSize(new java.awt.Dimension(100, 100));
                                jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);
                                ListUserStreamPanel.remove(i);
                                ListUserStreamPanel.add(jPanel1, i);                            
                                ListUserStreamPanel.revalidate();
                            ListUserStreamPanel.repaint();
                            }
                            // Cập nhật giao diện
                        }
                        ListUserStreamPanel.revalidate();
                        ListUserStreamPanel.repaint();
                    }
                 }else{
                     ListUserStreamPanel.removeAll();
                     JPanel jPanel1 = new JPanel();
                     JLabel jLabel1 = new JLabel();
                     jPanel1.setLayout(new java.awt.BorderLayout());
     
                     jLabel1.setIcon(new ImageIcon(getClass().getResource("yeu2.jpg"))); // NOI18N
                     jLabel1.setPreferredSize(new java.awt.Dimension(100, 100));
                     jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);
     
                     ListUserStreamPanel.add(jPanel1);
                     ListUserStreamPanel.revalidate();
                     ListUserStreamPanel.repaint();
                 }
                try {
                Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    public class sendVideo extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Dto dto2 = new Dto();
            Map<String, Object> header2 = new HashMap<>();
            header2.put("function", "streamVideo");
            header2.put("Token",shareData.getUserID());
            dto2.setHeader(header2);
            Map<String, Object> body2 = new HashMap<>();
            body2.put("command", "streamVideo");
            
            body2.put("videoStream",im);
            body2.put("conservationID", 1);
            dto2.setBody(body2);
            try{
                if(im!=null){
                    lock.lock();
                    ObjectOutputStream outToServer2 = new ObjectOutputStream(clientSocket.getOutputStream());
                    outToServer2.writeObject(dto2);
                    lock.unlock();
                }
            }catch(Exception e){
                System.out.println("lỗi");
                e.printStackTrace();
            }
        }
    }
    public class ShowMyCamera extends Thread {
        @Override
        public void run() {
            webcam= Webcam.getDefault();
            webcam.open();
            while (true) {
                Image bm= webcam.getImage();
                Image newimg = bm.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH);
                im= new ImageIcon(newimg);
                
                sendVideo sVideo= new sendVideo();
                sVideo.start();
                
                jLabel5.setIcon(im);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    private void stopRingtone() {
        // Dừng âm thanh nếu đang phát
        if (clip != null && clip.isRunning()) {
           
            clip.stop();
            clip.close();
        }
    }
    private void playRingtone() {
        try {
            // Load âm thanh từ tệp âm thanh (ví dụ: ringtone.wav)
            File audioFile = new File("C:\\Users\\nhatt\\Desktop\\Client\\View\\2.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Lấy đối tượng Clip để phát âm thanh
            this.clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Bắt đầu phát âm thanh
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    private void initComponents() {

        ListUserStreamPanel = new JPanel();
   
       
        UserStreamAndSettingPanel = new JPanel();
        Controllpanel = new JPanel();
        jButton1 = new JButton();
        Userpanel = new JPanel();
        jLabel5 = new JLabel();

      
        setPreferredSize(new java.awt.Dimension(450, 350));

        ListUserStreamPanel.setMinimumSize(new java.awt.Dimension(3317, 200));
        ListUserStreamPanel.setPreferredSize(new java.awt.Dimension(450, 200));
        ListUserStreamPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 1));

        
        getContentPane().add(ListUserStreamPanel, java.awt.BorderLayout.PAGE_START);

        UserStreamAndSettingPanel.setPreferredSize(new java.awt.Dimension(400, 100));
        UserStreamAndSettingPanel.setLayout(new java.awt.BorderLayout());

        Controllpanel.setPreferredSize(new java.awt.Dimension(300, 100));
        Controllpanel.setLayout(new BoxLayout(Controllpanel, BoxLayout.LINE_AXIS));

        jButton1.setText("Tắt");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        Controllpanel.add(jButton1);

        UserStreamAndSettingPanel.add(Controllpanel, java.awt.BorderLayout.CENTER);

        Userpanel.setLayout(new java.awt.BorderLayout());

        jLabel5.setIcon(new ImageIcon(getClass().getResource("yeu2.jpg"))); // NOI18N
        Userpanel.add(jLabel5, java.awt.BorderLayout.CENTER);

        UserStreamAndSettingPanel.add(Userpanel, java.awt.BorderLayout.EAST);

        getContentPane().add(UserStreamAndSettingPanel, java.awt.BorderLayout.CENTER);

        pack();
    }                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        
    }                                                    
}
