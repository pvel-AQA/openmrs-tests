package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    String uuid;
    String display;
    String username;
    String systemId;
    UserProperties userProperties;
    Person person;
    List<Object> privileges;
    List<Role> roles;
    List<Link> links;
    String resourceVersion;
}
