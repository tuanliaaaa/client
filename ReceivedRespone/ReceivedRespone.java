package ReceivedRespone;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import DTO.Dto;
import ShareData.ShareData;

public class ReceivedRespone extends Thread{
    private ReentrantLock lock;
    private Socket clientSocket;
    private ShareData shareData;
    public ReceivedRespone(ReentrantLock lock, Socket clientSocket,ShareData shareData){
        this.lock=lock;
        this.clientSocket=clientSocket;
        this.shareData=shareData;
    }
    @Override
    public void run() {
        while (true) {
            
            try {
                // System.out.println("đã bắt đầu quá tình nhận");
                ObjectInputStream inFromClient0 = new ObjectInputStream(clientSocket.getInputStream());
                Dto receivedObject = (Dto) inFromClient0.readObject();
                String cmd = receivedObject.getBody().get("command").toString();
                System.out.println(receivedObject);
                if(cmd.equals("gridMessage")){
                    Runnable messageReceived = new Runnable() {
                        @Override
                        public void run() {
                            
                            try {
                                // lock.wait();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            lock.lock();
                                shareData.setMessageStatus(1);
                                shareData.setMessageReceived(receivedObject);
                                // Thông báo cho các luồng khác rằng đã có thay đổi
                            lock.unlock();
                            
                        }
                    };
                    
                    Thread myThread = new Thread(messageReceived);
                    myThread.start();
                }
            } catch (Exception e) {
                System.out.println("lỗi nhận liên tuck");
                e.printStackTrace();
            }
        }
    }
}
