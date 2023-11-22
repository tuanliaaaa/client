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

import DTO.Dto;
import ShareData.ShareData;

public class CallReceiver extends javax.swing.JFrame {
    private Clip clip;
    private Socket clientSocket;
    private ShareData shareData;
    private ReentrantLock lock;
    private Dto receivedObject;
    public CallReceiver(Socket clientSocket,ShareData shareData,ReentrantLock lock,Dto receivedObject) {
        this.clientSocket=clientSocket;
        this.shareData=shareData;
        this.lock=lock;
        this.receivedObject=receivedObject;
        playRingtone();
        initComponents();
        jLabel1.setText(receivedObject.getBody().get("from").toString()+" đang gọi");
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
            File audioFile = new File("C:\\Users\\nhatt\\Desktop\\Client\\View\\1.wav");
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setPreferredSize(new java.awt.Dimension(400, 200));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setPreferredSize(new java.awt.Dimension(200, 274));

        jButton1.setLabel("Nghe");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("không nghe");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(64, 64, 64))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(218, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addGap(33, 33, 33))
        );

        jPanel3.add(jPanel6);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("");
        jPanel5.add(jLabel1);

        jPanel4.add(jPanel5);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        Dto dto3 = new Dto();
        Map<String, Object> header3 = new HashMap<>();
        header3.put("function", "conservations");
        header3.put("Token",shareData.getUserID());
        dto3.setHeader(header3);
        Map<String, Object> body3 = new HashMap<>();
        body3.put("command", "authCallVideo");
        body3.put("conservationID", 1);
        body3.put("statusAuthCall", "chấp nhận cuộc gọi");
        dto3.setBody(body3);
        
        try {
            lock.lock();
            ObjectOutputStream outToServer3 = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer3.writeObject(dto3);
            lock.unlock();
            this.dispose(); 
            CallStream callStream = new CallStream(clientSocket, shareData, lock, receivedObject);
            callStream.setVisible(true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }                                        

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       Dto dto3 = new Dto();
        Map<String, Object> header3 = new HashMap<>();
        header3.put("function", "conservations");
        header3.put("Token",shareData.getUserID());
        dto3.setHeader(header3);
        Map<String, Object> body3 = new HashMap<>();
        body3.put("command", "authCallVideo");
        body3.put("conservationID", 1);
        body3.put("statusAuthCall", "don't accept");
        dto3.setBody(body3);
        
        try {
            lock.lock();
            ObjectOutputStream outToServer3 = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer3.writeObject(dto3);
            lock.unlock();
            this.dispose(); 
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }                                        


                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;                 
}
