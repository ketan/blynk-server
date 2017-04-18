package cc.blynk.integration.https;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class UploadAPITest extends APIBaseTest {

    @Test
    public void uploadFileToServer() throws Exception {
        login(admin.email, admin.pass);

        upload("logo.png");
    }

    private void upload(String filename) throws Exception {
        InputStream logoStream = UploadAPITest.class.getResourceAsStream("/" + filename);

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/upload");
        ContentBody fileBody = new InputStreamBody(logoStream, ContentType.APPLICATION_OCTET_STREAM, filename);
        StringBody stringBody1 = new StringBody("Message 1", ContentType.MULTIPART_FORM_DATA);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        builder.addPart("text1", stringBody1);
        HttpEntity entity = builder.build();

        post.setEntity(entity);
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String staticPath = consumeText(response);

            assertNotNull(staticPath);
            assertTrue(staticPath.startsWith("/static"));
            assertTrue(staticPath.endsWith("png"));
            assertTrue(staticPath.contains("_" + filename));
        }
    }

    @Test
    public void upload2FilesToServer() throws Exception {
        login(admin.email, admin.pass);

        upload("logo.png");
        upload("logo.png");
        upload("logo.png");
        upload("logo.png");
        upload("logo.png");
    }

}
