import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import net.proteanit.sql.DbUtils;

public class DashboardForm extends JFrame {
    private JPanel dashboardPanel;
    private JPanel header;
    private JLabel userNameTxt;
    private JLabel balanceTxt;
    private JTable incomeTable;
    private JButton saveBtn;
    private JTextField nameTxt;
    private JTextField categoryTxt;
    private JTextField valueTxt;
    private JTable expensesTable;
    private JTextField dateTxt;
    private JButton deleteBtn;
    private JButton searchBtn;
    private JPanel balance;
    private JButton button1;
    private JButton button2;

    public DashboardForm() {
        setTitle("Budget-App Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(1400, 760));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        int month = 1;

        boolean hasRegisteredUsers = connectToDatabase();

        if (hasRegisteredUsers) {
            LoginForm loginForm = new LoginForm(this);
            User user = loginForm.user;

            if (user != null) {
                String userName = user.name;
                userNameTxt.setText("Hello " + userName + "! ");
//                balanceTxt.setText("+ 4250.00");
                setLocationRelativeTo(null);
                setVisible(true);
            } else {
                dispose();
            }
        }
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name, category, value, date;

                name = nameTxt.getText();
                category = categoryTxt.getText();
                value = valueTxt.getText();
                date = dateTxt.getText();

                try {
                    if (category.equals("1") || category.equals("6")) {
                        pst = con.prepareStatement("insert into actions (idUser, name, actionType, value, idCategory, date) values (1, ?, 1, ?, ?, ?)");
                        pst.setString(1, name);
                        pst.setString(2, value);
                        pst.setString(3, category);
                        pst.setString(4, date);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Record Added!");
                        incomeLoad(month);
                        nameTxt.setText("");
                        categoryTxt.setText("");
                        valueTxt.setText("");
                        dateTxt.setText("");

                    } else {
                        pst = con.prepareStatement("insert into actions (idUser, name, actionType, value, idCategory, date) values (1, ?, 2, ?, ?, ?)");
                        pst.setString(1, name);
                        pst.setString(2, value);
                        pst.setString(3, category);
                        pst.setString(4, date);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Record Added!");
                        expensesLoad(month);
                        nameTxt.setText("");
                        categoryTxt.setText("");
                        valueTxt.setText("");
                        dateTxt.setText("");
                    }

                } catch (SQLException er) {
                    er.printStackTrace();
                }
            }
        });

        incomeLoad(month);
        expensesLoad(month);
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id;

            }
        });
    }

    void incomeLoad(int date) {
        try {
            pst = con.prepareStatement("select idAction, name, value, date from actions where actionType = 1 and month(date) =" + date + "  order by date,value desc");
            ResultSet rs = pst.executeQuery();
            incomeTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void expensesLoad(int date) {
        try {
            pst = con.prepareStatement("select idAction, name, value, date from actions where actionType = 2 and month(date) =" + date + " order by date,value desc");
            ResultSet rs = pst.executeQuery();
            expensesTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;

        connect();

        try {
            pst = con.prepareStatement("Create Database If Not Exists Budget");
            pst.executeUpdate();

            pst = con.prepareStatement("Create Table If Not Exists users (" +
                    "id int NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "login varchar(45) NOT NULL UNIQUE," +
                    "password varchar(45) NOT NULL," +
                    "name varchar(45) NOT NULL," +
                    "isAdmin tinyint DEFAULT 0)");
            pst.executeUpdate();

            pst = con.prepareStatement("Select count(*) from users");
            ResultSet resultSet = pst.executeQuery();

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegisteredUsers = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return hasRegisteredUsers;



    }

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

    public static void main(String[] args) {
        DashboardForm dashForm = new DashboardForm();
    }

}
