import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeManagementApp {
    // Database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/employee_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Employee Management System ===");
        createTableIfNotExists();
        
        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addEmployee();
                    break;
                case 2:
                    viewAllEmployees();
                    break;
                case 3:
                    viewEmployeeById();
                    break;
                case 4:
                    updateEmployee();
                    break;
                case 5:
                    deleteEmployee();
                    break;
                case 6:
                    searchEmployees();
                    break;
                case 7:
                    System.out.println("Thank you for using Employee Management System!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    // Employee class (inner static class)
    static class Employee {
        private int id;
        private String name;
        private String email;
        private String department;
        private double salary;
        private String hireDate;

        public Employee() {}

        public Employee(String name, String email, String department, double salary, String hireDate) {
            this.name = name;
            this.email = email;
            this.department = department;
            this.salary = salary;
            this.hireDate = hireDate;
        }

        public Employee(int id, String name, String email, String department, double salary, String hireDate) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.department = department;
            this.salary = salary;
            this.hireDate = hireDate;
        }

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public double getSalary() { return salary; }
        public void setSalary(double salary) { this.salary = salary; }
        public String getHireDate() { return hireDate; }
        public void setHireDate(String hireDate) { this.hireDate = hireDate; }

        @Override
        public String toString() {
            return String.format("ID: %d | Name: %-15s | Email: %-25s | Department: %-10s | Salary: $%,.2f | Hire Date: %s",
                    id, name, email, department, salary, hireDate);
        }
    }
    
    // Database connection method
    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    
    // Create table if not exists
    private static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "department VARCHAR(50), " +
                    "salary DECIMAL(10,2), " +
                    "hire_date DATE)";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table checked/created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }
    
    // CRUD Operations
    
    // Create - Add new employee
    private static boolean addEmployee() {
        System.out.println("\n--- Add New Employee ---");
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Department: ");
        String department = scanner.nextLine();
        
        double salary = getDoubleInput("Enter Salary: ");
        
        System.out.print("Enter Hire Date (YYYY-MM-DD): ");
        String hireDate = scanner.nextLine();
        
        String sql = "INSERT INTO employees (name, email, department, salary, hire_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, department);
            pstmt.setDouble(4, salary);
            pstmt.setString(5, hireDate);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully!");
                return true;
            } else {
                System.out.println("Failed to add employee!");
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }
    
    // Read - Get all employees
    private static void viewAllEmployees() {
        System.out.println("\n--- All Employees ---");
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY id";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setDepartment(rs.getString("department"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setHireDate(rs.getString("hire_date"));
                
                employees.add(employee);
            }
            
            if (employees.isEmpty()) {
                System.out.println("No employees found!");
            } else {
                System.out.println("Total employees: " + employees.size());
                System.out.println("-".repeat(100));
                for (Employee emp : employees) {
                    System.out.println(emp);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving employees: " + e.getMessage());
        }
    }
    
    // Read - Get employee by ID
    private static void viewEmployeeById() {
        int id = getIntInput("\nEnter Employee ID: ");
        String sql = "SELECT * FROM employees WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setDepartment(rs.getString("department"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setHireDate(rs.getString("hire_date"));
                
                System.out.println("\n--- Employee Details ---");
                System.out.println(employee);
            } else {
                System.out.println("Employee not found with ID: " + id);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("Error retrieving employee: " + e.getMessage());
        }
    }
    
    // Update - Update employee
    private static void updateEmployee() {
        int id = getIntInput("\nEnter Employee ID to update: ");
        Employee existingEmployee = getEmployeeById(id);
        
        if (existingEmployee == null) {
            System.out.println("Employee not found with ID: " + id);
            return;
        }
        
        System.out.println("Current details: " + existingEmployee);
        System.out.println("\nEnter new details (press Enter to keep current value):");
        
        System.out.print("Name [" + existingEmployee.getName() + "]: ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) {
            existingEmployee.setName(name);
        }
        
        System.out.print("Email [" + existingEmployee.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (!email.trim().isEmpty()) {
            existingEmployee.setEmail(email);
        }
        
        System.out.print("Department [" + existingEmployee.getDepartment() + "]: ");
        String department = scanner.nextLine();
        if (!department.trim().isEmpty()) {
            existingEmployee.setDepartment(department);
        }
        
        System.out.print("Salary [" + existingEmployee.getSalary() + "]: ");
        String salaryInput = scanner.nextLine();
        if (!salaryInput.trim().isEmpty()) {
            try {
                existingEmployee.setSalary(Double.parseDouble(salaryInput));
            } catch (NumberFormatException e) {
                System.out.println("Invalid salary format. Keeping current value.");
            }
        }
        
        System.out.print("Hire Date [" + existingEmployee.getHireDate() + "]: ");
        String hireDate = scanner.nextLine();
        if (!hireDate.trim().isEmpty()) {
            existingEmployee.setHireDate(hireDate);
        }
        
        String sql = "UPDATE employees SET name = ?, email = ?, department = ?, salary = ?, hire_date = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, existingEmployee.getName());
            pstmt.setString(2, existingEmployee.getEmail());
            pstmt.setString(3, existingEmployee.getDepartment());
            pstmt.setDouble(4, existingEmployee.getSalary());
            pstmt.setString(5, existingEmployee.getHireDate());
            pstmt.setInt(6, existingEmployee.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee updated successfully!");
            } else {
                System.out.println("Failed to update employee!");
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating employee: " + e.getMessage());
        }
    }
    
    // Delete - Delete employee
    private static void deleteEmployee() {
        int id = getIntInput("\nEnter Employee ID to delete: ");
        
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            System.out.println("Employee not found with ID: " + id);
            return;
        }
        
        System.out.println("You are about to delete: " + employee);
        System.out.print("Are you sure? (yes/no): ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("yes")) {
            String sql = "DELETE FROM employees WHERE id = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Employee deleted successfully!");
                } else {
                    System.out.println("Failed to delete employee!");
                }
                
            } catch (SQLException e) {
                System.out.println("Error deleting employee: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    // Search employees by name
    private static void searchEmployees() {
        System.out.print("\nEnter name to search: ");
        String name = scanner.nextLine();
        
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE name LIKE ? ORDER BY id";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setDepartment(rs.getString("department"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setHireDate(rs.getString("hire_date"));
                
                employees.add(employee);
            }
            
            rs.close();
            
            if (employees.isEmpty()) {
                System.out.println("No employees found with name containing: " + name);
            } else {
                System.out.println("Found " + employees.size() + " employee(s):");
                System.out.println("-".repeat(100));
                for (Employee emp : employees) {
                    System.out.println(emp);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error searching employees: " + e.getMessage());
        }
    }
    
    // Helper method to get employee by ID
    private static Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setDepartment(rs.getString("department"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setHireDate(rs.getString("hire_date"));
                rs.close();
                return employee;
            }
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("Error retrieving employee: " + e.getMessage());
        }
        
        return null;
    }
    
    // Menu display
    private static void displayMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Add New Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. View Employee by ID");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. Search Employees by Name");
        System.out.println("7. Exit");
        System.out.println("=====================");
    }
    
    // Utility methods for input validation
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }
}
