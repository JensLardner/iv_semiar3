package se.kth.iv1351.handledb.startup;

import se.kth.iv1351.handledb.controller.Controller;
import se.kth.iv1351.handledb.integration.UniversityDBException;
import se.kth.iv1351.handledb.view.BlockingInterpreter;

public class Main {
    public static void main(String[] args) {
        try {
            new BlockingInterpreter(new Controller()).handleCommands();
        } catch (UniversityDBException e) {
            {
                System.out.println("Could not connect to UniversityDB");
                e.printStackTrace();
            }
        }
    }
}