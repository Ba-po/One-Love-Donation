package OOP;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;



class Donation {
    String name;
    String phone_number;
    String necessities;
    String location;

    Donation(String name, String phone_number, String necessities, String location) {
        this.name = name;
        this.phone_number = phone_number;
        this.necessities = necessities;
        this.location = location;
    }
}

class DonationManager {
    private List<Donation> donations;

    DonationManager() {
        donations = new ArrayList<>();
    }

    void addDonation(String name, String phone_number, String necessities, String location) {
        Donation donation = new Donation(name, phone_number, necessities, location);
        donations.add(donation);
        JOptionPane.showMessageDialog(null, "Information added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    boolean removeDonation(String name) {
        try (Connection connection = DriverManager.getConnection(Project.JDBC_URL, Project.DB_USER, Project.DB_PASSWORD)) {
            String deleteQuery = "DELETE FROM donations WHERE name = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, name);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    void displayDonation(JPanel donationListPanel) {
        try (Connection connection = DriverManager.getConnection(Project.JDBC_URL, Project.DB_USER, Project.DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM donations";

            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(selectQuery)) {
                    if (!resultSet.isBeforeFirst()) {
                        JOptionPane.showMessageDialog(null, "No recipients found in the database.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String phoneNumber = resultSet.getString("phone_number");
                        String necessities = resultSet.getString("necessities");
                        String location = resultSet.getString("location");

                        JLabel donationLabel = new JLabel();
                        donationLabel.setText(
                        		"<html>" +
                                "Name: " + name + "<br>" +
                                "Phone Number: " + phoneNumber + "<br>" +
                                "Recipient Necessities: " + necessities + "<br>" +
                                "Location: " + location + "<br><br>"
                        );
                        donationLabel.setForeground(new Color(0xfff4e6));
                        donationLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
                        donationListPanel.add(donationLabel);
                    }

                    donationListPanel.revalidate();
                    donationListPanel.repaint();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    void searchDonation(String name, JPanel searchListPanel) {
        try (Connection connection = DriverManager.getConnection(Project.JDBC_URL, Project.DB_USER, Project.DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM donations WHERE name LIKE ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, "%" + name + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    boolean found = false;

                    while (resultSet.next()) {
                        found = true;
                       
                        JLabel donationLabel = new JLabel();
                        donationLabel.setHorizontalAlignment(JLabel.CENTER);
                        donationLabel.setText(
                        		"<html>" +
                                "Name: " + resultSet.getString("name") + "<br>" +
                                        "Phone Number: " + resultSet.getString("phone_number") + "<br>" +
                                        "Recipient Necessities: " + resultSet.getString("necessities") + "<br>" +
                                        "Location: " + resultSet.getString("location") + "<br><br>"
                        );
                  
                        donationLabel.setForeground(new Color(0x4b3832));
                        donationLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
                        searchListPanel.add(donationLabel);
                    }

                    searchListPanel.revalidate();
                    searchListPanel.repaint();

                    if (!found) {
                      
                        JLabel noResultLabel = new JLabel("Recipients not found.");
                        noResultLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
                        searchListPanel.add(noResultLabel);
                        searchListPanel.revalidate();
                        searchListPanel.repaint();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    void updateDonation(String name, String phone_number, String necessities, String location) {
        try (Connection connection = DriverManager.getConnection(Project.JDBC_URL, Project.DB_USER, Project.DB_PASSWORD)) {
            String updateQuery = "UPDATE donations SET phone_number = ?, necessities = ?, location = ? WHERE name = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, phone_number);
                preparedStatement.setString(2, necessities);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, name);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Recipients not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Donation donation : donations) {
            if (donation.name.equals(name)) {
                donation.phone_number = phone_number;
                donation.necessities = necessities;
                donation.location = location;
                return;
            }
        }
    }
}

public class Project {

    static final String JDBC_URL = "jdbc:mysql://localhost:3306/DBProj";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "";

    static DonationManager donationManager = new DonationManager();

    public static void main(String[] args) {


    	 SwingUtilities.invokeLater(() -> {
    	        try {
    	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
    	            e.printStackTrace();
    	        }
            showLogin();
        });
    }

            public static void showLogin() {
            	Border loginBorder2 = BorderFactory.createLineBorder(new Color(0x987c62), 3);
                JFrame loginFrame = new JFrame("Login");
                loginFrame.setSize(800,500);
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginFrame.setLayout(null);
                loginFrame.setResizable(false);
                    
        	    ImageIcon originalIcon = new ImageIcon("donation.png");
                Image originalImage = originalIcon.getImage();
                Image resizedImage = originalImage.getScaledInstance(784, 461, Image.SCALE_SMOOTH);
                ImageIcon resizedIcon = new ImageIcon(resizedImage);

                JLabel icon = new JLabel();
                icon.setIcon(resizedIcon);
                icon.setBounds(0, 0, 784, 461);
                
                JTextField usernameField = new JTextField();
        	    usernameField.setPreferredSize(new Dimension(150,30));
        	    usernameField.setBounds(200, 273, 200, 25);
        	    usernameField.setBackground(Color.WHITE);
        	    usernameField.setBorder(BorderFactory.createCompoundBorder());
        	    usernameField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        	    usernameField.setForeground(new Color(0x8c6135));
        	    
                JPasswordField passwordField = new JPasswordField();
                passwordField.setPreferredSize(new Dimension(150,30));
                passwordField.setBounds(200, 313, 200, 25);
                passwordField.setBackground(Color.WHITE);
                passwordField.setBorder(BorderFactory.createCompoundBorder());
                passwordField.setForeground(new Color(0x8c6135));
                passwordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
                
                JLabel line1 = new JLabel();
        	    line1.setForeground(new Color(0x8c6135));
        	    line1.setBounds(200, 243, 295, 100);
        	    line1.setText("__________________________________");
        	    
        	    JLabel line2 = new JLabel();
        	    line2.setForeground(new Color(0x8c6135));
        	    line2.setBounds(200, 283, 295, 100);
        	    line2.setText("__________________________________");
        	    
                JButton loginButton = new JButton("Login");
                loginButton.setBounds(170, 350, 100, 30);
        	    loginButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        	    loginButton.setBackground(new Color(0xfff4e6));
        	    loginButton.setBorder(BorderFactory.createCompoundBorder());
        	    loginButton.setForeground(new Color(0x8c6135));
        	    loginButton.setContentAreaFilled(false);
        	    loginButton.setOpaque(true);

                JButton adminButton = new JButton("Admin Login");
                adminButton.setBounds(300, 350, 100, 30);;
        	    adminButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        	    adminButton.setBackground(new Color(0xfff4e6));
        	    adminButton.setBorder(BorderFactory.createCompoundBorder());
        	    adminButton.setForeground(new Color(0x8c6135));
        	    adminButton.setContentAreaFilled(false);
        	    adminButton.setOpaque(true);
                
                JLabel labeluser = new JLabel();
        	    labeluser.setText("Username: ");
        	    labeluser.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        	    labeluser.setBounds(120, 250, 200, 75);
        	    labeluser.setForeground(new Color(0x8c6135));
        	    
        	    JLabel labelpass = new JLabel();
        	    labelpass.setText("Password: ");
        	    labelpass.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        	    labelpass.setBounds(120, 290, 250, 75);
        	    labelpass.setForeground(new Color(0x8c6135));
        	    
                JPanel loginPanel = new JPanel();
                loginPanel.setBackground(new Color(0xbe9b7b));
                loginPanel.setBounds(0, 0, 784, 461);
                loginPanel.setLayout(null);
                loginPanel.add(labeluser);
                loginPanel.add(usernameField);
                loginPanel.add(labelpass);
                loginPanel.add(passwordField);
                loginPanel.add(loginButton);
                loginPanel.add(adminButton);
                loginFrame.add(loginPanel);
                loginPanel.setBorder(loginBorder2);
                loginPanel.add(line1);
                loginPanel.add(line2);
                loginPanel.add(icon);
                
                MouseListener buttonHoverListener = new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        JButton button = (JButton) e.getSource();
                        button.setBackground(new Color(0xd9c3b3)); // Darken the background color
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        JButton button = (JButton) e.getSource();
                        button.setBackground(new Color(0xfff4e6)); // Restore the original background color
                    }
                };

                loginButton.addMouseListener(buttonHoverListener);
                adminButton.addMouseListener(buttonHoverListener);


                loginButton.addActionListener(e -> {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    if (isValidLogin(username, password)) {
                        loginFrame.dispose();
                        showUserApp(username);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
     
            adminButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (isValidAdminLogin(username, password)) {
                    loginFrame.dispose();
                    showAdminApp(username);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid admin username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            loginFrame.setVisible(true);
        };
    



    private static boolean isValidLogin(String username, String password) {
        return "username".equals(username) && "userpass".equals(password);
    }
    
    private static boolean isValidAdminLogin(String username, String password) {
        return "admin".equals(username) && "adminpass".equals(password);
    }
    
    private static void showUserApp(String username) {
    	
    	ImageIcon originalIcon = new ImageIcon("composition-still-life-friendship-day-elements.jpg");
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        JLabel icon = new JLabel();
        icon.setIcon(resizedIcon);
        icon.setBounds(0, 0, 500, 500);
    	
    	JPanel panel1 = new JPanel();
	    panel1.setBackground(new Color(0x854442));
	    panel1.setBounds(0, 0, 1000, 100);
	    panel1.setLayout(null);
	    
	    JPanel panel2 = new JPanel();
	    panel2.setBackground(new Color(0xbe9b7b));
	    panel2.setBounds(0, 100, 1000, 1000);
	    panel2.setLayout(null);
	    
	    JLabel welcome = new JLabel();
	    welcome.setText("Welcome, " + username + "!");
        welcome.setBounds(100, 40, 300, 20);
        welcome.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
        welcome.setForeground(Color.WHITE);
        
	    JLabel adddona = new JLabel();
	    adddona.setText("Add Donation Recipients");
	    adddona.setFont(new Font("Comic Sans MS Bold", Font.BOLD, 30));
	    adddona.setBounds(75, 5, 400, 75);
	    adddona.setForeground(Color.WHITE);
	    
	    JLabel Name = new JLabel();
	    Name.setText("Name: ");
	    Name.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
	    Name.setBounds(104, 60, 200, 75);
	    Name.setForeground(Color.WHITE);

	    JTextField nameF = new JTextField();
	    nameF.setPreferredSize(new Dimension(150,30));
	    nameF.setBounds(180, 86, 250, 25);
	    nameF.setBackground(new Color(0xfff4e6));
	    nameF.setBorder(BorderFactory.createCompoundBorder());
	    nameF.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
	    nameF.setForeground(new Color(0x854442));
	   
	    JLabel pnum = new JLabel();
	    pnum.setText("Phone Number: ");
	    pnum.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
	    pnum.setBounds(15, 120, 250, 75);
	    pnum.setForeground(Color.WHITE);
	    
	    JLabel location = new JLabel();
	    location.setText("Location: ");
	    location.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
	    location.setBounds(80, 260, 250, 75);
	    location.setForeground(Color.WHITE);
	    
	    JTextField locationF = new JTextField();
	    locationF.setPreferredSize(new Dimension(150,30));
	    locationF.setBounds(180, 285, 250, 25);
	    locationF.setBackground(new Color(0xfff4e6));
	    locationF.setBorder(BorderFactory.createCompoundBorder());
	    locationF.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
	    locationF.setForeground(new Color(0x854442));
	    
	    JLabel necessitites = new JLabel();
	    necessitites.setText("Necessitites: ");
	    necessitites.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
	    necessitites.setBounds(35, 190, 250, 75);
	    necessitites.setForeground(Color.WHITE);
	    
	    JTextField necessititesF = new JTextField();
	    necessititesF.setPreferredSize(new Dimension(150,30));
	    necessititesF.setBounds(180, 217, 250, 25);
	    necessititesF.setBackground(new Color(0xfff4e6));
	    necessititesF.setBorder(BorderFactory.createCompoundBorder());
	    necessititesF.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
	    necessititesF.setForeground(new Color(0x854442));
	    
	    JTextField pnumf = new JTextField();
	    pnumf.setPreferredSize(new Dimension(150,30));
	    pnumf.setBounds(180, 145, 250, 25);
	    pnumf.setBackground(new Color(0xfff4e6));
	    pnumf.setBorder(BorderFactory.createCompoundBorder());
	    pnumf.setForeground(new Color(0x854442));
	    pnumf.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

        JButton addButton = new JButton("Submit");
	    addButton.setBounds(330, 389, 130, 40);;
	    addButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
	    addButton.setBackground(new Color(0xfff4e6));
	    addButton.setBorder(BorderFactory.createCompoundBorder());
	    addButton.setForeground(new Color(0x4b3832));
	    addButton.setContentAreaFilled(false);
	    addButton.setOpaque(true);
        
        JButton logoutButton = new JButton("Logout");
	    logoutButton.setBounds(390, 30, 80, 30);;
	    logoutButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
	    logoutButton.setBackground(new Color(0xfff4e6));
	    logoutButton.setBorder(BorderFactory.createCompoundBorder());
	    logoutButton.setForeground(new Color(0x4b3832));
	    logoutButton.setContentAreaFilled(false);
	    logoutButton.setOpaque(true);
    	
    	JFrame userFrame = new JFrame();
	    userFrame.setTitle("sadas");
	    userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	    userFrame.setSize(500,600);
	    userFrame.setLayout(null); 
	    userFrame.setVisible(true);
	    userFrame.add(panel1);
	    userFrame.add(panel2);
	    userFrame.setResizable(false);
	  
	    panel1.add(logoutButton);
	    panel1.add(welcome);
	    
	    panel2.add(Name);
	    panel2.add(nameF);
	    panel2.add(adddona);
	    panel2.add(necessititesF);
	    panel2.add(locationF);
	    panel2.add(pnum);
	    panel2.add(pnumf);
	    panel2.add(location);
	    panel2.add(necessitites);
	    panel2.add(addButton);
	    panel2.add(icon);
        
	    addButton.addActionListener(e -> {
	    	String name = nameF.getText();
	        String phoneNumber = pnumf.getText();
	        String necessities = necessititesF.getText();
	        String locations = locationF.getText();

	        if (!name.isEmpty() && !phoneNumber.isEmpty() && !necessities.isEmpty() && !locations.isEmpty()) {
	            donationManager.addDonation(name, phoneNumber, necessities, locations);

	            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
	                String insertQuery = "INSERT INTO donations (name, phone_number, necessities, location) VALUES (?, ?, ?, ?)";

	                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
	                    preparedStatement.setString(1, name);
	                    preparedStatement.setString(2, phoneNumber);
	                    preparedStatement.setString(3, necessities);
	                    preparedStatement.setString(4, locations);

	                    preparedStatement.executeUpdate();
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }

	            nameF.setText("");
	            pnumf.setText("");
	            necessititesF.setText("");
	            locationF.setText("");
	        } else {
	            JOptionPane.showMessageDialog(null, "Please fill in all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

        
        logoutButton.addActionListener(e -> {
            userFrame.dispose();
            showLogin();
        });
    }

     


    private static void showAdminApp(String username) {
    		
    		ImageIcon originalIcon = new ImageIcon("composition-still-life-friendship-day-elements.jpg");
	        Image originalImage = originalIcon.getImage();
	        Image resizedImage = originalImage.getScaledInstance(784, 561, Image.SCALE_SMOOTH);
	        ImageIcon resizedIcon = new ImageIcon(resizedImage);
	        JLabel icon = new JLabel();
	        icon.setIcon(resizedIcon);
	        icon.setBounds(0, 0, 692, 561);
	        
	        ImageIcon originalIcon1 = new ImageIcon("logo.png");
	        Image originalImage1 = originalIcon1.getImage();
	        Image resizedImage1 = originalImage1.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
	        ImageIcon resizedIcon1 = new ImageIcon(resizedImage1);
	        JLabel icon1 = new JLabel();
	        icon1.setIcon(resizedIcon1);
	        icon1.setBounds(50, 0, 100, 100);
    	
            JLabel welcomeadmin = new JLabel();
            welcomeadmin.setText("Welcome, " + username + "!");
            welcomeadmin.setFont(new Font("Comic Sans MS Bold", Font.BOLD, 20));
            welcomeadmin.setForeground(new Color(0x4b3832));
            welcomeadmin.setBounds(10, 30, 200, 30);
    	    
    	    JLabel adddona = new JLabel();
    	    adddona.setText("Add Donation Recipients");
    	    adddona.setFont(new Font("Comic Sans MS Bold", Font.BOLD, 30));
    	    adddona.setBounds(30, 5, 400, 75);
    	    adddona.setForeground(new Color(0xfff4e6));
    	    
    	    JLabel line1 = new JLabel();
    	    line1.setForeground(new Color(0xfff4e6));
    	    line1.setBounds(40, 116, 295, 100);
    	    line1.setText("________________________________________");
    	    
    	    JLabel line2 = new JLabel();
    	    line2.setForeground(new Color(0xfff4e6));
    	    line2.setBounds(40, 206, 295, 100);
    	    line2.setText("________________________________________");
    	    
    	    JLabel line3 = new JLabel();
    	    line3.setForeground(new Color(0xfff4e6));
    	    line3.setBounds(40, 296, 295, 100);
    	    line3.setText("________________________________________");
    	    
    	    JLabel line4 = new JLabel();
    	    line4.setForeground(new Color(0xfff4e6));
    	    line4.setBounds(40, 386, 295, 100);
    	    line4.setText("________________________________________");
    	    
    	    JLabel line5 = new JLabel();
    	    line5.setForeground(new Color(0xfff4e6));
    	    line5.setBounds(40, 386, 295, 100);
    	    line5.setText("________________________________________");
    	    
    	    JLabel Name = new JLabel();
    	    Name.setText("Name: ");
    	    Name.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
    	    Name.setBounds(40, 90, 200, 75);
    	    Name.setForeground(new Color(0xfff4e6));

    	    JTextField nameF = new JTextField();
    	    nameF.setPreferredSize(new Dimension(150,30));
    	    nameF.setBounds(40, 145, 250, 26);
    	    nameF.setBackground(new Color(0xBE9B7B & 0xFFFFFF | (40 << 24), true));
    	    nameF.setBorder(BorderFactory.createCompoundBorder());
    	    nameF.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
    	    nameF.setForeground(new Color(0xfff4e6));
    	    nameF.setOpaque(false);
     	   
    	    JLabel pnum = new JLabel();
    	    pnum.setText("Phone Number: ");
    	    pnum.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
    	    pnum.setBounds(40, 180, 250, 75);
    	    pnum.setForeground(new Color(0xfff4e6));
    	    
    	    JTextField pnumf = new JTextField();
    	    pnumf.setPreferredSize(new Dimension(150,30));
    	    pnumf.setBounds(40, 235, 250, 26);
    	    pnumf.setBackground(new Color(0xBE9B7B & 0xFFFFFF | (150 << 24), true));
    	    pnumf.setBorder(BorderFactory.createCompoundBorder());
    	    pnumf.setForeground(new Color(0xfff4e6));
    	    pnumf.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
    	    pnumf.setOpaque(false);
    	    
    	    JLabel locationl = new JLabel();
    	    locationl.setText("Location: ");
    	    locationl.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
    	    locationl.setBounds(40, 270, 250, 75);
    	    locationl.setForeground(new Color(0xfff4e6));
    	    
    	    JTextField locationF = new JTextField();
    	    locationF.setPreferredSize(new Dimension(150,30));
    	    locationF.setBounds(40, 325, 250, 26);
    	    locationF.setBackground(new Color(0xBE9B7B & 0xFFFFFF | (150 << 24), true));
    	    locationF.setBorder(BorderFactory.createCompoundBorder());
    	    locationF.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
    	    locationF.setForeground(new Color(0xfff4e6));
    	    locationF.setOpaque(false);
    	    
    	    JLabel necessitites = new JLabel();
    	    necessitites.setText("Necessitites: ");
    	    necessitites.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
    	    necessitites.setBounds(45, 360, 250, 75);
    	    necessitites.setForeground(new Color(0xfff4e6));
    	    
    	    JTextField necessititesF = new JTextField();
    	    necessititesF.setPreferredSize(new Dimension(150,30));
    	    necessititesF.setBounds(40, 415, 250, 26);
    	    necessititesF.setBackground(new Color(0xBE9B7B & 0xFFFFFF | (150 << 24), true));
    	    necessititesF.setBorder(BorderFactory.createCompoundBorder());
    	    necessititesF.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
    	    necessititesF.setForeground(new Color(0xfff4e6));
    	    necessititesF.setOpaque(false);

    	    JPanel panel1 = new JPanel();
    	    panel1.setBackground(new Color(0xfff4e6));
    	    panel1.setBounds(0, 0, 192, 561);
    	    panel1.setLayout(null);
    	    
    	    JPanel panel3 = new JPanel();
    	    panel3.setBackground(new Color(0xbe9b7b));
    	    panel3.setBounds(192, 0, 692, 561);
    	    panel3.setLayout(null);
    	    
    	    JLabel conf = new JLabel();
    	    conf.setText("Enter Name:");
    	    conf.setBounds(155, 35, 300, 300);
    	    conf.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
    	    conf.setForeground(new Color(0xfff4e6));
    	    
    	    JTextField conftext = new JTextField();
    	    conftext.setBounds(80, 210, 320, 35);
    	    conftext.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
    	    conftext.setBackground(new Color(0xfff4e6));
    	    conftext.setForeground(new Color(0x2b3832));
    	    conftext.setBorder(BorderFactory.createCompoundBorder());
    	    conftext.setHorizontalAlignment(JTextField.CENTER);
    	    
    	    JPanel panel4 = new JPanel();
    	    panel4.setBackground(new Color(0xbe9b7b));
    	    panel4.setBounds(192, 0, 692, 561);
    	    panel4.setLayout(null);
    	    
    	    JPanel panel5 = new JPanel();
    	    panel5.setBackground(new Color(0xbe9b7b));
    	    panel5.setBounds(192, 0, 692, 561);
    	    panel5.setLayout(null);
    	    
    	    JPanel panel6 = new JPanel();
    	    panel6.setBackground(new Color(0xbe9b7b));
    	    panel6.setBounds(192, 0, 692, 561);
    	    panel6.setLayout(null);

            JButton addButton = new JButton("Submit");
            addButton.setBounds(230, 490, 130, 40);;
    	    addButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    addButton.setBackground(new Color(0xfff4e6));
    	    addButton.setBorder(BorderFactory.createCompoundBorder());
    	    addButton.setForeground(new Color(0x4b3832));
    	    addButton.setContentAreaFilled(false);
    	    addButton.setOpaque(true);
    	    addButton.addActionListener(e -> {
    	    	String name = nameF.getText();
    	        String phoneNumber = pnumf.getText();
    	        String necessities = necessititesF.getText();
    	        String locations = locationF.getText();

    	        if (!name.isEmpty() && !phoneNumber.isEmpty() && !necessities.isEmpty() && !locations.isEmpty()) {
    	            donationManager.addDonation(name, phoneNumber, necessities, locations);

    	            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
    	                String insertQuery = "INSERT INTO donations (name, phone_number, necessities, location) VALUES (?, ?, ?, ?)";

    	                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
    	                    preparedStatement.setString(1, name);
    	                    preparedStatement.setString(2, phoneNumber);
    	                    preparedStatement.setString(3, necessities);
    	                    preparedStatement.setString(4, locations);

    	                    preparedStatement.executeUpdate();
    	                }
    	            } catch (SQLException ex) {
    	                ex.printStackTrace();
    	            }

    	            nameF.setText("");
    	            pnumf.setText("");
    	            necessititesF.setText("");
    	            locationF.setText("");
    	        } else {
    	            JOptionPane.showMessageDialog(null, "Please fill in all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
    	        }
    	    });
            
            JButton confirmButton = new JButton("Confirm");
            confirmButton.setBounds(160, 280, 130, 40);;
            confirmButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            confirmButton.setBackground(new Color(0xfff4e6));
            confirmButton.setBorder(BorderFactory.createCompoundBorder());
            confirmButton.setForeground(new Color(0x4b3832));
            confirmButton.setContentAreaFilled(false);
    	    confirmButton.setOpaque(true);
            confirmButton.addActionListener(e -> {
                String name = conftext.getText().trim();
                if (!name.isEmpty()) {
                    boolean removed = donationManager.removeDonation(name);
                    if (removed) {
                        JOptionPane.showMessageDialog(null, "Recipients have successfully received the donation.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Recipients not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                conftext.setText("");
            });
    	    
            JPanel donationListPanel = new JPanel();
            donationListPanel.setBounds(30, 30, 430, 1000);
            donationListPanel.setPreferredSize(new Dimension(430, 1000));
            donationListPanel.setBackground(new Color(0xAB8B6E & 0xFFFFFF | (150 << 24), true));
            donationListPanel.setLayout(new BoxLayout(donationListPanel, BoxLayout.Y_AXIS));

            JScrollPane scrollPane = new JScrollPane(donationListPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            
            JLabel search = new JLabel();
    	    search.setText("Search Name:");
    	    search.setBounds(150, -120, 300, 300);
    	    search.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
    	    search.setForeground(new Color(0xfff4e6));
    	    
    	    JTextField searchtext = new JTextField();
    	    searchtext.setBounds(85, 50, 320, 35);
    	    searchtext.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
    	    searchtext.setBackground(new Color(0xfff4e6));
    	    searchtext.setForeground(new Color(0xbe9b7b));
    	    searchtext.setBorder(BorderFactory.createCompoundBorder());
    	    searchtext.setHorizontalAlignment(JTextField.CENTER);
    	    
            JPanel searchListPanel = new JPanel();
            searchListPanel.setBounds(30, 150, 430, 1000);
            searchListPanel.setBackground(new Color(0xAB8B6E & 0xFFFFFF | (150 << 24), true));
            searchListPanel.setLayout(new BoxLayout(searchListPanel, BoxLayout.Y_AXIS));
            
            JButton searchButton = new JButton("Confirm");
            searchButton.setBounds(170, 100, 130, 40);
    	    searchButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    searchButton.setBackground(new Color(0xfff4e6));
    	    searchButton.setBorder(BorderFactory.createCompoundBorder());
    	    searchButton.setForeground(new Color(0x4b3832));
    	    searchButton.setContentAreaFilled(false);
    	    searchButton.setOpaque(true);
    	    searchButton.addActionListener(e -> {
                String name = searchtext.getText();
                donationManager.searchDonation(name, searchListPanel);
            });
    	    
    	    JLabel update = new JLabel();
    	    update.setText("Update Who:");
    	    update.setBounds(150, -120, 300, 300);
    	    update.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
    	    update.setForeground(new Color(0xfff4e6));
    	    
    	    JLabel newpn = new JLabel();
    	    newpn.setText("New Phone Number:");
    	    newpn.setBounds(20, 40, 300, 20);
    	    newpn.setFont(new Font("Comic Sans MS", Font.PLAIN, 23));
    	    newpn.setForeground(new Color(0xfff4e6));
    	    
    	    JTextField newpntext = new JTextField();
    	    newpntext.setBounds(20, 70, 360, 40);
    	    newpntext.setFont(new Font("Comic Sans MS", Font.PLAIN, 23));
    	    newpntext.setBackground(new Color(0xbe9b7b));
    	    newpntext.setForeground(new Color(0xfff4e6));
    	    newpntext.setBorder(BorderFactory.createCompoundBorder());
    	    
    	    JLabel newnece = new JLabel();
    	    newnece.setText("New Necessities:");
    	    newnece.setBounds(20, 140, 300, 20);
    	    newnece.setFont(new Font("Comic Sans MS", Font.PLAIN, 23));
    	    newnece.setForeground(new Color(0xfff4e6));
    	    
    	    JTextField newnecetext = new JTextField();
    	    newnecetext.setBounds(20, 170, 360, 40);
    	    newnecetext.setFont(new Font("Comic Sans MS", Font.PLAIN, 23));
    	    newnecetext.setBackground(new Color(0xbe9b7b));
    	    newnecetext.setForeground(new Color(0xfff4e6));
    	    newnecetext.setBorder(BorderFactory.createCompoundBorder());
    	    
    	    JLabel newloca = new JLabel();
    	    newloca.setText("New Location:");
    	    newloca.setBounds(20, 240, 300, 20);
    	    newloca.setFont(new Font("Comic Sans MS", Font.PLAIN, 23));
    	    newloca.setForeground(new Color(0xfff4e6));
    	    
    	    JTextField newlocatext = new JTextField();
    	    newlocatext.setBounds(20, 270, 360, 40);
    	    newlocatext.setFont(new Font("Comic Sans MS", Font.PLAIN, 23));
    	    newlocatext.setBackground(new Color(0xbe9b7b));
    	    newlocatext.setForeground(new Color(0xfff4e6));
    	    newlocatext.setBorder(BorderFactory.createCompoundBorder());
    	    
    	    JTextField updatetext = new JTextField();
    	    updatetext.setBounds(85, 50, 320, 35);
    	    updatetext.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
    	    updatetext.setBackground(new Color(0xfff4e6));
    	    updatetext.setForeground(new Color(0xbe9b7b));
    	    updatetext.setBorder(BorderFactory.createCompoundBorder());
    	    updatetext.setHorizontalAlignment(JTextField.CENTER);
    	    
    	    JPanel updatePanel = new JPanel();
            updatePanel.setBounds(30, 150, 430, 1000);
            updatePanel.setBackground(new Color(0xAB8B6E & 0xFFFFFF | (150 << 24), true));
            updatePanel.setLayout(null);
            
            JButton confirmUpdateButton = new JButton("Confirm Update");
            confirmUpdateButton.setBounds(250, 350, 130, 40);
            confirmUpdateButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
            confirmUpdateButton.setBackground(new Color(0xfff4e6));
            confirmUpdateButton.setBorder(BorderFactory.createCompoundBorder());
            confirmUpdateButton.setForeground(new Color(0x4b3832));
            confirmUpdateButton.setContentAreaFilled(false);
    	    confirmUpdateButton.setOpaque(true);
            confirmUpdateButton.addActionListener(e2 -> {
            	String name = updatetext.getText();
                String newPhone = newpntext.getText();
                String newNecessities = newnecetext.getText();
                String newLocation = newlocatext.getText();

                donationManager.updateDonation(name, newPhone, newNecessities, newLocation);

                newpntext.setText("");
                newnecetext.setText("");
                newlocatext.setText("");
            });

            JButton updateButton = new JButton("Confirm");
            updateButton.setBounds(170, 100, 130, 40);
    	    updateButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    updateButton.setBackground(new Color(0xfff4e6));
    	    updateButton.setForeground(new Color(0x4b3832));
    	    updateButton.setBorder(BorderFactory.createCompoundBorder());
    	    updateButton.setContentAreaFilled(false);
    	    updateButton.setOpaque(true);
    	    updateButton.addActionListener(e -> {
                String name = updatetext.getText();
                donationManager.searchDonation(name, searchListPanel);
                
                if (searchListPanel.getComponentCount() > 0) {
	                updatePanel.add(newpn);
	            	updatePanel.add(newpntext);
	            	updatePanel.add(newlocatext);
	            	updatePanel.add(newloca);
	            	updatePanel.add(newnece);
	            	updatePanel.add(newnecetext);
	                updatePanel.add(confirmUpdateButton);
	            }
	            updatePanel.revalidate();
	            updatePanel.repaint();
	        });

          
            JButton logoutButton = new JButton("Log out");
            logoutButton.setBounds(45, 510, 100, 40);;
    	    logoutButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    logoutButton.setBackground(new Color(0xe5dbcf));
    	    logoutButton.setBorder(BorderFactory.createCompoundBorder());
    	    logoutButton.setForeground(new Color(0x4b3832));
    	    logoutButton.setContentAreaFilled(false);
    	    logoutButton.setOpaque(true);
    	    
    	    JPanel panel2 = new JPanel();
    	    panel2.setBackground(new Color(0xbe9b7b));
    	    panel2.setBounds(192, 0, 692, 561);
    	    panel2.setLayout(null);
    	    
    	    JButton submi2 = new JButton("Add Donation Recipients");
    	    submi2.setBounds(0, 100, 200, 70);
    	    submi2.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    submi2.setBackground(new Color(0xe5dbcf));
    	    submi2.setForeground(new Color(0x4b3832));
    	    submi2.setBorder(BorderFactory.createCompoundBorder());
    	    submi2.setContentAreaFilled(false);
    	    submi2.setOpaque(true);
    	    submi2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	panel2.add(icon);
                    panel2.setVisible(true);
                    panel3.setVisible(false);
                    panel4.setVisible(false);
                    panel5.setVisible(false);
                    panel6.setVisible(false);
                }
            });
    	    
    	    JButton submi3 = new JButton("Confirm Donation");
    	    submi3.setBounds(0, 170, 200, 70);
    	    submi3.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    submi3.setBackground(new Color(0x854442));
    	    submi3.setForeground(new Color(0x4b3832));
    	    submi3.setBorder(BorderFactory.createCompoundBorder());
    	    submi3.setContentAreaFilled(false);
    	    submi3.setOpaque(false);
    	    submi3.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	panel3.add(icon);
                	panel2.setVisible(false);
                    panel3.setVisible(true);
                    panel4.setVisible(false);
                    panel5.setVisible(false);
                    panel6.setVisible(false);
                }
            });
    	    
    	    JButton submi4 = new JButton("View List");
    	    submi4.setBounds(0, 240, 200, 70);
    	    submi4.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    submi4.setBackground(new Color(0xe5dbcf));;
    	    submi4.setForeground(new Color(0x4b3832));
    	    submi4.setBorder(BorderFactory.createCompoundBorder());
    	    submi4.setContentAreaFilled(false);
    	    submi4.setOpaque(true);
    	    submi4.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	donationListPanel.removeAll();
                	donationManager.displayDonation(donationListPanel);
                    donationListPanel.revalidate();
                    donationListPanel.repaint();
                    panel4.add(icon);
                    panel2.setVisible(false);
                    panel3.setVisible(false);
                    panel4.setVisible(true);
                    panel5.setVisible(false);
                    panel6.setVisible(false);
                }
            });
    	    
    	    JButton submi5 = new JButton("Search Donation");
    	    submi5.setBounds(0, 310, 200, 70);
    	    submi5.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    submi5.setBackground(new Color(0x854442));
    	    submi5.setForeground(new Color(0x4b3832));
    	    submi5.setBorder(BorderFactory.createCompoundBorder());
    	    submi5.setContentAreaFilled(false);
    	    submi5.setOpaque(false);
    	    submi5.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
               	    panel5.add(searchListPanel);
               	    panel5.add(icon);
                	panel2.setVisible(false);
                    panel3.setVisible(false);
                    panel4.setVisible(false);
                    panel5.setVisible(true);
                    panel6.setVisible(false);
                }
            });
    	    
    	    JButton submi6 = new JButton("Update Donation");
    	    submi6.setBounds(0, 380, 200, 70);
    	    submi6.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
    	    submi6.setBackground(new Color(0xe5dbcf));
    	    submi6.setForeground(new Color(0x4b3832));
    	    submi6.setBorder(BorderFactory.createCompoundBorder());
    	    submi6.setContentAreaFilled(false);
    	    submi6.setOpaque(true);
    	    submi6.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	panel6.add(updatePanel);
                	panel6.add(icon);
                	panel2.setVisible(false);
                    panel3.setVisible(false);
                    panel4.setVisible(false);
                    panel5.setVisible(false);
                    panel6.setVisible(true);
                }
            });
    	    

    	    JFrame userFrame = new JFrame();
    	    userFrame.setTitle("sadas");
    	    userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
    	    userFrame.setSize(700,600);
    	    userFrame.setLayout(null); 
    	    userFrame.setResizable(false);
    	    userFrame.setVisible(true);
    	    userFrame.add(panel1);
    	    userFrame.add(panel2);
    	    userFrame.add(panel3);
    	    userFrame.add(panel4);
    	    userFrame.add(panel5);
    	    userFrame.add(panel6);
    	    
    	    panel1.add(submi2);
    	    panel1.add(submi3);
    	    panel1.add(submi4);
    	    panel1.add(submi5);
    	    panel1.add(submi6);
    	    panel1.add(logoutButton);
    	    panel1.add(icon1);
    	    
    	    panel2.add(Name);
    	    panel2.add(nameF);
    	    panel2.add(adddona);
    	    panel2.add(necessititesF);
    	    panel2.add(locationF);
    	    panel2.add(pnum);
    	    panel2.add(pnumf);
    	    panel2.add(locationl);
    	    panel2.add(necessitites);
    	    panel2.add(line1);
    	    panel2.add(line2);
    	    panel2.add(line3);
    	    panel2.add(line4);
    	    panel2.add(addButton);
    	    panel2.add(icon);
    	    
    	    panel3.add(conf);
    	    panel3.add(conftext);
    	    panel3.add(confirmButton);
    	    panel3.setVisible(false);
    	    
    	    panel4.add(donationListPanel);
    	    donationListPanel.add(scrollPane);
    	    panel4.repaint();
    	    panel4.revalidate();
    	    panel4.setVisible(false);
    	    
    	    
    	    panel5.add(searchtext);
    	    panel5.add(searchButton);
    	    panel5.add(search);
    	    panel5.setVisible(false);
    	    
    	    panel6.add(updateButton);
    	    panel6.add(updatetext);
    	    panel6.add(update);
    	    panel6.setVisible(false);
    	    
            addButton.setToolTipText("Add a new donation");
            confirmButton.setToolTipText("Confirm donations received");
            searchButton.setToolTipText("Search donation recipients");
            updateButton.setToolTipText("Update donation recipients");
            logoutButton.setToolTipText("Log out");

            logoutButton.addActionListener(e -> {
                userFrame.dispose();
                showLogin();
            });

            userFrame.setVisible(true);
        };
    }
