package finalwork;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private boolean signInOpen = false;  // 是否开启签到
    private Set<String> signedInStudents = ConcurrentHashMap.newKeySet(); // 已签到学生集合
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器启动，监听端口：" + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }
    class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)) {

                String line;
                while ((line = in.readLine()) != null) {
                    String response = handleCommand(line);
                    if (response != null) {
                        out.println(response);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized String handleCommand(String command) {
        if (command.equalsIgnoreCase("OPEN")) {
            signInOpen = true;
            return "签到已开启";
        } else if (command.equalsIgnoreCase("CLOSE")) {
            signInOpen = false;
            return "签到已关闭";
        } else if (command.equalsIgnoreCase("LIST")) {
            return "已签到学生：" + String.join(", ", signedInStudents);
        } else if (command.startsWith("SIGNIN")) {
            String[] parts = command.split("\\s+", 2);
            if (parts.length < 2) {
                return "签到指令格式错误，请使用：SIGNIN <姓名>";
            }
            if (!signInOpen) {
                return "当前未开放签到，无法签到。";
            }
            String studentName = parts[1].trim();
            if (signedInStudents.contains(studentName)) {
                return studentName + " 已经签到过。";
            } else {
                signedInStudents.add(studentName);
                return studentName + " 签到成功。";
            }
        } else {
            return "未知指令：" + command;
        }
    }

}

