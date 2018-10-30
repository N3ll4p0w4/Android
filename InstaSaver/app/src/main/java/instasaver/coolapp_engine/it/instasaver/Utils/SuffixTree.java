package instasaver.coolapp_engine.it.instasaver.Utils;

import java.util.ArrayList;

public class SuffixTree {

    private Account account;
    private char carattere = '*';
    private ArrayList<SuffixTree> suffixTrees = new ArrayList<>();

    public SuffixTree(){}

    public SuffixTree(char carattere){
        this.carattere = carattere;
    }

    public void insertByUsername(Account account){
        insert(account, account.getUsername().toLowerCase(), 0);
    }
    public void insertByNome(Account account){
        insert(account, account.getNome().toLowerCase(), 0);
    }
    private void insert(Account account, String s, int at){
        if(s.length() == at){
            this.account = account;
            return;
        }

        int i=0;
        for(; i<suffixTrees.size(); i++){
            if(s.charAt(at) == suffixTrees.get(i).getCarattere()) {
                suffixTrees.get(i).insert(account, s, at + 1);
                return;
            } else if(s.charAt(at) < suffixTrees.get(i).getCarattere()) {
                SuffixTree ramo = new SuffixTree(s.charAt(at));
                suffixTrees.add(i, ramo);
                ramo.insert(account, s, at+1);
                return;
            }
        }
        SuffixTree ramo = new SuffixTree(s.charAt(at));
        suffixTrees.add(ramo);
        ramo.insert(account, s, at+1);
        return;
    }

    public ArrayList<Account> getAccountStartWith(String string){
        ArrayList<Account> accounts = new ArrayList<>();
        return getAccountStartWith(accounts, string.toLowerCase(), 0);
    }
    private ArrayList<Account> getAccountStartWith(ArrayList<Account> accounts, String string, int at){
        if(string.length() <= at){
            if(this.account != null)
                accounts.add(this.account);
            for(int i=0; i<suffixTrees.size(); i++){
                suffixTrees.get(i).getAccountStartWith(accounts, string, at+1);
            }
        } else {
            for(int i=0; i<suffixTrees.size(); i++){
                if(suffixTrees.get(i).carattere == string.charAt(at)){
                    suffixTrees.get(i).getAccountStartWith(accounts, string, at+1);
                    break;
                }
            }
        }
        return accounts;
    }

    @Override
    public String toString() {
        String r = ""+carattere;
        if(account != null)
            r += " "+account.getUsername();
        r += "\n";
        for(int i=0; i<suffixTrees.size(); i++){
            r += suffixTrees.get(i).toString();
        }
        r += "\n";
        return r;
    }

    public void setAccount(Account account){
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setCarattere(char carattere) {
        this.carattere = carattere;
    }

    public char getCarattere() {
        return carattere;
    }
}
