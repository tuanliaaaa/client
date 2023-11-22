package ShareData;
import java.util.ArrayList;
import java.util.List;

import DTO.Dto;
import lombok.Data;

@Data
public class ShareData {
    private int Mode;
    private Long userID;
    private List<Dto> messageReceivedList;
    private Dto videoReceived;
    private boolean video;
    private boolean checkOnline;
    private List<VideoReceivedList> videoReceivedList;
    public ShareData(){
        this.video=false;
        this.messageReceivedList= new ArrayList<>();
        this.videoReceivedList = new ArrayList<>();
        this.videoReceivedList = new ArrayList<>();
    }
    public void addMessageReceivedList(Dto newMessage){
        this.messageReceivedList.add(newMessage);   
    }
    // public void addVideoReceivedList(Dto newVideo){
    //     this.videoReceivedList.add(newVideo);
    // }
}
