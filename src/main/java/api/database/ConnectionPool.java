package api.database;

import api.configs.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static volatile HikariDataSource dataSource;

    private ConnectionPool() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (ConnectionPool.class) {
                if (dataSource == null) {
                    dataSource = createDataSource();
                }
            }
        }
        return dataSource;
    }

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(Config.getProperty("db.url"));
        config.setUsername(Config.getProperty("db.username"));
        config.setPassword(Config.getProperty("db.password"));
        config.setDriverClassName(Config.getProperty("db.driver"));
        // Пример: org.postgresql.Driver/ org.mariadb.jdbc.Driver

        // Размер пула = количество параллельных потоков + запас
        // Для JUnit5 parallel execution с 8 потоками:
        config.setMaximumPoolSize(10);
        // Максимум 10 соединений одновременно
        // Если 11-й поток запросит соединение - будет ждать

        config.setMinimumIdle(5);
        // 5 соединений всегда готовы (даже если нет нагрузки)
        // Ускоряет первые запросы после простоя

        // === НАСТРОЙКИ ТАЙМАУТОВ ===
        config.setConnectionTimeout(30000);
        // 30 секунд - максимальное время ожидания соединения из пула
        // Если за 30 сек соединение не получено - исключение
        // Важно: в тестах лучше быстро падать, чем висеть

        config.setIdleTimeout(600000);
        // 10 минут - время бездействия, после которого соединение закрывается
        // Освобождает ресурсы на сервере БД

        config.setMaxLifetime(1800000);
        // 30 минут - максимальное время жизни соединения
        // После этого соединение будет заменено новым
        // Предотвращает проблемы с таймаутами на стороне БД

        config.setLeakDetectionThreshold(60000);
        // 60 секунд - если соединение не возвращено в пул за это время
        // Будет выведено предупреждение об утечке
        // КРИТИЧНО для отладки многопоточных проблем!

        // === НАСТРОЙКИ ПРОИЗВОДИТЕЛЬНОСТИ ===
        config.addDataSourceProperty("cachePrepStmts", "true");
        // Кешировать PreparedStatement на стороне БД

        config.addDataSourceProperty("prepStmtCacheSize", "250");
        // Размер кеша для PreparedStatement

        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        // Максимальная длина SQL для кеширования

        // === ТЕСТОВЫЕ НАСТРОЙКИ ===
        config.setConnectionTestQuery("SELECT 1");
        // Проверка живости соединения перед выдачей из пула

        config.setValidationTimeout(5000);
        // 5 секунд на проверку соединения

        return new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
        // connection.close() будет возвращать соединение в пул, а не закрывать его!
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
    }
}
