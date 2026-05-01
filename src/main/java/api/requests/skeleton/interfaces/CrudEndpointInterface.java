package api.requests.skeleton.interfaces;

import api.models.BaseModel;

import java.util.Map;

public interface CrudEndpointInterface {
    Object get(String uuid, Class<?> clazz);
    Object getAll(String uuid, Class<?> clazz);
    Object getAll(Map<String, Object> params, Class<?> clazz);
    Object post(BaseModel model);
    Object post(BaseModel model, String uuid);
    void delete(String uuid);
    void delete(String uuid, Boolean purge);
    Response postRaw(BaseModel model);
}
