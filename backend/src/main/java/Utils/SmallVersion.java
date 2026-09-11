package Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class SmallVersion {

    public static void main(String[] args) {
        int maxSmallVersion = 0;
        File directory = new File("data/icon/x4");
        if (directory.isDirectory()) {
            Optional<File> maxFile = Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith(".png"))
                    .max(Comparator.comparingInt(file -> {
                        String name = file.getName();
                        return Integer.valueOf(name.substring(0, name.length() - 4));
                    }));
            if (maxFile.isPresent()) {
                String fileName = maxFile.get().getName();
                maxSmallVersion = Short.parseShort(fileName.substring(0, fileName.length() - 4)) + 1;
                System.out.println("maxSmallVersion: " + maxSmallVersion);
            }
        }

        for (int i = 1; i < 5; i++) {
            try {
                String filePath = "data/smallimage_version/x" + i + "/smallimage_version_data";
                Path path = Paths.get(filePath);
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(path.toFile()))) {
                    dos.writeShort(maxSmallVersion);
                    for (int j = 0; j < maxSmallVersion; j++) {
                        byte[] data = FileIO.readFile("data/icon/x" + i + "/" + j + ".png");
                        byte len = -1;
                        if (data != null) {
                            len = (byte) (data.length % 127);
                        }
                        dos.writeByte(len);
                    }
                }
                System.out.println(i);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

}




