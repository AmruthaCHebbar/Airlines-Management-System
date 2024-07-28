import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class SwingApp extends JFrame implements ActionListener {
    private JLabel label1, label2, label3;
    private JTextField field1, field2, field3;
    private JButton button1, button2, displayButton;
    private Connection con;
    private Statement stmt;

    public SwingApp() {
        super("Swing Application");
        setLayout(new FlowLayout());

        label1 = new JLabel("Enter first attribute:");
        field1 = new JTextField(10);

        label2 = new JLabel("Enter second attribute:");
        field2 = new JTextField(10);

        label3 = new JLabel("Result:");
        field3 = new JTextField(10);
        field3.setEditable(false);

        button1 = new JButton("Capture");
        button1.addActionListener(this);

        button2 = new JButton("Addition");
        button2.addActionListener(this);

        displayButton = new JButton("Display Result");
        displayButton.addActionListener(this);

        add(label1);
        add(field1);
        add(label2);
        add(field2);
        add(label3);
        add(field3);
        add(button1);
        add(button2);
        add(displayButton);

        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Connect to the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/myDB", "root", "amithudayshinde@1234");
            stmt = con.createStatement();
        } catch (Exception e) {
            handleException(e);
        }

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();

        if (command.equals("Capture")) {
            try {
                int a = Integer.parseInt(field1.getText());
                int b = Integer.parseInt(field2.getText());

                // Using prepared statement to prevent SQL injection
                PreparedStatement pstmt = con.prepareStatement("INSERT INTO attributes (attribute1, attribute2) VALUES (?, ?)");
                pstmt.setInt(1, a);
                pstmt.setInt(2, b);

                int i = pstmt.executeUpdate();

                if (i > 0) {
                    JOptionPane.showMessageDialog(this, "Data saved successfully");
                }
            } catch (Exception e) {
                handleException(e);
            }
        }

        if (command.equals("Addition")) {
            try {
                int a = Integer.parseInt(field1.getText());
                int b = Integer.parseInt(field2.getText());

                int c = a + b;

                PreparedStatement pstmt = con.prepareStatement("INSERT INTO results (result) VALUES (?)");
                pstmt.setInt(1, c);

                int i = pstmt.executeUpdate();

                if (i > 0) {
                    field3.setText(String.valueOf(c));
                }
            } catch (Exception e) {
                handleException(e);
            }
        }

        if (command.equals("Display Result")) {
            displayResult();
        }
    }

    private void handleException(Exception e) {
        JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    private void displayResult() {
        try {
            String query = "SELECT result FROM results ORDER BY id DESC LIMIT 1";
            ResultSet resultSet = stmt.executeQuery(query);

            if (resultSet.next()) {
                int result = resultSet.getInt("result");
                field3.setText(String.valueOf(result));
            } else {
                JOptionPane.showMessageDialog(this, "No results found", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SwingApp();
            }
        });
    }
}
