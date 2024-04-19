package Part2;

import java.util.HashMap;
import java.util.Map;
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
    private Map<Horse, List<Bet>> horseBets = new HashMap<>();

    public void placeBet(Horse horse, double amount, double odds) {
        Bet bet = new Bet(horse, amount, odds);
        bets.add(bet);
        horseBets.computeIfAbsent(horse, k -> new ArrayList<>()).add(bet);
    }

    public void calculatePayouts() {
        for (Bet bet : bets) {
            if (bet.horse.hasWon()) {
                double payout = bet.amount * bet.odds;
                // Update user currency here
            }
        }
    }
}
