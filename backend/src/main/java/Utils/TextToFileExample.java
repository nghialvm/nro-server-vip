package Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextToFileExample {

    public static void main(String[] args) {
        String inputFilePath = "D:/tap_tin.txt"; // Đường dẫn đến tệp văn bản đầu vào
        String outputFilePath = "D:/test.dat"; // Đường dẫn đến tệp dữ liệu nhị phân đầu ra

        try {
            convertTextFileToBinaryFile(inputFilePath, outputFilePath);
            System.out.println("Đã chuyển đổi và lưu tệp văn bản thành tệp nhị phân: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertTextFileToBinaryFile(String inputFilePath, String outputFilePath) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(inputFilePath);
             FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}