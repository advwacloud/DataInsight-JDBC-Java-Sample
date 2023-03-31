package example.hive;

import java.sql.*;

public class Hive {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        try {
            Class.forName(driverName);
            // 127.0.0.1 需要替换成DataInsight的JDBC外部接口地址, user和password是SSO账户密码
            // 带默认数据库url示例: jdbc:hive2://127.0.0.1:10090/database。
            con = DriverManager.getConnection(
                    "jdbc:hive2://127.0.0.1:10090", "xxx@advantech.com.tw", "1234");
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM `main.d1` LIMIT 10";
            ResultSet res = stmt.executeQuery(sql);
            while (res.next()) {
                System.out.println(res.getString(1) + "\t" + res.getString(2));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
}
