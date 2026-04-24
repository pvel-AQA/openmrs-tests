package api.requests.skeleton.interfaces;

import api.models.BaseModel;

import java.util.Map;

public interface CrudEndpointInterface {
    Object get(String uuid, Class<?> clazz);
    Object getWithParams(Map<String, Object> params, Class<?> clazz);
    Object post(BaseModel model);
    Object post(BaseModel model, String uuid);
    Object delete(long uuid);
}
