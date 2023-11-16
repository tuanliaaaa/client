package login;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;
import DTO.Dto;
import ReceivedRespone.ReceivedRespone;
import ShareData.ShareData;
import View.Chat;

public class Login extends javax.swing.JFrame {
    private Socket clientSocket;
    private ReentrantLock lock;
    private ShareData shareData;

    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JTextField jTextField1;
    public Login(Socket clientSocket,ReentrantLock lock) {
        this.clientSocket=clientSocket;
        this.lock=lock;
        this.shareData= new ShareData();
        initComponents();
    }

    private boolean authenticate(String username, String password) {
        //Khởi tạo đối tượng để Login
        Dto dto = new Dto();
        Map<String, Object> header = new HashMap<>();
        header.put("function", "Login");
        dto.setHeader(header);
        Map<String, Object> body = new HashMap<>();
        body.put("username", "nhi");
        body.put("password", "nhi");
        dto.setBody(body);

        //Gửi đối tượng đến server
        try{

            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer.writeObject(dto);
        }catch(Exception e){
            e.printStackTrace();
             return false;
        }

        //Đọc đối tượng từ server
        try {
            ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());       
            Dto receivedObject = (Dto) inFromClient.readObject();
            
            System.out.println(receivedObject.getBody().get("status").toString());
            if (receivedObject.getBody().get("status").toString().equals("200")) {
                String UserID = receivedObject.getBody().get("userID").toString();
                saveUserID(UserID);

                return true;
            } else{
                return false;
        
            }
        } catch (Exception e) {
            return false;
        }
    }    
        
    private void saveUserID(String userID) {
        Properties properties = new Properties();
        String path = "app.properties";
        try (FileInputStream in = new FileInputStream(path)) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Thiết lập giá trị cho thuộc tính "Token"
        properties.setProperty("Token", userID);
        try (FileOutputStream out = new FileOutputStream(path)) {
            properties.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

  
    @SuppressWarnings("unchecked")
   
    private void initComponents() {

        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPasswordField1.setText("jPasswordField1");

        jLabel1.setText("Tên đăng nhập");

        jLabel2.setText("Mật khẩu");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setText("Đăng nhập");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1))
                                .addGap(61, 61, 61)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jButton1)
                                        .addComponent(jPasswordField1)
                                        .addComponent(jTextField1))
                                .addContainerGap(127, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(86, 86, 86)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                .addGap(29, 29, 29)
                                .addComponent(jButton1)
                                .addContainerGap(100, Short.MAX_VALUE)));

        pack();
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String username = jTextField1.getText();
        String password = new String(jPasswordField1.getPassword());

        if (authenticate(username, password)) {
            JOptionPane.showMessageDialog(null, "Đăng nhập thành công!");
            this.dispose(); 
            try {
                clientSocket= new Socket("localhost",2207);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Tham gia Conservations
            Dto dto = new Dto();
            Map<String, Object> header = new HashMap<>();
            header.put("function", "conservations");
            header.put("Token", shareData.getUserID());
            dto.setHeader(header);
            Map<String, Object> body = new HashMap<>();
            dto.setBody(body);
            
            try {
                ObjectOutputStream outToServer;
                outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
                outToServer.writeObject(dto);
                ObjectInputStream inFromClient0 = new ObjectInputStream(clientSocket.getInputStream());
                Dto receivedObject = (Dto) inFromClient0.readObject();
                
                if(receivedObject.getBody().get("status").equals(200)){
                    // java.awt.EventQueue.invokeLater(() -> {
                    // new Chat().setVisible(true);
                    // });
                    ReceivedRespone receivedRespone = new ReceivedRespone(lock, clientSocket,shareData);
                    receivedRespone.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
          
        } else {
            JOptionPane.showMessageDialog(null, "Đăng nhập thất bại. Vui lòng thử lại.");
        } 
    }

   
}
