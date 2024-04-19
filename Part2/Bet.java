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
    private double totalMoney = 1000.0;

    public void placeBet(Horse horse, double amount, double odds) {
        if (amount <= totalMoney && amount > 0) {
            Bet bet = new Bet(horse, amount, odds);
            bets.add(bet);
            totalMoney -= amount;  
        }
    }

    public void calculatePayouts() {
        for (Bet bet : bets) {
            if (bet.horse.getHasWon()) {
                double payout = bet.amount * bet.odds;
                totalMoney += payout; 
            } 
        }
        bets.clear(); 
    }

    public double calculateOdds(Horse horse, int raceLength) {
        double baseOdds = 1.0 / horse.getConfidence();
        double riskFactor = (raceLength / 2000.0) * (horse.getConfidence() - 0.5);
        double calculatedOdds = baseOdds * (1 + riskFactor);
    
        return Math.max(1.5, Math.round(calculatedOdds * 100) / 100.0);
    }
    
    
    
    
    public double getTotalMoney() {
        return totalMoney;
    }

    public boolean hasBets() {
        return !bets.isEmpty();
    }

    public double calculateOdds(Horse horse) {
        return Math.round(1.0 / horse.getConfidence() * 10) / 10.0;
    }
}





