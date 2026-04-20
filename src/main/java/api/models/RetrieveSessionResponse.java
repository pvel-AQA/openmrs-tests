package api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveSessionResponse extends BaseModel{
    boolean authenticated;
    String locale;
    List<String> allowedLocales;
    User user;
    String sessionLocation;
    CurrentProvider currentProvider;
}
