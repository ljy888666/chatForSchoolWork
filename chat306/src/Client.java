import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client extends JFrame {

    private JTextField textFieldNickname;
    private JTextArea textAreaInput;
    private JTextArea textAreaOutput;
    private JButton buttonConnect;
    private JButton buttonSend;
    private Socket socket;
    private PrintWriter out;
    private String nickname;

    public Client() {
        initComponents();
        initStyles();
    }

    private void initComponents() {
        setTitle("306聊天室");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        textFieldNickname = new JTextField("先输入姓名再连接", 20);
        JLabel labelNickname = new JLabel("昵称:");

        JPanel nicknamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nicknamePanel.add(labelNickname);
        nicknamePanel.add(textFieldNickname);

        textAreaInput = new JTextArea();
        textAreaOutput = new JTextArea();
        textAreaOutput.setEditable(false);
        JScrollPane scrollPaneInput = new JScrollPane(textAreaInput);
        JScrollPane scrollPaneOutput = new JScrollPane(textAreaOutput);

        buttonConnect = new JButton("连接");
        buttonSend = new JButton("发送");

        buttonConnect.addActionListener(e -> {
            nickname = textFieldNickname.getText().trim();
            connectToServer();
        });

        buttonSend.addActionListener(e -> sendText());
        buttonSend.setEnabled(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        topPanel.add(nicknamePanel);
        topPanel.add(buttonConnect);

        inputPanel.add(scrollPaneInput, BorderLayout.CENTER);
        buttonPanel.add(buttonSend);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPaneOutput, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void initStyles() {
        // 设置全局字体
        UIManager.put("Button.font", new Font("微软雅黑", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("微软雅黑", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("微软雅黑", Font.PLAIN, 14));

        // 设置文本区域样式
        textAreaInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textAreaOutput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // 设置按钮样式
        buttonConnect.setBackground(new Color(200, 200, 255));
        buttonSend.setBackground(new Color(200, 200, 255));
        buttonConnect.setOpaque(true);
        buttonSend.setOpaque(true);

        // 添加鼠标悬停效果
        for (AbstractButton button : new AbstractButton[]{buttonConnect, buttonSend}) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(150, 150, 200));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(200, 200, 255));
                }
            });
        }
    }



    private void connectToServer() {
        try {
            String serverAddress = "10.188.49.182";
            int port = 8000;

            socket = new Socket(serverAddress, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(socket.getOutputStream(), true);

            buttonConnect.setEnabled(false);
            buttonSend.setEnabled(true);

            Thread readThread = new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        textAreaOutput.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "连接失败，请检查服务器是否在线！");
        }
    }

    private void sendText() {
        String message = textAreaInput.getText();
        out.println(nickname + ":" + message);
        textAreaOutput.append(nickname + ":" + message + "\n"); // 添加在此处，以便在聊天窗口显示发送的消息
        textAreaInput.setText("");
    }

    // 连接服务器和发送消息的方法不变...

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}