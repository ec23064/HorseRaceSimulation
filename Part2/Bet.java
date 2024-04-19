package Part2;

import java.util.List;
import java.util.ArrayList;

class Bet {
    Horse horse;
    double amount;
    double odds;

    public Bet(Horse horse, double amount, double odds) {
        this.horse = horse;
        this.amount = amount;
        this.odds = odds;
    }
}

class BetManager {
    private List<Bet> bets = new ArrayList<>();
    private double totalMoney = 1000.0; // Initial money for betting
    private StringBuilder results = new StringBuilder();

    public void placeBet(Horse horse, double amount, double odds) {
        if (amount <= totalMoney && amount > 0) {
            Bet bet = new Bet(horse, amount, odds);
            bets.add(bet);
            totalMoney -= amount;  // Subtract the bet from total money
        } else {
            results.append("Not enough money or invalid bet amount.\n");
        }
    }

    public void calculatePayouts() {
        results.setLength(0);  // Clear previous results
        for (Bet bet : bets) {
            if (bet.horse.hasWon()) {
                double payout = bet.amount * bet.odds;
                totalMoney += payout;  // Add winnings including the original bet amount
                results.append(bet.horse.getName()).append(" won! Payout: $").append(String.format("%.2f", payout)).append("\n");
            } else {
                results.append(bet.horse.getName()).append(" lost. Bet: $").append(String.format("%.2f", bet.amount)).append("\n");
            }
        }
        if (bets.isEmpty()) {
            results.append("No bets were placed.\n");
        }
    }

    public String getResults() {
        return results.toString() + "Total money: $" + String.format("%.2f", totalMoney);
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public boolean hasBets() {
        return !bets.isEmpty();
    }

    public double calculateOdds(Horse horse) {
        // Simplified example: Odds based on the inverse of horse's confidence
        return Math.round(1.0 / horse.getConfidence() * 10) / 10.0;
    }
}





