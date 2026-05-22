package com.com253.payrollsystem;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        
        Scanner input = new Scanner(System.in);
        System.out.println("[1] LiveTest \n[2] Test");
        
        int choice = input.nextInt();
        if (choice==1) {
            LiveTest.main(args);
        } else if (choice==2) {
            Test.main(args);
        }
    }
}