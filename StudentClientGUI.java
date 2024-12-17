package finalwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class StudentClientGUI extends JFrame {
    private JTextField hostField;
    private JTextField portField;
    private JButton connectButton;
    private JTextField nameField;
    private JButton signinButton;
    private JTextArea responseArea;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public StudentClientGUI() {
        setTitle("学生端");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 使用 GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 连接服务器面板
        JPanel connectionPanel = new JPanel(new GridLayout(3, 3, 20, 30));
        connectionPanel.add(new JLabel("服务器IP："));
        hostField = new JTextField("localhost");
        connectionPanel.add(hostField);
        connectionPanel.add(new JLabel("服务器端口："));
        portField = new JTextField("9999");
        connectionPanel.add(portField);

        // 签到面板
        JPanel signinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        signinPanel.add(new JLabel("姓名："));
        nameField = new JTextField(10);
        signinPanel.add(nameField);
        signinButton = new JButton("签到");
        signinPanel.add(signinButton);

        // 响应区域
        responseArea = new JTextArea();
        responseArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(responseArea);

        // 添加签到面板到中间
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5; // 占一半宽度
        gbc.fill = GridBagConstraints.BOTH;
        add(signinPanel, gbc);

        // 添加响应区域到右侧
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5; // 占一半宽度
        gbc.weighty = 0.2; // 占一半宽度
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // 添加连接面板到顶部
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1; // 占两列
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(connectionPanel, gbc);

        // 初始状态下，签到按钮不可用
        signinButton.setEnabled(false);

        // 连接按钮事件
        connectButton = new JButton("连接服务器");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(connectButton, gbc);
        connectButton.addActionListener(e -> connectToServer());

        // 签到按钮事件
        signinButton.addActionListener(e -> sendSignin());
    }

    private void connectToServer() {
        String host = hostField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            responseArea.append("连接服务器成功.\n");
            connectButton.setEnabled(false);
            signinButton.setEnabled(true);

            // 开启线程监听服务器响应
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        responseArea.append("服务器响应：" + line + "\n");
                    }
                } catch (IOException ex) {
                    responseArea.append("读取服务器数据时出错\n");
                }
            }).start();
        } catch (IOException e) {
            responseArea.append("连接服务器失败：" + e.getMessage() + "\n");
        }
    }

    private void sendSignin() {
        if (out != null) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                out.println("SIGNIN " + name);
            } else {
                responseArea.append("请先输入姓名。\n");
            }
        }
    }

}