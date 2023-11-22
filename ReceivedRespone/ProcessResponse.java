package ReceivedRespone;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import DTO.Dto;
import ShareData.ShareData;
import ShareData.VideoReceivedList;
import View.CallReceiver;
import View.CallSend;
import View.CallStream;

public class ProcessResponse extends Thread {
    private ReentrantLock lock;
    private Socket clientSocket;
    private ShareData shareData;
    private Dto receivedObject;
    public ProcessResponse(ReentrantLock lock, Socket clientSocket,ShareData shareData,Dto receivedObject){
        this.lock=lock;
        this.clientSocket=clientSocket;
        this.shareData=shareData;
        this.receivedObject=receivedObject;
    }
    public int checkUserVideoRoom(List<VideoReceivedList> listVideoReceivedLists,Long userID)
    {
        for(int i=0;i<listVideoReceivedLists.size();i++){
            if(listVideoReceivedLists.get(i).getUserID().equals(userID))
            {
                return i;
            }
        }
        return -1;
    }
    @Override
    public void run() {
        String cmd = receivedObject.getBody().get("command").toString();
        // System.out.println(receivedObject);
        if(cmd.equals("gridMessage")){
            lock.lock();
            shareData.addMessageReceivedList(receivedObject);
            // System.out.println(shareData.getMessageReceivedList().size());
            lock.unlock();                    
        }else if(cmd.equals("gridCall")){
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new CallReceiver(clientSocket,shareData,lock,receivedObject).setVisible(true);
                }
            });
        }else if(cmd.equals("CallProcess")){
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new CallStream(clientSocket,shareData,lock,receivedObject).setVisible(true);
                }
            });
        }else if(cmd.equals("authCall"))  {
            int x=checkUserVideoRoom(shareData.getVideoReceivedList(),Long.parseLong(receivedObject.getBody().get("from").toString()));
            if(x==-1){
                lock.lock();
                shareData.getVideoReceivedList().add(new VideoReceivedList(Long.parseLong(receivedObject.getBody().get("from").toString())));
                lock.unlock();
            }

        }else if(cmd.equals("streamVideo")){
            int x=checkUserVideoRoom(shareData.getVideoReceivedList(),Long.parseLong(receivedObject.getBody().get("from").toString()));
            if(x!=-1){             
                shareData.getVideoReceivedList().get(x).setImageReceived(receivedObject);
               
            }else{
                lock.lock();
                try{

                    shareData.getVideoReceivedList().add(new VideoReceivedList(Long.parseLong(receivedObject.getBody().get("from").toString()),receivedObject));
                }catch(Exception e){
                    System.out.println("úa");
                }
                lock.unlock();
            }
           
        }
    }
}
