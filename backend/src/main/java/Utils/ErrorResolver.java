package Utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;


public class ErrorResolver {

    public static void howToFix(String error) {
        try {
            String query = "https://chatgpt.com/?q="
                    + URLEncoder.encode(error + ". Giúp tôi sửa lỗi", "UTF-8");
            Desktop.getDesktop().browse(new URI(query));
        } catch (IOException | URISyntaxException e) {

        }
    }
}
