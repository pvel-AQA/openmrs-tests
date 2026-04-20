package api.requests;

import api.models.BaseModel;
import api.models.RetrieveSessionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    SESSION(
            "/session",
            BaseModel.class,
            RetrieveSessionResponse.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

}
