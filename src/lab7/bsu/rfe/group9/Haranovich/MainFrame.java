package lab7.bsu.rfe.group9.Haranovich;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {
    private static final String FRAME_TITLE = "������ ���������� ���������";

    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;

    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;

    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;

    private static final int SERVER_PORT = 4567;
    //127.0.0.1
    public MainFrame() {
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));

       
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);

      
        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);

       
        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);

       
        final JLabel labelFrom = new JLabel("��");
        final JLabel labelTo = new JLabel("����������");

       
        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);

       
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);

        
        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);

        
        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("���������"));

        
        final JButton sendButton = new JButton("���������");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        
        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout2.createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom)
                        .addComponent(labelTo)
                        .addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap());

  
        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);
        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());
        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(
                                socket.getInputStream());
                        
                        final String senderName = in.readUTF();
                      
                        final String message = in.readUTF();
                        
                        socket.close();
                        
                        final String address = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().getHostAddress();
                        
                        textAreaIncoming.append(senderName + " (" + address + "): " + message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "������ � ������ �������", "������", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }

    private void sendMessage() {
        try {
          
            final String senderName = textFieldFrom.getText();
            final String destinationAddress = textFieldTo.getText();
            final String message = textAreaOutgoing.getText();
          
            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "������� ��� �����������", "������", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (destinationAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "������� ����� ����-����������", "������", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "������� ����� ���������", "������", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int iter = 0;
            String regex = "\\d+";
            for (String destAddress : destinationAddress.split("\\.")) {
                if(!destAddress.matches(regex)){
                    JOptionPane.showMessageDialog(this, "������ ������������ ����� ����-����������", "������", JOptionPane.ERROR_MESSAGE);
                    textFieldTo.requestFocus();
                    return;
                }
                iter++;
                int i = Integer.parseInt(destAddress);
                if( i < 0 || i > 128){
                    JOptionPane.showMessageDialog(this, "������ ������������ ����� ����-����������", "������", JOptionPane.ERROR_MESSAGE);
                    textFieldTo.requestFocus();
                    return;
                }
            }
            if(iter != 4){
                JOptionPane.showMessageDialog(this, "������ ������������ ����� ����-����������", "������", JOptionPane.ERROR_MESSAGE);
                textFieldTo.requestFocus();
                return;
            }


          
            final Socket socket = new Socket(destinationAddress, SERVER_PORT);
           
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            out.writeUTF(senderName);
            
            out.writeUTF(message);
           
            socket.close();
            
            //textAreaIncoming.append("� -> " + destinationAddress + ": " + message + "\n");
            
            textAreaOutgoing.setText("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this,
                    "�� ������� ��������� ���������: ����-������� �� ������", "������", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "�� ������� ��������� ���������", "������", JOptionPane.ERROR_MESSAGE);
        }
    }
}