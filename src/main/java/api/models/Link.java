package api.models;

import lombok.Data;

@Data
public class Link {
    private String rel;
    private String uri;
    private String resourceAlias;
}
