package com.ridango.game;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class GameController {
    private int highestScore = 0;
    private int currentScore = 0;
    private Set<String> shownCocktails = new HashSet<>();
    private final CocktailService cocktailService;

    public GameController(CocktailService cocktailService) {
        this.cocktailService = cocktailService;
    }

    public void startGame() {
        while (true) {
            Cocktail cocktail = cocktailService.getRandomCocktail();

            // Check if the cocktail has already been shown
            if (shownCocktails.contains(cocktail.getStrDrink())) {
                continue;
            }

            shownCocktails.add(cocktail.getStrDrink());  // Add cocktail to the set
            boolean success = playGuessingGame(cocktail);

            // If the player fails to guess, handle the end of the round
            if (!success) {
                handleGameOver();
                currentScore = 0;
            }

            // Ask the player if they want to continue or quit
            if (!askToContinue()) {
                System.out.println("Game over! Your highest score: " + highestScore);
                break;
            }
        }
    }

    private void handleGameOver() {
        System.out.println("Game over! Your current score: " + currentScore);
        if (currentScore > highestScore) {
            highestScore = currentScore;
            System.out.println("New high score: " + highestScore);
        } else {
            System.out.println("Highest score remains: " + highestScore);
        }
    }

    private boolean askToContinue() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to play another round? (yes/no)");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y");
    }

    private boolean playGuessingGame(Cocktail cocktail) {
        Scanner scanner = new Scanner(System.in);
        String cocktailName = cocktail.getStrDrink();
        char[] revealedName = hideCocktailName(cocktailName).toCharArray();
        int attempts = 5;

        // Display the hidden cocktail name and instructions at the start
        System.out.println("Guess the cocktail name!");
        System.out.println("Instructions: " + cocktail.getStrInstructions());
        System.out.println("Cocktail Name: " + new String(revealedName));

        while (attempts > 0) {
            System.out.println("Enter your guess:");
            String guess = scanner.nextLine();

            if (guess.equalsIgnoreCase(cocktailName)) {
                System.out.println("Correct! You guessed the cocktail: " + cocktailName);
                currentScore += attempts;  // Add remaining attempts to the current score
                System.out.println("Current Score: " + currentScore);
                return true;
            } else {
                attempts--;
                System.out.println("Wrong guess! Attempts left: " + attempts);
                revealLetters(cocktailName, revealedName, attempts);
                System.out.println("Cocktail Name: " + new String(revealedName));

                if (attempts == 4) {
                    System.out.println("Category: " + cocktail.getStrCategory());
                }
                if (attempts == 3) {
                    System.out.println("Glass: " + cocktail.getStrGlass());
                }
                if (attempts == 2) {
                    System.out.println("Ingredients: " + cocktail.getStrIngredients());
                }
            }
        }

        // If player fails to guess, return false to indicate failure
        return false;
    }

    private String hideCocktailName(String cocktailName) {
        // Replace the name with underscores
        return cocktailName.replaceAll("[a-zA-Z0-9]", "_");
    }


    private void revealLetters(String cocktailName, char[] revealedName, int attemptsLeft) {
        Random random = new Random();
        int nameLength = cocktailName.length();

        // Dynamically calculate the number of letters to reveal based on the word's length and remaining attempts
        int lettersToReveal = Math.max(1, (int) Math.ceil(nameLength * (0.03 * (6 - attemptsLeft))));

        System.out.println("Revealing letters...");

        int revealedCount = 0;

        for (int i = 0; i < lettersToReveal; i++) {
            boolean letterRevealed = false;

            // Attempt to reveal letters while avoiding an infinite loop
            int attempts = 0;
            while (!letterRevealed && attempts < 100) {
                int randomIndex = random.nextInt(nameLength);

                // Ensure that we reveal only hidden letters (underscores)
                if (revealedName[randomIndex] == '_' && Character.isLetterOrDigit(cocktailName.charAt(randomIndex))) {
                    revealedName[randomIndex] = cocktailName.charAt(randomIndex);
                    letterRevealed = true;
                    revealedCount++;
                }
                attempts++;
            }

            // If no more letters can be revealed, break out of the loop early
            if (revealedCount >= nameLength) {
                System.out.println("All revealable letters have been revealed.");
                break;
            }
        }
    }
}
