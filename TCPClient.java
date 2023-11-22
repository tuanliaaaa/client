import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.SwingUtilities;

import DTO.Dto;
import ReceivedRespone.ReceivedRespone;
import ShareData.ShareData;
import View.Chat;
import login.Login;
import lombok.extern.java.Log;

public class TCPClient{
    
  
    public static void main(String[] args) {
        try{
            Socket clientSocket= new Socket("localhost",2207);
            ReentrantLock lock = new ReentrantLock();
            Properties properties = new Properties();
            FileInputStream fileInputStream = null;
            try {
                String filePath = "app.properties";
                fileInputStream = new FileInputStream(filePath);
                properties.load(fileInputStream);
                String userID = properties.getProperty("Token");
                if(userID==null){
                    new Login(clientSocket,lock).setVisible(true);
                }else{
                    
                    ShareData shareData= new ShareData();
                    shareData.setUserID(Long.parseLong(userID));
                    //Tham gia Conservations
                    Dto dto = new Dto();
                    Map<String, Object> header = new HashMap<>();
                    header.put("function", "conservations");
                    header.put("Token", shareData.getUserID());
                    dto.setHeader(header);
                    Map<String, Object> body = new HashMap<>();
                    dto.setBody(body);
                    
                    ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
                    outToServer.writeObject(dto);
                    ObjectInputStream inFromClient0 = new ObjectInputStream(clientSocket.getInputStream());
                    Dto receivedObject = (Dto) inFromClient0.readObject();
                    // System.out.println(receivedObject);
                    if(receivedObject.getBody().get("status").equals(200)){
                        
                        lock.lock();
                        shareData.setCheckOnline(true);
                        lock.unlock();
                        new Chat(clientSocket,shareData,lock).setVisible(true);
                       
                        ReceivedRespone receivedRespone = new ReceivedRespone(lock, clientSocket,shareData);
                        receivedRespone.start();
                    }
                }
            } catch (IOException e) {
                System.out.println("out ở tcp");
                // e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch(Exception e){
            System.out.println("NotConnect to Server");
            // e.printStackTrace();
        }
    }
}