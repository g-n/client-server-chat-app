/**
 * This program is a rudimentary demonstration of Swing GUI programming.
 * Note, the default layout manager for JFrames is the border layout. This
 * enables us to position containers using the coordinates South and Center.
 *
 * Usage:
 *	java ChatScreen
 *
 * When the user enters text in the textfield, it is displayed backwards
 * in the display area.
 */

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class ChatScreen extends JFrame implements ActionListener, KeyListener
{
    final static DateFormat dtFormat = new SimpleDateFormat("hh:mm aa");
    JButton sendButton;
    JButton exitButton;
    JTextField sendText;
    JTextPane displayArea;
    JList<String> userList;
    User user;
    DefaultListModel<String> model;
    StyledDocument doc;

    SimpleAttributeSet dtAtt;
    SimpleAttributeSet userAtt;
    SimpleAttributeSet msgAtt;

    JScrollPane displayScrollPane;

    public ChatScreen(User user) {
        this.user = user;
//        this.br = br;
//        this.pw = pw;
        /**
         * a panel used for placing components
         */
        JPanel p = new JPanel();
//        p.setSize(600, 800);

        Border etched = BorderFactory.createEtchedBorder();
        Border titled = BorderFactory.createTitledBorder(etched, "Enter Message Here ...");
        p.setBorder(titled);

        /**
         * set up all the components
         */
        sendText = new JTextField(30);
        sendButton = new JButton("Send");
        exitButton = new JButton("Exit");

        /**
         * register the listeners for the different button clicks
         */
        sendText.addKeyListener(this);
        sendButton.addActionListener(this);
        exitButton.addActionListener(this);

        /**
         * add the components to the panel
         */
        p.add(sendText);
        p.add(sendButton);
        p.add(exitButton);

        /**
         * add the panel to the "south" end of the container
         */
        getContentPane().add(p,"South");

        /**
         * add the text area for displaying output. Associate
         * a scrollbar with this text area. Note we add the scrollpane
         * to the container, not the text area
         */

        JTextPane displayArea = new JTextPane();
        displayArea.setPreferredSize(new Dimension(800, 600));

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        doc = displayArea.getStyledDocument();

        displayArea.setCharacterAttributes(attributeSet, true);
        displayScrollPane = new JScrollPane(displayArea);
        displayScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(displayScrollPane,"Center");

        displayScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });


         model = new DefaultListModel<>();
        userList = new JList<>(model);
        userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setLayoutOrientation(JList.VERTICAL_WRAP);
        userList.setVisibleRowCount(-1);

//        userList.add();
        JScrollPane listScrollPane = new JScrollPane(userList);
        listScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(listScrollPane, "East");


        dtAtt = new SimpleAttributeSet();
        StyleConstants.setForeground(dtAtt, Color.GRAY);
//        StyleConstants.setBackground(dtAtt, Color.YELLOW);
        StyleConstants.setFontFamily(dtAtt, "SansSerif");
        StyleConstants.setFontSize(dtAtt, 14);
        StyleConstants.setBold(dtAtt, false);

        userAtt = new SimpleAttributeSet();
        StyleConstants.setForeground(userAtt, Color.BLACK);
        StyleConstants.setFontFamily(userAtt, "SansSerif");
        StyleConstants.setFontSize(userAtt, 14);
//        StyleConstants.setBackground(userAtt, Color.YELLOW);
        StyleConstants.setBold(userAtt, true);

        msgAtt = new SimpleAttributeSet();
        StyleConstants.setForeground(msgAtt, Color.BLACK);
//        StyleConstants.setBackground(msgAtt, Color.YELLOW);
        StyleConstants.setFontFamily(msgAtt, "SansSerif");
        StyleConstants.setFontSize(msgAtt, 14);
        StyleConstants.setBold(msgAtt, false);

        /**
         * set the title and size of the frame
         */
        setTitle("GUI Demo");
        pack();

        setVisible(true);
        sendText.requestFocus();

        /** anonymous inner class to handle window closing events */
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        } );

    }

    /**
     * adds a user to the userlist
     * @param ulist a list of strings containing the usernames
     */
    public void addUser(String[] ulist){
        ArrayList<String> arr = new ArrayList<String>(Arrays.asList(ulist));
        Collections.sort(arr);
        DefaultListModel<String> newList = new DefaultListModel<>();
        newList.addAll(arr);

        userList.setModel(newList);
        pack();

    }

    /**
     * This gets the text the user entered and outputs it
     * in the display area.
     */
    public void displayText() {
        String message = sendText.getText().trim();
        sendText.setText("");
        sendText.requestFocus();
    }
    /**
     * This gets the text the user entered and outputs it
     * in the display area.
     */
    void scrollToBottom(){
        JScrollBar vertical = displayScrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum()+10);
    }

    /**
     * adds a chat message to the window
     * @param un username
     * @param message message to be displayed
     */
    public void addMessage(String un, String message) {
        String dt = "["+dtFormat.format((new Date())) +"] ";
        String userf = un + ": ";
        String msgf = message + "\n";

        int start = doc.getLength();
        try{
            doc.insertString(start, dt, dtAtt);
            start = start + dt.length();
            doc.insertString(start, userf, userAtt);
            start = start + userf.length();
            doc.insertString(start, msgf, msgAtt);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds a server notification message to the chat streen
     * @param un username
     * @param message message to display
     */
    public void notifyMessage(String un, String message) {
        String dt = "["+dtFormat.format((new Date())) +"] ";
        String userf = un + " ";
        String msgf = message + "\n";

        int start = doc.getLength();
        try{
            doc.insertString(start, dt, dtAtt);
            start = start + dt.length();
            doc.insertString(start, userf, dtAtt);
            start = start + userf.length();
            doc.insertString(start, msgf, dtAtt);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void sendText(){
        String message = sendText.getText().trim();
        sendText.setText("");
        sendText.requestFocus();
        user.msg(message);
    }
    /**
     * This method responds to action events .... i.e. button clicks
     * and fulfills the contract of the ActionListener interface.
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == sendButton){
            sendText();
        }
        else if (source == exitButton){
            user.exit();
            System.exit(0);
        }

    }

    /**
     * These methods responds to keystroke events and fulfills
     * the contract of the KeyListener interface.
     */

    /**
     * This is invoked when the user presses
     * the ENTER key.
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
            sendText();
        }
    }

    /** Not implemented */
    public void keyReleased(KeyEvent e) { }

    /** Not implemented */
    public void keyTyped(KeyEvent e) {  }
}