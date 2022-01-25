import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import net.proteanit.sql.DbUtils;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class DashboardForm extends JFrame {
    private JPanel dashboardPanel;
    private JLabel userNameTxt;
    private JLabel balanceTxt;
    private JTable incomeTable;
    private JButton saveBtn;
    private JTextField nameTxt;
    private JTextField valueTxt;
    private JTable expensesTable;
    private JTextField dateTxt;
    private JButton deleteBtn;
    private JButton searchBtn;
    private JButton previousBtn;
    private JButton nextBtn;
    private JTextField idTxt;
    private JLabel dateHeaderTxt;
    private JLabel incomeTxt;
    private JLabel expensesTxt;
    private JComboBox typeBox;
    private JPanel header;
    private JPanel balance;


    int month, year, incomeBalance, expensesBalance, monthBalance;
    String monthStr, yearStr;

    public DashboardForm() {

        setTitle("Budget-App Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(1200, 680));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        boolean hasRegisteredUsers = connectToDatabase();


        String date = CurrentDateTime();
        yearStr = date.substring(0,4);
        year = Integer.parseInt(yearStr);
        char ch = getChar(date,5);
        String[] monthsTab = {"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

        if (ch == '0') {
            monthStr = date.substring(6,7);
        } else {
            monthStr = date.substring(5,7);

        }
        month = Integer.parseInt(monthStr);

        if (hasRegisteredUsers) {
            LoginForm loginForm = new LoginForm(this);
            User user = loginForm.user;

            if (user != null) {
                String userName = user.name;
                userNameTxt.setText("Hello " + userName + "! ");
                balanceTxt.setText("Balance: "+ incomeBalance);
                dateHeaderTxt.setText("Available Budget in " +  monthsTab[month] + " " + year);
                setLocationRelativeTo(null);
                setVisible(true);
            } else {
                dispose();
            }
        }
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name, value, date;
                int type;

                name = nameTxt.getText();
                type = typeBox.getSelectedIndex();
                value = valueTxt.getText();
                date = dateTxt.getText();
                System.out.println("Type: " + type);

                try {
                    if (type == 0) {
                        pst = con.prepareStatement("insert into actions (idUser, name, actionType, value, date) values (1, ?, 1, ?, ?)");
                        pst.setString(1, name);
                        pst.setString(2, value);
                        pst.setString(3, date);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Record Added!");
                        incomeLoad(month, year);
                        nameTxt.setText("");
                        valueTxt.setText("");
                        dateTxt.setText("");

                    } else {
                        pst = con.prepareStatement("insert into actions (idUser, name, actionType, value, date) values (1, ?, 2, ?, ?)");
                        pst.setString(1, name);
                        pst.setString(2, value);
                        pst.setString(3, date);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Record Added!");
                        expensesLoad(month, year);
                        nameTxt.setText("");
                        valueTxt.setText("");
                        dateTxt.setText("");
                    }

                } catch (SQLException er) {
                    er.printStackTrace();
                }
            }
        });

        incomeLoad(month, year);
        expensesLoad(month, year);
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id;
                id = idTxt.getText();

                try {
                    pst = con.prepareStatement("delete from actions where idAction = ?");
                    pst.setString(1, id);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Deleted!");
                    expensesLoad(month, year);
                    incomeLoad(month, year);
                    nameTxt.setText("");
                    valueTxt.setText("");
                    dateTxt.setText("");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String id = idTxt.getText();
                    pst = con.prepareStatement("select name, value, date from actions where idAction = ?");
                    pst.setString(1, id);
                    ResultSet rs = pst.executeQuery();

                    if(rs.next() == true) {
                        String name = rs.getString(1);
                        String value = rs.getString(2);
                        String date = rs.getString(3);
                        nameTxt.setText(name);
                        valueTxt.setText(value);
                        dateTxt.setText(date);


                    } else {
                        nameTxt.setText("");
                        valueTxt.setText("");
                        dateTxt.setText("");
                        JOptionPane.showMessageDialog(null,"Invalid data!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        previousBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (month > 1) {
                    month -= 1;
                } else {
                    year -= 1;
                    month = 12;
                }

                dateHeaderTxt.setText("Available Budget in " + monthsTab[month] + " " + year);
                incomeLoad(month, year);
                expensesLoad(month, year);
            }
        });


        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (month < 12) {
                    month += 1;
                } else {
                    year += 1;
                    month = 1;
                }

                dateHeaderTxt.setText("Available Budget in " + monthsTab[month] + " " + year);
                incomeLoad(month, year);
                expensesLoad(month, year);
            }
        });
    }

    public String CurrentDateTime () {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String dateToReturn = (dtf.format(now));
        return dateToReturn;
    }

    public static char getChar(String str, int index)
    {
        return str.charAt(index);
    }

    void incomeLoad(int date, int year) {
        try {
            pst = con.prepareStatement("select idAction, name, value, date from actions where actionType = 1 and month(date) =" + date + " and year(date) =" + year + "  order by date desc,value desc");
            ResultSet rs = pst.executeQuery();
            incomeTable.setModel(DbUtils.resultSetToTableModel(rs));
            pst = con.prepareStatement("select SUM(value) from actions where actionType = 1 and month(date) =" + date + " and year(date) =" + year);
            ResultSet rs2 = pst.executeQuery();
            rs2.next();
            incomeBalance = rs2.getInt(1);
            monthBalance = incomeBalance - expensesBalance;
            incomeTxt.setText("INCOME:  " + incomeBalance);
            balanceTxt.setText("Balance: "+ monthBalance);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void expensesLoad(int date, int year) {
        try {
            pst = con.prepareStatement("select idAction, name, value, date from actions where actionType = 2 and month(date) =" + date + " and year(date) =" + year + " order by date desc,value desc");
            ResultSet rs = pst.executeQuery();
            expensesTable.setModel(DbUtils.resultSetToTableModel(rs));
            pst = con.prepareStatement("select SUM(value) from actions where actionType = 2 and month(date) =" + date + " and year(date) =" + year);
            ResultSet rs2 = pst.executeQuery();
            rs2.next();
            expensesBalance = rs2.getInt(1);
            monthBalance = incomeBalance - expensesBalance;
            expensesTxt.setText("EXPENSES:  " + expensesBalance);
            balanceTxt.setText("Balance: "+ monthBalance);
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

            pst = con.prepareStatement("Create Table If Not Exists actions (" +
                    "idAction int NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "idUser int NOT NULL," +
                    "name varchar(45) NOT NULL," +
                    "actionType int NOT NULL," +
                    "value int NOT NULL," +
                    "date date NOT NULL," +
                    "FOREIGN KEY (idUser) REFERENCES users(id))");
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
