package finalwork;
import javax.swing.*;
import java.io.IOException;
public class app {
    public static void main(String[] args) {
        // 启动服务器线程
        new Thread(() -> {
            try {
                Server server = new Server(9999);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 启动教师端 GUI
        SwingUtilities.invokeLater(() -> {
            TeacherClientGUI frame = new TeacherClientGUI();
            frame.setVisible(true);
        });
        // 启动学生端 GUI
        SwingUtilities.invokeLater(() -> {
            StudentClientGUI frame = new StudentClientGUI();
            frame.setVisible(true);
        });
    }
}
