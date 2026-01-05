import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseSeeder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        // Load SQL từ file
        String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/database/seeder/seed_users.sql")));

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Seed dữ liệu thành công");
        } catch (Exception e) {
            System.err.println("❌ Lỗi seed dữ liệu: " + e.getMessage());
        }
    }
}
