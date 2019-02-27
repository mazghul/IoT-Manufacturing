package wilp.bits.iotmanufacturing.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventResponse {

    @SerializedName("response")
    private List<Manu> response = new ArrayList<>();

    public List<Manu> getResponse() {
        return response;
    }

    public void setResponse(List<Manu> response) {
        this.response = response;
    }
}
