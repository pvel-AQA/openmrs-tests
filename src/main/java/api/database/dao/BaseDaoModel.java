package api.database.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseDaoModel<T> {
    public abstract T mapRow(ResultSet rs) throws SQLException;
}
