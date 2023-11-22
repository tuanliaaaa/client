package ReceivedRespone;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import DTO.Dto;
import ShareData.ShareData;

public class ReceivedRespone extends Thread{
    private ReentrantLock lock;
    private Socket clientSocket;
    private ShareData shareData;
    private ObjectInputStream x;
    public ReceivedRespone(ReentrantLock lock, Socket clientSocket,ShareData shareData){
        this.lock=lock;
        this.clientSocket=clientSocket;
        this.shareData=shareData;
        
    }
    @Override
    public void run() {
        while (true) {
            // System.out.println("đã bắt đầu quá tình nhận");
            ObjectInputStream inFromClient0;
            try {
                inFromClient0 = new ObjectInputStream(clientSocket.getInputStream());
                Dto receivedObject = (Dto) inFromClient0.readObject();
                x=inFromClient0;
                ProcessResponse processResponse = new ProcessResponse(lock, clientSocket, shareData,receivedObject);
                processResponse.start();
            } catch (Exception e) {
                System.out.println("hhh");
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
           
        }
    }
}
