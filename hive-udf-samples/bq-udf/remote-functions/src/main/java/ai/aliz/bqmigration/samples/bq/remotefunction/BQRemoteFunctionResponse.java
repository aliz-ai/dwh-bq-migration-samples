package ai.aliz.bqmigration.samples.bq.remotefunction;

import java.util.List;

import com.google.gson.JsonElement;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString 
public class BQRemoteFunctionResponse {

    private final List<JsonElement> replies;
    private final String errorMessage;

    private BQRemoteFunctionResponse(List<JsonElement> replies, String errorMessage) {
        this.replies = replies;
        this.errorMessage = errorMessage;
    }

    public static BQRemoteFunctionResponse success(List<JsonElement> replies) {
        return new BQRemoteFunctionResponse(replies, null);
    }

    public static BQRemoteFunctionResponse error(String errorMessage) {
        return new BQRemoteFunctionResponse(null, errorMessage);
    }
}
