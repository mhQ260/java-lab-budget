import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class LoginForm  extends JDialog {
    private JTextField loginTxt;
    private JPasswordField passwordTxt;
    private JButton logBtn;
    private JPanel loginPanel;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Budget-App Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(600, 400));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        logBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginTxt.getText();
                String password = String.valueOf(passwordTxt.getPassword());

                user = getAuthUser(login, password);

                if (user != null) {
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Login or password invalid!",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    public User user;

    Connection con;
    PreparedStatement pst;

    public void connect() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/budget?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "zaq1@WSX");
            System.out.println("Success!");

        }  catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private User getAuthUser(String login, String password) {
        connect();
        User user = null;

        try {
            pst = con.prepareStatement("Select * From users Where login=? And password =?");
            pst.setString(1, login);
            pst.setString(2, password);
            ResultSet resultSet = pst.executeQuery();


            if (resultSet.next()) {
                user = new User();
                user.name = resultSet.getString("name");
                user.login = resultSet.getString("login");
                user.password = resultSet.getString("password");
                user.isAdmin = resultSet.getBoolean("isAdmin");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static void main (String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;
        if (user != null) {
            System.out.println("Successful authentication for " + user.name);
        } else {
            System.out.println("Authentication failed");
        }
    }
}
