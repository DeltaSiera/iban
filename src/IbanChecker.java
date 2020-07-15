import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class IbanChecker {
    public static final int COUNTRY_ABBREVIATION_LENGTH = 2;
    public static final int FOUR_INITIAL_CHARACTER_LENGTH = 4;
    public static final int BASE = 55;
    public static final int FINAL_MOD97_REMAINDER_VALUE = 1;
    public static final int FIRST_NINE_NUMBERS = 9;
    public static final int BEGIN = 0;
    public static final String MOD_97 = "97";
    public static final String OUTPUT_FILE_NAME_EXTENSION = ".out.";
    public static final String INVALID_IBAN_MESSAGE = "Given IBAN is invalid!";
    public static final String VALID_IBAN_MESSAGE = "IBAN is valid!";
    public static final int DEFAULT_VALUE = 0;
    public static final String WRONG_CHOICE_MESSAGE = "Wrong choice!";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Integer> countryIBANLength = createIBANLengthMap();
        HashMap<Character, Integer> letterValues = createLetterAndItsValueMap();

        System.out.println("Choices: \n1-user enters iban;\n2-iban will be read from txt file; \nEnter your choise: ");
        int userChoice = Integer.parseInt(scanner.nextLine());
        String iban = "";                                                                               //user input iban
        switch (userChoice) {
            case 1 -> {
                iban = scanner.nextLine();
                String ibanChangedLetterPosition = changeCountryLettersPosition(iban);                  //move country abbreviation letter and two number to iban back
                String formattedIBAN = formatIBAN(ibanChangedLetterPosition, letterValues);             //replaces all letter to its specific number
                if (!performCalculationsIBAN(formattedIBAN) ||
                        !checkGivenIBANLength(iban, countryIBANLength)) {
                    System.out.println(INVALID_IBAN_MESSAGE);
                } else {
                    System.out.println(VALID_IBAN_MESSAGE);
                }
            }
            case 2 -> {
                System.out.println("Enter file path: ");
                String filePath = scanner.nextLine();                                                   //takes user input to file path
                System.out.println("Enter file name: ");
                String inputFileName = scanner.nextLine();                                              //takes user input as file name
                String outputFileName = createOutputFileName(inputFileName);                            //creates output file name with .out extension
                File resultFile = new File(filePath + outputFileName);
                if (!resultFile.exists()) {
                    resultFile.createNewFile();
                }

                BufferedReader fileReader = new BufferedReader(new FileReader(filePath + inputFileName));
                BufferedWriter resultWriter = new BufferedWriter(new FileWriter(resultFile));

                while ((iban = fileReader.readLine()) != null) {
                    String ibanChangedLetterPosition = changeCountryLettersPosition(iban);
                    String formattedIBAN = formatIBAN(ibanChangedLetterPosition, letterValues);
                    boolean validIBAN = checkGivenIBANLength(iban, countryIBANLength) &&
                            performCalculationsIBAN(formattedIBAN);
                    writeResult(resultWriter, iban, validIBAN);
                }
                fileReader.close();
                resultWriter.close();
            }
            default -> System.out.println(WRONG_CHOICE_MESSAGE);
        }
    }

    private static String createOutputFileName(String inputFileName) {
        String[] words = inputFileName.split("\\.");
        return words[0] + OUTPUT_FILE_NAME_EXTENSION + words[1];
    }

    private static boolean performCalculationsIBAN(String iban) {
        BigInteger mod97 = new BigInteger(MOD_97);
        String currentNumber = iban.substring(BEGIN, FIRST_NINE_NUMBERS);   //take first nine number
        BigInteger number = new BigInteger(currentNumber);                  //creates number of  first nine taken numbers
        number = number.remainder(mod97);                                   //perform first mode operation

        for (int i = 9; i < iban.length(); i += 7) {                        //every other loop iteration takes seven number and concatenates with mod97 remainder numbers
            currentNumber = i + 7 >= iban.length() ?
                    (number.toString() + iban.substring(i)) :
                    (number.toString() + iban.substring(i, i + 7));
            number = new BigInteger(currentNumber);                         //creates new number mod97 remainder and other seven numbers
            number = number.remainder(mod97);
        }
        return number.intValue() == FINAL_MOD97_REMAINDER_VALUE;
    }

    private static void writeResult(BufferedWriter bufferedWriter, String iban, boolean validIBAN) throws IOException {
        bufferedWriter.write(iban + ";" + validIBAN + "\n");
    }

    private static String formatIBAN(String iban, HashMap<Character, Integer> lettersValueMap) {
        StringBuilder onlyNumbersIBAN = new StringBuilder();
        for (int i = 0; i < iban.length(); i++) {
            char currentCharacter = iban.charAt(i);
            if (Character.isLetter(currentCharacter)) {
                onlyNumbersIBAN.append(lettersValueMap.get(currentCharacter));
            } else {
                onlyNumbersIBAN.append(currentCharacter);
            }
        }
        return onlyNumbersIBAN.toString();
    }

    private static String changeCountryLettersPosition(String currentIBAN) {
        String fourInitialCharacters = currentIBAN.toUpperCase().trim().substring(BEGIN, FOUR_INITIAL_CHARACTER_LENGTH);
        return currentIBAN.substring(4) + fourInitialCharacters;
    }

    private static boolean checkGivenIBANLength(final String iban, HashMap<String, Integer> ibanLength) {
        String currentIBANCountry = iban.substring(BEGIN, COUNTRY_ABBREVIATION_LENGTH);
        int length = ibanLength.getOrDefault(currentIBANCountry, DEFAULT_VALUE);
        return iban.length() == length;
    }

    private static HashMap<Character, Integer> createLetterAndItsValueMap() {
        HashMap<Character, Integer> letterValue = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            letterValue.put(c, c - BASE);
        }
        return letterValue;
    }

    public static HashMap<String, Integer> createIBANLengthMap() {
        HashMap<String, Integer> ibanMap = new HashMap<>();
        ibanMap.put("AL", 28);
        ibanMap.put("AD", 24);
        ibanMap.put("AT", 20);
        ibanMap.put("AZ", 28);
        ibanMap.put("BH", 22);
        ibanMap.put("BY", 28);
        ibanMap.put("BA", 20);
        ibanMap.put("BR", 29);
        ibanMap.put("BG", 22);
        ibanMap.put("CR", 22);
        ibanMap.put("HR", 21);
        ibanMap.put("CY", 28);
        ibanMap.put("CZ", 24);
        ibanMap.put("DK", 18);
        ibanMap.put("DO", 28);
        ibanMap.put("TL", 23);
        ibanMap.put("EG", 29);
        ibanMap.put("EE", 20);
        ibanMap.put("FO", 18);
        ibanMap.put("FI", 18);
        ibanMap.put("FR", 27);
        ibanMap.put("GE", 22);
        ibanMap.put("DE", 22);
        ibanMap.put("GI", 23);
        ibanMap.put("GR", 27);
        ibanMap.put("GL", 18);
        ibanMap.put("GT", 28);
        ibanMap.put("HU", 28);
        ibanMap.put("IS", 26);
        ibanMap.put("IQ", 23);
        ibanMap.put("IE", 22);
        ibanMap.put("IL", 23);
        ibanMap.put("IT", 27);
        ibanMap.put("JO", 30);
        ibanMap.put("KZ", 20);
        ibanMap.put("XK", 20);
        ibanMap.put("KW", 30);
        ibanMap.put("LV", 21);
        ibanMap.put("LB", 28);
        ibanMap.put("LI", 21);
        ibanMap.put("LT", 20);
        ibanMap.put("LU", 20);
        ibanMap.put("MK", 19);
        ibanMap.put("MT", 31);
        ibanMap.put("MR", 27);
        ibanMap.put("MU", 30);
        ibanMap.put("MC", 27);
        ibanMap.put("MD", 24);
        ibanMap.put("ME", 22);
        ibanMap.put("NL", 18);
        ibanMap.put("NO", 15);
        ibanMap.put("PK", 24);
        ibanMap.put("PS", 29);
        ibanMap.put("PL", 28);
        ibanMap.put("PT", 25);
        ibanMap.put("QA", 29);
        ibanMap.put("RO", 24);
        ibanMap.put("LC", 32);
        ibanMap.put("SM", 27);
        ibanMap.put("SA", 24);
        ibanMap.put("SC", 31);
        ibanMap.put("RS", 22);
        ibanMap.put("SK", 24);
        ibanMap.put("SI", 19);
        ibanMap.put("SE", 24);
        ibanMap.put("CH", 21);
        ibanMap.put("TN", 24);
        ibanMap.put("TR", 26);
        ibanMap.put("UA", 29);
        ibanMap.put("AE", 23);
        ibanMap.put("GB", 22);
        ibanMap.put("VA", 22);
        ibanMap.put("VG", 24);
        return ibanMap;
    }
}

