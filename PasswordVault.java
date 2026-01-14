import java.io.*;
import java.util.*;

public class PasswordVault
{
    // ANSI Color Codes for CMD
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
    
    private static String currentUser = "";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "superadmin123";
    private static boolean isAdmin = false;
    private static ArrayList<String[]> passwords = new ArrayList<>();
    private static final String FILE_NAME = "vault.txt";
    static Scanner input = new Scanner(System.in);
    private static String masterPassword = "admin123";
    
    public static void main(String[] args) 
    {
        System.out.flush();
        // Welcome Banner
        System.out.println(CYAN + "╔════════════════════════════════════════════════╗");
        System.out.println("║       " + BOLD + "WELCOME TO SECUREPASS VAULT" + RESET + CYAN + "              ║");
        System.out.println("║      Password Management System v1.0           ║");
        System.out.println("╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();

        // Login
        if (!login())
        {
            System.out.println(RED + "\n[!] Access denied. Exiting program..." + RESET);
            input.close();
            return;
        }

        loadFromFile();
        showMenu();
        input.close();
    }
    
    public static void showMenu() 
    {
        if (isAdmin) 
        {
            showAdminMenu();
        } else 
        {
            showUserMenu();
        }
    }
    public static void showUserMenu() 
    {
        while (true) 
        {
            System.out.println(YELLOW + "\n╔════════════════════════════════════════════════╗");
            System.out.println("║               USER MENU                        ║");
            System.out.println("╠════════════════════════════════════════════════╣" + RESET);
            System.out.println(CYAN + "   1. Add New Password");
            System.out.println("   2. Search Password");
            System.out.println("   3. View My Passwords");
            System.out.println("   4. Generate Random Password");
            System.out.println("   5. Exit");
            System.out.println(YELLOW + "╚════════════════════════════════════════════════╝" + RESET);
            System.out.print(GREEN + "   Choose option (1-5): " + RESET);
            
            try 
            {
                int choice = input.nextInt();
                input.nextLine();
                
                switch (choice) 
                {
                    case 1: addPassword(); break;
                    case 2: searchPassword(); break;
                    case 3: viewPasswords(); break;
                    case 4: generatePassword(); break;
                    case 5: 
                        saveToFile();
                        System.out.println(GREEN + "\n[✓] Thank you for using SecurePass Vault!" + RESET);
                        return;
                    default: System.out.println(RED + "   [!] Invalid choice!" + RESET);
                }
                
                System.out.print(YELLOW + "\n   Press Enter to continue..." + RESET);
                input.nextLine();
                
            } 
            catch (InputMismatchException e) 
            {
                System.out.println(RED + "   [!] Please enter a number!" + RESET);
                input.nextLine();
            }
        }
    }   
    public static void showAdminMenu() 
    {
        while (true) 
        {
            System.out.println(PURPLE + "\n╔════════════════════════════════════════════════╗");
            System.out.println("║               ADMIN MENU                       ║");
            System.out.println("╠════════════════════════════════════════════════╣" + RESET);
            System.out.println(CYAN + "   1. Add New Password");
            System.out.println("   2. Delete Passwords");
            System.out.println("   3. Search Password");
            System.out.println("   4. View All Passwords");
            System.out.println("   5. Generate Random Password");
            System.out.println("   6. Change Master Password");
            System.out.println("   7. Manage Users");
            System.out.println("   8. Exit");
            System.out.println(PURPLE + "╚════════════════════════════════════════════════╝" + RESET);
            System.out.print(GREEN + "   Choose option (1-8): " + RESET);
            
            try 
            {
                int choice = input.nextInt();
                input.nextLine();
                
                switch (choice) 
                {
                    case 1: addPassword(); break;
                    case 2: deletePasswordBySearch(); break;
                    case 3: searchPassword(); break;
                    case 4: viewPasswords(); break;
                    case 5: generatePassword(); break;
                    case 6: changeMasterPassword(); break;
                    case 7: manageUsers(); break;
                    case 8: 
                        saveToFile();
                        System.out.println(GREEN + "\n[✓] Thank you for using SecurePass Vault!" + RESET);
                        return;
                    default: System.out.println(RED + "   [!] Invalid choice!" + RESET);
                }
                
                System.out.print(YELLOW + "\n   Press Enter to continue..." + RESET);
                input.nextLine();
                
            } 
            catch (InputMismatchException e) 
            {
                System.out.println(RED + "   [!] Please enter a number!" + RESET);
                input.nextLine();
            }
        }
    }    
    public static boolean login() 
    {
        System.out.print(CYAN + "   Enter Username: " + RESET);
        String username = input.nextLine();
        
        System.out.print(CYAN + "   Enter Password: " + RESET);
        String password = input.nextLine();
        
        loading("   Authenticating");
        
        if (checkUserCredentials(username, password)) 
        {
            currentUser = username;
            isAdmin = username.equals("admin");
            
            if (isAdmin) 
            {
                System.out.println(GREEN + "\n   [✓] Welcome, ADMINISTRATOR!" + RESET);
            } 
            else 
            {
                System.out.println(GREEN + "\n   [✓] Login successful! Welcome, " + username + RESET);
            }
            
            try 
            {
                File file = new File("master.txt");
                if (file.exists()) 
                {
                    Scanner fileScan = new Scanner(file);
                    masterPassword = fileScan.nextLine();
                    fileScan.close();
                }
            } 
            catch (IOException e) 
            {
                masterPassword = "admin123";
            }
            
            return true;
        }
        
        System.out.println(RED + "\n   [X] Invalid credentials!" + RESET);
        return false;
    }   
    public static void loadFromFile() 
    {
        loading("   Loading vault database");
        try 
        {
            File file = new File("vault.txt");
            
            if (!file.exists()) 
            {
                System.out.println(YELLOW + "   [!] No existing vault found. Starting fresh." + RESET);
                return;
            }
            
            Scanner fileScanner = new Scanner(file);
            int count = 0;
            
            while (fileScanner.hasNextLine()) 
            {
                String line = fileScanner.nextLine();
                String[] parts = line.split(":");
                
                if (parts.length == 3) 
                {
                    String decryptedPassword = decrypt(parts[2]);
                    passwords.add(new String[]{parts[0], parts[1], decryptedPassword});
                    count++;
                }
            }
            fileScanner.close();
            
            System.out.println(GREEN + "   [✓] Loaded " + count + " passwords from vault." + RESET);
            
        } 
        catch (IOException e) 
        {
            System.out.println(RED + "   [X] Error loading vault: " + e.getMessage() + RESET);
        }
    }    
    public static void saveToFile() 
    {
        loading("   Encrypting and saving data");
        try 
        {
            PrintWriter writer = new PrintWriter("vault.txt");
            
            for (String[] record : passwords) 
            {
                String encryptedPassword = encrypt(record[2]);
                writer.println(record[0] + ":" + record[1] + ":" + encryptedPassword);
            }
            writer.close();
            System.out.println(GREEN + "   [✓] Saved " + passwords.size() + " passwords to vault." + RESET);
        } 
        catch (IOException e) 
        {
            System.out.println(RED + "   [X] Error saving vault: " + e.getMessage() + RESET);
        }
    }    
    public static void addPassword() 
    {
        System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
        System.out.println("   │            ADD NEW PASSWORD                 │");
        System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        
        String[] newRecord = new String[3];
        
        System.out.print(CYAN + "   Website/App: " + RESET);
        newRecord[0] = input.nextLine();
        
        newRecord[1] = currentUser;
        System.out.println(CYAN + "   Username: " + RESET + currentUser + " (auto-filled)");
        
        System.out.print(CYAN + "   Password: " + RESET);
        newRecord[2] = input.nextLine();
        
        passwords.add(newRecord);
        System.out.println(GREEN + "   [✓] Password added successfully!" + RESET);
        
        System.out.print(YELLOW + "\n   Add another password? (yes/no): " + RESET);
        String choice = input.nextLine();
        if (choice.equalsIgnoreCase("yes")) 
        {
            addPassword();
        }
    }    
    public static void viewPasswords() 
    {
        int indexCounter = 0;
        
        if (isAdmin) 
        {
            System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
            System.out.println("   │        ALL PASSWORDS (Admin View)        │");
            System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        } 
        else 
        {
            System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
            System.out.println("   │            MY PASSWORDS                 │");
            System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        }
        
        if (passwords.isEmpty()) 
        {
            System.out.println(YELLOW + "   [!] No passwords saved yet!" + RESET);
            return;
        }
        
        System.out.println(CYAN + "   ┌─────┬──────────────────────────┬──────────────────────────┬──────────────────────────┐");
        System.out.printf("   │%-5s│%-26s│%-26s│%-26s│\n", "No.", "Website", "Username", "Password");
        System.out.println("   ├─────┼──────────────────────────┼──────────────────────────┼──────────────────────────┤" + RESET);
        
        for(String[] record : passwords) 
        {
            if (isAdmin || record[1].equals(currentUser)) 
            { 
                indexCounter++;
                System.out.printf("   │%-5d│%-26s│%-26s│%-26s│\n", indexCounter, record[0], record[1], record[2]);
            }
        }
        
        System.out.println(CYAN + "   └─────┴──────────────────────────┴──────────────────────────┴──────────────────────────┘" + RESET);
        
        if (indexCounter == 0) 
        {
            if (isAdmin) 
            {
                System.out.println(YELLOW + "   [!] No passwords in the entire system!" + RESET);
            } 
            else 
            {
                System.out.println(YELLOW + "   [!] You haven't saved any passwords yet!" + RESET);
            }
        } 
        else 
        {
            if (isAdmin) 
            {
                System.out.println(GREEN + "   [✓] Showing ALL " + indexCounter + " password(s) in system." + RESET);
            } else 
            {
                System.out.println(GREEN + "   [✓] Showing " + indexCounter + " password(s) for user: " + currentUser + RESET);
            }
        }
    }   
    public static void searchPassword() 
    {
        if (isAdmin) 
        {
            System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
            System.out.println("   │     SEARCH ALL PASSWORDS (Admin)      │");
            System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        } 
        else 
        {
            System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
            System.out.println("   │         SEARCH MY PASSWORDS          │");
            System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        }
        
        if (passwords.isEmpty()) 
        {
            System.out.println(YELLOW + "   [!] No passwords saved yet!" + RESET);
            return;
        }
        
        System.out.print(CYAN + "   Enter website to search: " + RESET);
        String searchTerm = input.next().toLowerCase();
        input.nextLine();
        
        boolean found = false;
        int indexCounter = 0;
        
        System.out.println(CYAN + "   ┌─────┬──────────────────────────┬──────────────────────────┬──────────────────────────┐");
        System.out.printf("   │%-5s│%-26s│%-26s│%-26s│\n", "No.", "Website", "Username", "Password");
        System.out.println("   ├─────┼──────────────────────────┼──────────────────────────┼──────────────────────────┤" + RESET);
        
        for(String[] record : passwords)
        {
            boolean userMatch = isAdmin || record[1].equals(currentUser);
            boolean websiteMatch = record[0].toLowerCase().contains(searchTerm);
            
            if (userMatch && websiteMatch)
            {
                found = true;
                indexCounter++;
                System.out.printf("   │%-5d│%-26s│%-26s│%-26s│\n", indexCounter, record[0], record[1], record[2]);
            }
        }
        
        System.out.println(CYAN + "   └─────┴──────────────────────────┴──────────────────────────┴──────────────────────────┘" + RESET);
        
        if (found) 
        {
            if (isAdmin) 
            {
                System.out.println(GREEN + "   [✓] Found " + indexCounter + " matching password(s) in entire system!" + RESET);
            } 
            else 
            {
                System.out.println(GREEN + "   [✓] Found " + indexCounter + " matching password(s) in your account!" + RESET);
            }
        }
        else 
        {
            if (isAdmin) 
            {
                System.out.println(RED + "   [X] No passwords found for: [" + searchTerm + "] in entire system." + RESET);
            } 
            else 
            {
                System.out.println(RED + "   [X] No passwords found for: [" + searchTerm + "] in your account." + RESET);
            }
        }
    }    
    public static void generatePassword() 
    {
        System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
        System.out.println("   │       GENERATE STRONG PASSWORD       │");
        System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        
        loading("   Generating secure password");
    
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*";
    
        String allChars = uppercase + lowercase + numbers + symbols;
        Random random = new Random();
        StringBuilder password = new StringBuilder();
    
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(symbols.charAt(random.nextInt(symbols.length())));
    
        for (int i = 0; i < 8; i++) 
        {
           password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        String generatedPassword = password.toString();
    
        System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
        System.out.println("   │           GENERATED PASSWORD           │");
        System.out.println("   ├─────────────────────────────────────────────┤" + RESET);
        System.out.println(CYAN + "   │  Password: " + BOLD + generatedPassword + RESET + CYAN + "                │");
        System.out.println("   │  Length: 12 characters                   │");
        System.out.println(BLUE + "   └─────────────────────────────────────────────┘" + RESET);
        System.out.println(YELLOW + "\n   Tip: Copy this password when adding new account!" + RESET);
    }   
    public static void changeMasterPassword() 
    {
        System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
        System.out.println("   │       CHANGE MASTER PASSWORD         │");
        System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        
        int attempts = 0;
        boolean verified = false;
        
        while (attempts < 3) 
        {
            System.out.print(CYAN + "   Enter current master password: " + RESET);
            String current = input.nextLine();
            
            if (current.equals(masterPassword)) 
            {
                verified = true;
                break;
            } 
            else 
            {
                attempts++;
                System.out.println(RED + "   [X] Incorrect password! Attempts left: " + (3 - attempts) + RESET);
                
                if (attempts < 3) 
                {
                    System.out.print(YELLOW + "   Try again? (yes/no): " + RESET);
                    String tryAgain = input.nextLine();
                    if (!tryAgain.equalsIgnoreCase("yes")) 
                    {
                        System.out.println(YELLOW + "   [!] Password change cancelled." + RESET);
                        return;
                    }
                }
            }
        }
        
        if (!verified) 
        {
            System.out.println(RED + "   [X] Too many failed attempts! Returning to menu." + RESET);
            return;
        }
        
        boolean passwordChanged = false;
        
        while (!passwordChanged) 
        {
            System.out.print(CYAN + "\n   Enter new master password: " + RESET);
            String newPass = input.nextLine();
            
            System.out.print(CYAN + "   Confirm new master password: " + RESET);
            String confirm = input.nextLine();
            
            if (newPass.trim().isEmpty()) 
            {
                System.out.println(RED + "   [X] Password cannot be empty!" + RESET);
            } 
            else if (!newPass.equals(confirm)) 
            {
                System.out.println(RED + "   [X] Passwords don't match!" + RESET);
            } 
            else if (newPass.equals(masterPassword)) 
            {
                System.out.println(RED + "   [X] New password cannot be same as old password!" + RESET);
            } 
            else 
            {
                masterPassword = newPass;
                passwordChanged = true;
                System.out.println(GREEN + "   [✓] Master password changed successfully!" + RESET);
                
                try 
                {
                    PrintWriter writer = new PrintWriter("master.txt");
                    writer.println(masterPassword);
                    writer.close();
                    System.out.println(GREEN + "   [✓] Password saved to file." + RESET);
                } 
                catch (IOException e) 
                {
                    System.out.println(YELLOW + "   [!] Could not save to file, but password changed for this session." + RESET);
                }
                
                return;
            }
            
            System.out.print(YELLOW + "\n   Try entering new password again? (yes/no): " + RESET);
            String tryAgain = input.nextLine();
            if (!tryAgain.equalsIgnoreCase("yes")) 
            {
                System.out.println(YELLOW + "   [!] Password change cancelled." + RESET);
                return;
            }
        }
    }    
    public static String encrypt(String text) 
    {
        StringBuilder encrypted = new StringBuilder();
        
        for (char c : text.toCharArray())
        {
            char plusTwo = (char)(c + 2);
            char minusTwo = (char)(c - 2);
            
            encrypted.append(plusTwo);
            encrypted.append(minusTwo);
        }
        
        return encrypted.toString();
    }    
    public static String decrypt(String encryptedText) 
    {
        StringBuilder decrypted = new StringBuilder();
        
        for (int i = 0; i < encryptedText.length(); i += 2) 
        {
            char plusTwo = encryptedText.charAt(i);
            char minusTwo=encryptedText.charAt(i);
            try{minusTwo = encryptedText.charAt(i + 1);}
            catch(StringIndexOutOfBoundsException e){}
            
            plusTwo = (char)(plusTwo-2);
            minusTwo=(char)(minusTwo+2);
            if(plusTwo == minusTwo)
            {
                 char original = plusTwo;
                decrypted.append(original);
            }   
        }
        return decrypted.toString();
    }    
    public static void deletePasswordBySearch() 
    {
        System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
        System.out.println("   │         DELETE PASSWORD (Search)       │");
        System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        
        if (passwords.isEmpty()) 
        {
            System.out.println(YELLOW + "   [!] No passwords saved yet!" + RESET);
            return;
        }
        
        System.out.print(CYAN + "   Enter website to delete: " + RESET);
        String searchTerm = input.nextLine().toLowerCase();
        
        ArrayList<Integer> matches = new ArrayList<>();
        
        for (int i = 0; i < passwords.size(); i++) 
        {
            if (passwords.get(i)[0].toLowerCase().contains(searchTerm)) 
            {
                matches.add(i);
            }
        }
        
        if (matches.isEmpty()) 
        {
            System.out.println(RED + "   [X] No passwords found for: " + searchTerm + RESET);
            return;
        }
        
        System.out.println(CYAN + "\n   Found " + matches.size() + " matching password(s):");
        System.out.println("   ┌─────┬──────────────────────────┬──────────────────────────┬──────────────────────────┐");
        System.out.printf("   │%-5s│%-26s│%-26s│%-26s│\n", "No.", "Website", "Username", "Password");
        System.out.println("   ├─────┼──────────────────────────┼──────────────────────────┼──────────────────────────┤" + RESET);
        
        for (int i = 0; i < matches.size(); i++) 
        {
            String[] record = passwords.get(matches.get(i));
            System.out.printf("   │%-5d│%-26s│%-26s│%-26s│\n", (i+1), record[0], record[1], "***************");
        }
        
        System.out.println(CYAN + "   └─────┴──────────────────────────┴──────────────────────────┴──────────────────────────┘" + RESET);
        System.out.println(YELLOW + "   Showing "+matches.size()+" / "+passwords.size()+" passwords" + RESET);
        
        if (matches.size() == 1) 
        {
            System.out.print(YELLOW + "\n   Delete this password? (yes/no): " + RESET);
            if (input.nextLine().equalsIgnoreCase("yes")) 
            {
                passwords.remove((int)matches.get(0));
                System.out.println(GREEN + "   [✓] Password deleted!" + RESET);
                saveToFile();
            }
        } 
        else 
        {
            System.out.print(YELLOW + "\n   Enter number to delete (or 0 to cancel): " + RESET);
            try 
            {
                int choice = input.nextInt();
                input.nextLine();
                
                if (choice > 0 && choice <= matches.size()) 
                {
                    passwords.remove((int)matches.get(choice - 1));
                    System.out.println(GREEN + "   [✓] Password deleted!" + RESET);
                    saveToFile();
                }
            } 
            catch (InputMismatchException e) 
            {
                System.out.println(RED + "   [X] Invalid input!" + RESET);
            }
        }
    }   
    public static boolean checkUserCredentials(String username, String password) 
    {
        try 
        {
            File file = new File("users.txt");
            if (!file.exists()) 
            {
                createDefaultUsers();
            }
            
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username)) 
                {
                    String storedEncryptedPassword = parts[1];
                    String storedDecryptedPassword = decrypt(storedEncryptedPassword);
                    
                    if (password.equals(storedDecryptedPassword)) 
                    {
                        scanner.close();
                        return true;
                    }
                }
            }
            scanner.close();
        } 
        catch (IOException e) 
        {
            System.out.println(RED + "   [X] Error reading users file!" + RESET);
        }
        return false;
    }   
    public static void createDefaultUsers() 
    {
        try 
        {
            PrintWriter writer = new PrintWriter("users.txt");
            writer.println("admin:" + encrypt("superadmin123"));
            writer.println("user1:" + encrypt("password123"));
            writer.close();
            System.out.println(GREEN + "   [✓] Created default users file." + RESET);
        } 
        catch (IOException e) 
        {
            System.out.println(RED + "   [X] Could not create users file!" + RESET);
        }
    }   
    public static void manageUsers() 
    {
        System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
        System.out.println("   │         MANAGE USER ACCOUNTS          │");
        System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        System.out.println(CYAN + "   1. Add New User");
        System.out.println("   2. View All Users");
        System.out.println("   3. Back to Main Menu");
        System.out.print(GREEN + "   Choose option: " + RESET);
        
        try 
        {
            int choice = input.nextInt();
            input.nextLine();
            
            switch (choice) 
            {
                case 1: addNewUser(); break;
                case 2: viewAllUsers(); break;
                case 3: return;
                default: System.out.println(RED + "   [!] Invalid choice!" + RESET);
            }
        } 
        catch (InputMismatchException e)
        {
            System.out.println(RED + "   [!] Please enter a number!" + RESET);
            input.nextLine();
        }
    }   
    public static void addNewUser() 
    {
        System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
        System.out.println("   │            ADD NEW USER                │");
        System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        
        System.out.print(CYAN + "   Enter new username: " + RESET);
        String username = input.nextLine();
        
        System.out.print(CYAN + "   Enter password for " + username + ": " + RESET);
        String password = input.nextLine();
        
        if (userExists(username)) 
        {
            System.out.println(RED + "   [X] Username already exists!" + RESET);
            return;
        }
        
        String encryptedPassword = encrypt(password);
        
        try 
        {
            FileWriter fw = new FileWriter("users.txt", true);
            PrintWriter writer = new PrintWriter(fw);
            writer.println(username + ":" + encryptedPassword);
            writer.close();
            System.out.println(GREEN + "   [✓] User '" + username + "' added successfully!" + RESET);
        } 
        catch (IOException e) 
        {
            System.out.println(RED + "   [X] Error adding user: " + e.getMessage() + RESET);
        }
    }   
    public static boolean userExists(String username) 
    {
        try 
        {
            File file = new File("users.txt");
            if (!file.exists()) return false;
            
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length > 0 && parts[0].equals(username)) 
                {
                    scanner.close();
                    return true;
                }
            }
            scanner.close();
        } 
        catch (IOException e) 
        {
            System.out.println(RED + "   [X] Error checking user!" + RESET);
        }
        return false;
    }    
    public static void viewAllUsers() 
    {
        System.out.println(BLUE + "\n   ┌─────────────────────────────────────────────┐");
        System.out.println("   │          ALL USER ACCOUNTS           │");
        System.out.println("   └─────────────────────────────────────────────┘" + RESET);
        
        try 
        {
            File file = new File("users.txt");
            if (!file.exists()) 
            {
                System.out.println(YELLOW + "   [!] No users file found!" + RESET);
                return;
            }
            
            Scanner scanner = new Scanner(file);
            int count = 0;
            
            System.out.println(CYAN + "   ┌─────┬──────────────────────────┬──────────────────────────┐");
            System.out.printf("   │%-5s│%-26s│%-26s│\n", "No.", "Username", "Password");
            System.out.println("   ├─────┼──────────────────────────┼──────────────────────────┤" + RESET);
            
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) 
                {
                    count++;
                    String decryptedPassword = decrypt(parts[1]);
                    System.out.printf("   │%-5d│%-26s│%-26s│\n", count, parts[0], decryptedPassword);
                }
            }
            scanner.close();
            
            System.out.println(CYAN + "   └─────┴──────────────────────────┴──────────────────────────┘" + RESET);
            System.out.println(GREEN + "   [✓] Total users: " + count + RESET);
            
        } 
        catch (IOException e) 
        {
            System.out.println(RED + "   [X] Error reading users file!" + RESET);
        }
    }    
    // Loading animation method
    public static void loading(String message) {
        System.out.print("\n" + CYAN + message + " " + RESET);
        for (int i = 0; i < 3; i++) {
            System.out.print(CYAN + "▪" + RESET);
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        }
        System.out.println();
    }
}