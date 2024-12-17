package finalwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class TeacherClientGUI extends JFrame {
    private JTextField hostField;
    private JTextField portField;
    private JButton connectButton;
    private JButton openButton;
    private JButton closeButton;
    private JButton listButton;
    private JTextArea responseArea;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public TeacherClientGUI() {
        setTitle("教师端");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 使用 GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 连接服务器面板
        JPanel topPanel = new JPanel(new GridLayout(3, 3, 20, 30));
        topPanel.add(new JLabel("服务器IP："));
        hostField = new JTextField("localhost");
        topPanel.add(hostField);
        topPanel.add(new JLabel("服务器端口："));
        portField = new JTextField("9999");
        topPanel.add(portField);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel();
        openButton = new JButton("开启签到");
        closeButton = new JButton("关闭签到");
        listButton = new JButton("查看已签到列表");
        buttonPanel.add(openButton);
        buttonPanel.add(closeButton);
        buttonPanel.add(listButton);

        // 响应区域
        responseArea = new JTextArea();
        responseArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(responseArea);

        // 添加连接面板到顶部
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // 占两列
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(topPanel, gbc);

        // 添加操作按钮面板到中间
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5; // 占一半宽度
        gbc.fill = GridBagConstraints.BOTH;
        add(buttonPanel, gbc);

        // 添加响应区域到右侧
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5; // 占一半宽度
        gbc.weighty = 0.2; // 占一半宽度
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // 初始化按钮状态
        openButton.setEnabled(false);
        closeButton.setEnabled(false);
        listButton.setEnabled(false);

        // 连接按钮事件
        connectButton = new JButton("连接服务器");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(connectButton, gbc);
        connectButton.addActionListener(e -> connectToServer());

        // 教师端操作按钮事件
        openButton.addActionListener(e -> sendCommand("OPEN"));
        closeButton.addActionListener(e -> sendCommand("CLOSE"));
        listButton.addActionListener(e -> sendCommand("LIST"));
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
            // 启用操作按钮
            openButton.setEnabled(true);
            closeButton.setEnabled(true);
            listButton.setEnabled(true);

            // 开启读取线程实时接收服务器数据
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

    private void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }
}