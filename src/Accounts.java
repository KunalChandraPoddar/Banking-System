import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner scanner;

    Accounts(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }



    public long open_account(String email) throws RuntimeException{
        if(!account_exists(email)){
            String open_account_query = "insert into accounts(account_number, full_name, email, balance, security_pin) values(?, ?, ?, ?, ?)";
            scanner.nextLine();
            System.out.print("Enter Full Name : ");
            String full_name = scanner.nextLine();
            System.out.println("Enter Initial Amount : ");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter Security Pin : ");
            String security_pin = scanner.nextLine();
            try{
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5,security_pin);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    return account_number;
                }
                else{
                    throw new RuntimeException("Accont Creation Failed!!!");
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Creation Failed!!!");
        
    }
    
    public long getAccount_number(String email){
        String query = "select account_number from accounts where email = ?;";
        try{
            PreparedStatement prepared = connection.prepareStatement(query);
            prepared.setString(1, email);
            ResultSet resultSet = prepared.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong("account_number");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number doesn't exists!!!");   
    }
    
    
    private long generateAccountNumber() {
        try{
            String query = "select account_number from accounts order by account_number desc limit 1;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number + 1;
            }
            else{
                return 10000100;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }

    public boolean account_exists(String email) {
        String query = "select account_number from accounts where email = ?;";
        try{
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            prepareStatement.setString(1, email);
            ResultSet resultSet = prepareStatement.executeQuery();
            if(resultSet.next()) {
                return true;
            }
            else{
                return false;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
