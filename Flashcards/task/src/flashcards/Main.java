package flashcards;

import java.io.*;
import java.util.*;

public class Main {

    Scanner scanner = new Scanner(System.in);
    Map<String, String> cards = new LinkedHashMap<>();
    Map<String, Integer> cardWithErrors = new LinkedHashMap<>();
    List<String> inputsLog = new ArrayList<>();

    public static void main(String[] args) {
        String path = "";
        Main main = new Main();
        for (int i = 0; i < args.length; i = i + 2) {
            if (args[i].equals("-import")) {
                main.importFileFromCommand(args[i + 1]);
            } else if (args[i].equals("-export")) {
                path = args[i + 1];
            }
        }

        //start
        main.welcome();

        //after exit : save everything
        main.exportFileFromCommand(path);

    }

    private void exportFileFromCommand(String path) {
        File file = new File(path);

        try (PrintWriter printWriter = new PrintWriter(new FileWriter(file))) {
            for (var entry : cards.entrySet()) {
                int error = 0;
                if (cardWithErrors.containsKey(entry.getKey())) {
                    error = cardWithErrors.get(entry.getKey());
                }

                String str = String.format("%s: %s: %d", entry.getKey(), entry.getValue(), error);
                printWriter.println(str);
                printWriter.flush();
            }
            String str = String.format("%d cards have been saved.", cards.size());
            printOutput(str);
        } catch (IOException e) {
            printOutput("File not found.");
        }
    }

    private void importFileFromCommand(String path) {
        File file = new File(path);

        try (Scanner fileScan = new Scanner(file)) {
            int c = 0;

            while (fileScan.hasNext()) {
                String line = fileScan.nextLine();
                String[] arr = line.split(": ", 3);  //key: value: error

                if (cards.containsKey(arr[0])) {
                    cards.replace(arr[0], arr[1]);
                    cardWithErrors.replace(arr[0], Objects.equals(arr[2], null) || Objects.equals(arr[2], "") ?
                            0 : Integer.parseInt(arr[2]));
                } else {
                    cards.put(arr[0], arr[1]);
                    cardWithErrors.put(arr[0], Objects.equals(arr[2], null) || Objects.equals(arr[2], "") ?
                            0 : Integer.parseInt(arr[2]));
                }
                c++;
            }

            if (c > 0) {
                String str = String.format("%d cards have been loaded.%n", c);
                printOutput(str);
            }

        } catch (FileNotFoundException e) {
            printOutput("File not found.");
        }
    }

    private void welcome() {
        printOutput("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        chooseAction();
    }

    private void printOutput(String str) {
        System.out.println(str);
        inputsLog.add(str);
    }

    private void chooseAction() {
        scanner = flushBuffer();
        String input = scanner.nextLine();
        inputsLog.add(input);

        switch (input) {
            case "add": { addCard(); break; }
            case "remove": { removeCard(); break; }
            case "import": { importFile(); break; }
            case "export": { exportFile(); break; }
            case "ask": { ask(); break; }
            case "exit": { printOutput("Bye bye!"); break; }
            case "log": { log(); break; }
            case "hardest card": { hardestCard(); break; }
            case "reset stats": { resetStats(); break; }
            default: { printOutput("Wrong Input!"); welcome(); }
        }
    }

    private void resetStats() {
        cardWithErrors.clear();
        printOutput("Card statistics have been reset.");
        welcome();
    }

    private void hardestCard() {
        int max = 0;

        List<String> keys = new ArrayList<>();

        //find key with max errors
        for (var entry : cardWithErrors.entrySet()) {
            if (entry.getValue() > max){
                max = entry.getValue();
                keys = new ArrayList<>();
                keys.add(entry.getKey());
            } else if (entry.getValue() == max) {
                keys.add(entry.getKey());
            }
        }

        if (keys.isEmpty()) {
            printOutput("There are no cards with errors.");
            welcome();
        } else if (keys.size() == 1) {
            String str = String.format("The hardest card is \"%s\". You have %d errors answering it.",
                    keys.get(0), cardWithErrors.get(keys.get(0)));
            printOutput(str);
            welcome();
        } else {
            StringBuilder br = new StringBuilder(); //The hardest card is "term". You have N errors answering it
            br.append("The hardest cards are \"");

            int error = 0;
            for (String key: keys) {
                br.append("\"").append(key).append("\", ");
                error = cardWithErrors.get(key);
            }
            br.delete(br.length() - 2, br.length());

            String str = String.format(". You have %d errors answering them.", error);
            br.append(str);

            printOutput(br.toString());
            welcome();
        }
    }

    private void log() {
        printOutput("File name:");
        scanner = flushBuffer();
        String path = scanner.nextLine();
        inputsLog.add(path);

        File file = new File(path);

        //createFileIfNotExists(file);

        try (PrintWriter printWriter = new PrintWriter(new FileWriter(file))) {
            printOutput("The log has been saved.");
            for (String entry : inputsLog) {
                printWriter.println(entry);
                printWriter.flush();
            }
            printOutput("The log has been saved.");
            welcome();
        } catch (IOException e) {
            printOutput("File not found.");
            welcome();
        }
    }

    private void ask() {
        printOutput("How many times to ask?");
        scanner = flushBuffer();
        int num = scanner.nextInt();
        inputsLog.add("" + num);

        askDefinitions(num);
        welcome();
    }

    private void exportFile() {
        printOutput("File name:");
        scanner = flushBuffer();
        String path = scanner.nextLine();
        inputsLog.add(path);

        File file = new File(path);

        try (PrintWriter printWriter = new PrintWriter(new FileWriter(file))) {
            for (var entry : cards.entrySet()) {
                int error = 0;
                if (cardWithErrors.containsKey(entry.getKey())) {
                    error = cardWithErrors.get(entry.getKey());
                }

                String str = String.format("%s: %s: %d", entry.getKey(), entry.getValue(), error);
                printWriter.println(str);
                printWriter.flush();
            }
            String str = String.format("%d cards have been saved.%n", cards.size());
            printOutput(str);
            welcome();
        } catch (IOException e) {
            printOutput("File not found.");
            welcome();
        }

    }

    private void importFile() {
        printOutput("File name:");
        scanner = flushBuffer();
        String path = scanner.nextLine();
        inputsLog.add(path);

        File file = new File(path);

        try (Scanner fileScan = new Scanner(file)) {
            int c = 0;

            while (fileScan.hasNext()) {
                String line = fileScan.nextLine();
                String[] arr = line.split(": ", 3);  //key: value: error

                if (cards.containsKey(arr[0])) {
                    cards.replace(arr[0], arr[1]);
                    cardWithErrors.replace(arr[0], Objects.equals(arr[2], null) || Objects.equals(arr[2], "") ?
                            0 : Integer.parseInt(arr[2]));
                } else {
                    cards.put(arr[0], arr[1]);
                    cardWithErrors.put(arr[0], Objects.equals(arr[2], null) || Objects.equals(arr[2], "") ?
                            0 : Integer.parseInt(arr[2]));
                }
                c++;
            }
            String str = String.format("%d cards have been loaded.%n", c);
            printOutput(str);
            welcome();

        } catch (FileNotFoundException e) {
            printOutput("File not found.");
            welcome();
        }
    }

    private void removeCard() {
        printOutput("Which card?");
        scanner = flushBuffer();
        String term = scanner.nextLine();
        inputsLog.add(term);

        //if exists The card has been removed.
        if (cards.containsKey(term)) {
            cards.remove(term);
            printOutput("The card has been removed.");
        } else {
            String str = String.format("Can't remove \"%s\": there is no such card.", term);
            printOutput(str);
        }

        System.out.println();
        System.out.println();
        welcome();
    }

    private void askDefinitions(int num) {
        int i = 0;

        for (var card : cards.entrySet()) {

            if (i < num) {
                i++;
            } else break;

            scanner = flushBuffer();
            printOutput("Print the definition of \"" + card.getKey() + "\":");
            String answer = scanner.nextLine(); //user enter definition
            inputsLog.add(answer);

            if (answer.compareTo(card.getValue()) == 0) {
                printOutput("Correct!");
            } else { //user entered wrong definition
                if (!checkForTerm(card, answer)) {
                    printOutput("Wrong. The right answer is \"" + card.getValue() + "\".");
                }

                if (cardWithErrors.containsKey(card.getKey())) {
                    cardWithErrors.replace(card.getKey(), cardWithErrors.get(card.getKey()) + 1);
                } else {
                    cardWithErrors.put(card.getKey(), 1);
                }
            }
        }
    }

    private boolean checkForTerm (Map.Entry < String, String > card, String answer){
        //check if there is a term for this definition
        for (var key : cards.keySet()) {
            if (cards.get(key).compareTo(answer) == 0) {
                printOutput("Wrong. The right answer is \"" + card.getValue() + "\"," +
                        " but your definition is correct for \"" + key + "\".");
                return true;
            }
        }
        return false;
    }

    private void addCard(){

        //read term:
        scanner = flushBuffer();
        printOutput("The card:");
        String term = scanner.nextLine();
        inputsLog.add(term);

        if (cards.containsKey(term)) {
            printOutput("The card \"" + term + "\" already exists.");
            welcome();
        } else {
            //read definition:
            scanner = flushBuffer();
            printOutput("The definition of the card:");
            String def = scanner.nextLine();
            inputsLog.add(def);

            if (cards.containsValue(def)) {
               printOutput("The definition \"" + def + "\" already exists.");
               welcome();
            } else {
                cards.put(term, def);
                String str = String.format("The pair (\"%s\":\"%s\") has been added.", term, def);
                printOutput(str);
                System.out.println();
                System.out.println();
                welcome();
            }
        }
    }

    private Scanner flushBuffer() {
        return new Scanner(System.in);
    }
}
