package ShareData;
import DTO.Dto;
import lombok.Data;

@Data
public class ShareData {
    private int Mode;
    private Long userID;
    private Dto messageReceived;
    private Dto videoReceived;
    private int messageStatus;
    private boolean video;
    private boolean checkOnline;
    public ShareData(){
        this.messageStatus=0;
        this.video=false;
    }
}
