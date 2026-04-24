package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)

public class VisitTypeListResponse extends BaseModel{

    private String uuid;
    private String display;
    private String name;
    private String description;
    private boolean retired;
    private AuditInfo auditInfo;
    private List<Link> links;
    private String resourceVersion;
}
