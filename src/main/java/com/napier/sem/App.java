package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App
{
    // Connection to MySQL database
    private Connection con = null;

    // Connect to the MySQL database
    public void connect()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                Thread.sleep(30000);
                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/employees?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                        "root",
                        "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    // Disconnect from the MySQL database
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    // Get all salaries with a limit
    public ArrayList<Employee> getAllSalaries(int limit)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries " +
                            "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' " +
                            "ORDER BY employees.emp_no ASC " +
                            "LIMIT " + limit;

            ResultSet rset = stmt.executeQuery(strSelect);
            ArrayList<Employee> employees = new ArrayList<>();

            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    // Print a list of employees
    public void printSalaries(ArrayList<Employee> employees)
    {
        if (employees == null)
        {
            System.out.println("No employees found");
            return;
        }

        System.out.printf("%-10s %-15s %-20s %-10s\n", "Emp No", "First Name", "Last Name", "Salary");
        for (Employee emp : employees)
        {
            System.out.printf("%-10d %-15s %-20s %-10d\n",
                    emp.emp_no, emp.first_name, emp.last_name, emp.salary);
        }
    }

    // Main method
    public static void main(String[] args)
    {
        App app = new App();   // Create new Application

        app.connect();         // Connect to database

        // Get only 20 employees (change number if needed)
        ArrayList<Employee> employees = app.getAllSalaries(20);
        app.printSalaries(employees);

        app.disconnect();      // Disconnect from database
    }
}
