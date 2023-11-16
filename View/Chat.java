package View;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import DTO.Dto;
import ShareData.ShareData;

import javax.swing.*;
public class Chat extends JFrame {
    private JPanel chatPanel;
    private JPanel conversationPanel;


    private Socket clientSocket;
    private ShareData shareData;
    private ReentrantLock lock;
    private ArrayList<String> receivedMessages = new ArrayList<>();   
    private int conservationON;
    private List<Map> conservationList;
    public Chat(Socket clienSocket, ShareData shareData,ReentrantLock lock) {
        this.clientSocket=clienSocket;
        this.shareData=shareData;
        this.lock=lock;
        conservationList = new ArrayList<>();
        try {                      
            // lấy toàn bộ đoạn chat
            Dto dto3 = new Dto();
            Map<String, Object> header3 = new HashMap<>();
            header3.put("Token",shareData.getUserID());
            dto3.setHeader(header3);
            Map<String, Object> body3 = new HashMap<>();
            body3.put("command", "getConservation");
            body3.put("conservationID", 1);
            dto3.setBody(body3);
            ObjectOutputStream outToServer3 = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer3.writeObject(dto3);
            ObjectInputStream inFromClienxt = new ObjectInputStream(clientSocket.getInputStream());
            Dto receivedObject = (Dto) inFromClienxt.readObject();
            // System.out.println(receivedObject);
            if(receivedObject.getBody().get("command").equals("getConservations")){
                List<Map<String,Object>>  conservationResList=(List) receivedObject.getBody().get("content");
               
                for( Map<String,Object> conservationRes:conservationResList){
                    conservationRes.put("chatFrame",conservationRes.get("listMessage"));
                    conservationRes.put("conservationFrame",conservationRes.get("conversationName"));
                    conservationRes.put("conservationID",conservationRes.get("conversationID"));
                    conservationList.add(conservationRes);
                }
                
                // System.out.println(receivedObject.getBody().get("content"));
            }
            
            initComponents();
            ReceiveMessage r= new ReceiveMessage();
            r.start();
        }
        catch(Exception e){
            System.out.println("chat khưởi tao");
            // e.printStackTrace();
        }
        // startSending();
        // checkOnline = 1;

    }
   
    public class ReceiveMessage extends Thread {
        @Override
        public void run() {
            while (true) {
                if(shareData.getMessageStatus()==1){
                    
                    Dto messageReceived = shareData.getMessageReceived();
                    if(messageReceived.getBody().get("command").equals("gridMessage")){
                        Long conservationID =Long.parseLong(messageReceived.getBody().get("conservationID").toString());
                        for(int i=0;i<conservationList.size();i++){
                            if(Long.parseLong(conservationList.get(i).get("conservationID").toString())==conservationID)
                            {
                                
                                ((JTextArea) conservationList.get(i).get("JTextAreaE")).append(messageReceived.getBody().get("from").toString()+": "+messageReceived.getBody().get("content").toString() +"\n");
                            }
                        }
                    }else if(messageReceived.getBody().get("command").equals("chấp nhận cuộc gọi")){
    
                    }
                       
                    lock.lock();
                    shareData.setMessageStatus(0);
                    lock.unlock();

                }
                lock.notify();
            }
                 
        }
    }
   
    // public void sendMessageOff(MessageClient me) {
    //     while(true){
    //         try {
    //             // Gửi Lần 2
    //             Dto dto2 = new Dto();
    //             Map<String, Object> header2 = new HashMap<>();
    //             header2.put("Token", userID);
    //             dto2.setHeader(header2);
    //             Map<String, Object> body2 = new HashMap<>();
    //             body2.put("command", "Message");
    //             body2.put("conservationID", 1);
    //             body2.put("content", me.getMessage());
    //             dto2.setBody(body2);
    //             ObjectOutputStream outToServer2 = new ObjectOutputStream(clientSocket.getOutputStream());
    //             outToServer2.writeObject(dto2);
    
    
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }
                  
    // private void startSending() {
    //     Runnable sendTask = new Runnable() {
    //         @Override
    //         public void run() {
    //             while (true ) {
    //                 try {
    //                     wait();
    //                 } catch (InterruptedException e) {
                        
    //                 }
    //                 try{
    //                 Properties properties = new Properties();
    //                 try{
    //                     String path = "Client/app.properties";
    //                     FileInputStream in = new FileInputStream(path);
    //                     properties.load(in);                               
    //                 } catch (IOException e) {
    //                     e.printStackTrace();
    //                     System.out.println("đọc file lỗi");
    //                 }
    //                 try {
    //                     String objectListString = properties.getProperty("objectList");
    //                     JSONParser parser = new JSONParser();
    //                     JSONArray jsonArray = (JSONArray) parser.parse(objectListString);
    //                     List<MessageClient> messageList = new ArrayList<>();
    //                     for (Object obj : jsonArray) {
    //                         JSONObject jsonObject = (JSONObject) obj;
    //                         String message = (String) jsonObject.get("message");
    //                         Long id = (Long) jsonObject.get("id");
    //                         messageList.add(new MessageClient(message, id.intValue()));
    //                     }
    //                     if(messageList.size()!=0){
    //                         //Khởi tạo Socket mới để kết nối
    //                         Socket client = new Socket("localhost", 2207);
    //                         Dto dto = new Dto();
    //                         Map<String, Object> header = new HashMap<>();
    //                         header.put("function", "conservations");
    //                         header.put("Token",userID);
    //                         dto.setHeader(header);
    //                         Map<String, Object> body = new HashMap<>();
    //                         dto.setBody(body);
    //                         ObjectOutputStream outToServer = new ObjectOutputStream(client.getOutputStream());
    //                         outToServer.writeObject(dto);
    //                         //Nhận về lần 1
    //                         ObjectInputStream inFromClient0 = new ObjectInputStream(client.getInputStream());
    //                         Dto receivedObject0 = (Dto) inFromClient0.readObject();
    //                         System.out.println(receivedObject0);
    //                         clientSocket=client;
    //                         List<MessageClient>  newmMessageClients = messageList;
    //                         for (int i=0;i<messageList.size();i++) {
    //                             sendMessageOff(messageList.get(i));
    //                             newmMessageClients.remove(i);   
    //                         }
    //                         System.out.println("vcl");
    //                         JSONArray jsonArraxy = new JSONArray();
    //                         for (MessageClient x : newmMessageClients) {
    //                             // Tạo một đối tượng JSONObject cho mỗi đối tượng Message
    //                             JSONObject jsonObject = new JSONObject();
    //                             jsonObject.put("message", x.getMessage());
    //                             jsonObject.put("id", x.getId());
    //                             jsonArraxy.add(jsonObject);
    //                         }
    //                         String jsonString = jsonArraxy.toJSONString();
    //                         properties.setProperty("objectList", jsonString);
    //                         FileOutputStream out = new FileOutputStream("Client/app.properties");
    //                         properties.store(out, "Updated properties");
    //                         out.close();
    //                     }
    //                 } catch (Exception e) {
    //                     e.printStackTrace();
    //                 }
    //             }catch(Exception e){

    //             }
    //             notifyAll();
    //             }
    //         }
    //     };

    //     // Bắt đầu luồng để gửi yêu cầu
    //     Thread sendingThread = new Thread(sendTask);
    //     // sendingThread.start();
    // }

       
   
    @SuppressWarnings("unchecked")                      
    private void initComponents() {

        conversationPanel = new JPanel();
      
        chatPanel = new JPanel();
   

        for(int i=0;i<conservationList.size();i++)
        {
            JPanel conversationx = new JPanel();
            conservationList.get(i).put("conversationE",conversationx);
            JLabel jLabelx = new JLabel();
            conservationList.get(i).put("jLabelE",jLabelx);
            JPanel chatx = new JPanel();
            conservationList.get(i).put("chatE",chatx);
            JScrollPane jScrollPanex = new JScrollPane();
            conservationList.get(i).put("jScrollPaneE",jScrollPanex);
            JPanel textAriaPanex = new JPanel();
            conservationList.get(i).put("textAriaPaneE",textAriaPanex);
            JTextArea jTextAreax = new JTextArea();
            conservationList.get(i).put("JTextAreaE", jTextAreax);
        
            
            JButton sendx = new JButton();
            conservationList.get(i).put("sendE", sendx);
            JTextField sendMessagex = new JTextField();
            conservationList.get(i).put("sendMessageE", sendMessagex);
            JButton callx = new JButton();
            conservationList.get(i).put("callE", callx);
            JPanel textAriaPanelx = new JPanel();
            conservationList.get(i).put("textAriaPanelE", textAriaPanelx);
            JPanel eventAriaPanelx = new JPanel();
            conservationList.get(i).put("eventAriaPanelE", eventAriaPanelx);
        }
       


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        conversationPanel.setPreferredSize(new java.awt.Dimension(120, 221));
        conversationPanel.setLayout(new BoxLayout(conversationPanel, BoxLayout.PAGE_AXIS));
        conservationON= Integer.parseInt(conservationList.get(0).get("conversationID").toString());
        for(int i=0;i<conservationList.size();i++)
        {
            final int index = i;
            ((JPanel) conservationList.get(i).get("conversationE")).addMouseListener(new java.awt.event.MouseAdapter() {
               
                public void mouseClicked(java.awt.event.MouseEvent evt) {

                    CardLayout cardLayout = (CardLayout) chatPanel.getLayout();
                    
                    cardLayout.show(chatPanel, "card"+index);
                    conservationON=index+1;
                }
            });
            ((JPanel) conservationList.get(i).get("conversationE")).setLayout(new BoxLayout((JPanel) conservationList.get(i).get("conversationE"), BoxLayout.LINE_AXIS));

            ((JLabel) conservationList.get(i).get("jLabelE")).setText((String) conservationList.get(i).get("conversationName"));
           
            ((JPanel) conservationList.get(i).get("conversationE")).add((JLabel) conservationList.get(i).get("jLabelE"));

            conversationPanel.add((JPanel) conservationList.get(i).get("conversationE"));
        }



        

        getContentPane().add(conversationPanel, java.awt.BorderLayout.WEST);

        chatPanel.setLayout(new java.awt.CardLayout());




        for(int i=0;i<conservationList.size();i++)
        {
            ((JPanel) conservationList.get(i).get("chatE")).setLayout(new java.awt.BorderLayout());

            ((JPanel) conservationList.get(i).get("textAriaPanelE")).setPreferredSize(new java.awt.Dimension(509, 200));
            ((JPanel) conservationList.get(i).get("textAriaPanelE")).setLayout(new java.awt.BorderLayout());
            ((JTextArea) conservationList.get(i).get("JTextAreaE")).setColumns(20);
            ((JTextArea) conservationList.get(i).get("JTextAreaE")).setRows(5);
            List lmess=(List) conservationList.get(i).get("listMessage");
             
            for(int j=0;j<lmess.size();j++){
                String h=(String) ((Map) lmess.get(j)).get("message");
                Long g=Long.parseLong((String)((Map) lmess.get(j)).get("userID"));
                ((JTextArea) conservationList.get(i).get("JTextAreaE")).append(g+": "+h +"\n");
            }
            ((JScrollPane) conservationList.get(i).get("jScrollPaneE")).setViewportView((JTextArea) conservationList.get(i).get("JTextAreaE"));

            ((JPanel) conservationList.get(i).get("textAriaPanelE")).add((JScrollPane) conservationList.get(i).get("jScrollPaneE"), java.awt.BorderLayout.CENTER);

            ((JPanel) conservationList.get(i).get("chatE")).add((JPanel) conservationList.get(i).get("textAriaPanelE"), java.awt.BorderLayout.PAGE_START);

            ((JPanel) conservationList.get(i).get("eventAriaPanelE")).setPreferredSize(new java.awt.Dimension(653, 100));
            ((JPanel) conservationList.get(i).get("eventAriaPanelE")).setLayout(new BoxLayout((JPanel) conservationList.get(i).get("eventAriaPanelE"), BoxLayout.LINE_AXIS));

            ((JButton) conservationList.get(i).get("sendE")).setText("Send");
            ((JButton) conservationList.get(i).get("sendE")).addActionListener(new java.awt.event.ActionListener() {
               
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    
                    sendMessage();
                }
            });
            ((JPanel) conservationList.get(i).get("eventAriaPanelE")).add((JButton) conservationList.get(i).get("sendE"));

            ((JTextField) conservationList.get(i).get("sendMessageE")).setPreferredSize(new java.awt.Dimension(64, 100));
            ((JTextField) conservationList.get(i).get("sendMessageE")).addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    // conservationList.get(i).get("sendMessageE")ActionPerformed(evt);
                }
            });
            ((JPanel) conservationList.get(i).get("eventAriaPanelE")).add((JTextField) conservationList.get(i).get("sendMessageE"));

            ((JButton) conservationList.get(i).get("callE")).setText("Call");
            ((JButton) conservationList.get(i).get("callE")).addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    // call();
                }
            });
            ((JPanel) conservationList.get(i).get("eventAriaPanelE")).add((JButton) conservationList.get(i).get("callE"));

            ((JPanel) conservationList.get(i).get("chatE")).add((JPanel) conservationList.get(i).get("eventAriaPanelE"), java.awt.BorderLayout.CENTER);

            chatPanel.add((JPanel) conservationList.get(i).get("chatE"), "card"+i);
        }
        getContentPane().add(chatPanel, java.awt.BorderLayout.CENTER);

        pack();
    }            
    // public void call(){
    //     Dto dto2 = new Dto();
    //     Map<String, Object> header2 = new HashMap<>();
    //     header2.put("function", "conservations");
    //     header2.put("Token",userID);
    //     dto2.setHeader(header2);
    //     Map<String, Object> body2 = new HashMap<>();
    //     body2.put("command", "callVideo");
    //     body2.put("conservationID", conservationON);
    //     dto2.setBody(body2);
    //     try{
    //         ObjectOutputStream outToServer2 = new ObjectOutputStream(clientSocket.getOutputStream());
    //         outToServer2.writeObject(dto2);
    //     }catch(Exception e){

    //     }
    // }                    
    
    private void sendMessage() {
        try{
            Dto dto5 = new Dto();
            Map<String, Object> header5 = new HashMap<>();
            header5.put("function", "conservations");
            header5.put("Token",shareData.getUserID());
            dto5.setHeader(header5);
            Map<String, Object> body5 = new HashMap<>();
            body5.put("command", "Message");
            body5.put("conservationID",conservationON);
            JTextField q=(JTextField)conservationList.get(conservationON-1).get("sendMessageE");
            
            body5.put("content",q.getText());
            dto5.setBody(body5);
            ObjectOutputStream outToServer5 = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer5.writeObject(dto5);
            q.setText("");
        }catch(Exception e){
            System.out.println("đã out");
            // e.printStackTrace();
        }
    }
           
}
