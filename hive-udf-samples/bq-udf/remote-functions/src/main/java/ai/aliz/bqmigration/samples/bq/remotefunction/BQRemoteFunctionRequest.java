package ai.aliz.bqmigration.samples.bq.remotefunction;

import java.util.List;
import java.util.Map;
import com.google.gson.JsonElement;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BQRemoteFunctionRequest {

    private final String requestId;
    private final String caller;
    private final String sessionUser;
    private final Map<String, String> userDefinedContext;
    private final List<List<JsonElement>> calls;
}
