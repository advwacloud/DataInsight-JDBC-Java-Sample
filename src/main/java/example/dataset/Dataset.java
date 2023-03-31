package example.dataset;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class Dataset {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) {
        // 1. 生成EIToken
        String username = "xxx@advantech.com.cn";
        String pwd = "xxx";
        String ssoUrl = "http://api-sso-xxx.dev003.wise-paas.com/v4.0/auth/native"; //{ssoUrl}/v4.0/auth/native
        String token = initToken(username, pwd, ssoUrl); //token 有效期是1个小时

        // 2. 创建 dataset
        String dataInsightUrl = "http://api-dis-dev-xxxx.dev003.wise-paas.com/api/v1/dataset"; //{dataInsightUrl}/api/v1/dataset
        String datasetName = "create_test";
        String groupId = "mg927Vj4Z7Xq";  //dataset 所在group的 groupId
        String description = "just test";
        String oriSql = "SELECT * FROM `main.cindy_pg.public.20W`";
        String type = "physical";  // 目前支持 physical, view
        createDataset(token, dataInsightUrl, datasetName, groupId, description, oriSql, type);

        // 3. 插入数据
        String hiveIp = "127.0.0.1";
        insertData(username, pwd, hiveIp);

    }


    private static String initToken(String username, String pwd, String url) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        JSONObject jsonResult = null;
        String result = "";
        try (CloseableHttpClient closeableHttpClient = httpClientBuilder.build()) {
            HttpPost post = new HttpPost(URLDecoder.decode(url, "UTF-8"));
            String body = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, pwd);
            HttpEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");
            HttpResponse resp = closeableHttpClient.execute(post);
            InputStream content = resp.getEntity().getContent();
            byte[] respBytes = IOUtils.toByteArray(content);
            result = new String(respBytes, StandardCharsets.UTF_8);
            jsonResult = JSONObject.parseObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonResult == null) {
            return "";
        }
        return jsonResult.getString("accessToken");

    }

    private static void createDataset(String token, String url, String datasetName, String groupId,
                                      String description, String oriSql, String type) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        String bodyTemplate = "{\n" +
                "  \"datasetName\": \"%s\",\n" +
                "  \"groupId\": \"%s\",\n" +
                "  \"schema\": {\n" +
                "    \"columns\": [\n" +
                "      {\n" +
                "        \"dataType\": \"%s\",\n" +
                "        \"length\": 64,\n" +
                "        \"name\": \"%s\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"type\": \"%s\"\n" +
                "}";
        String body = String.format(bodyTemplate, datasetName, description, groupId, oriSql, type);

        try (CloseableHttpClient closeableHttpClient = httpClientBuilder.build()) {
            HttpPost post = new HttpPost(URLDecoder.decode(url, "UTF-8"));
            post.setHeader("Content-type", "application/json");
            post.addHeader("Authorization", token);

            HttpEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
            post.setEntity(entity);

            HttpResponse resp = closeableHttpClient.execute(post);
            InputStream respIs = resp.getEntity().getContent();
            byte[] respBytes = IOUtils.toByteArray(respIs);
            System.out.println(new String(respBytes, StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertData(String username, String pwd, String ip) {
        try {
            Class.forName(driverName);
            // 127.0.0.1 需要替換成該環境DataInsight的JDBC外部接口地址, user和password則是SSO帳號密碼
            Connection con = DriverManager.getConnection(String.format("jdbc:hive2://%s:10090", ip), username, pwd);
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM `main.d1` LIMIT 10";
            boolean result = stmt.execute(sql);
            System.out.println(result);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
