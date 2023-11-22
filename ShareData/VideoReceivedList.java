package ShareData;

import DTO.Dto;
import lombok.Data;

@Data
public class VideoReceivedList {
    private Long userID;
    private Dto imageReceived;
    private Dto audioReceived;
    public VideoReceivedList(){
      
    }
    public VideoReceivedList(Long userID){
        this.userID=userID;
    }
    public VideoReceivedList(Long userID,Dto imageReceived){
        this.userID=userID;
        this.imageReceived=imageReceived;
    }
}
