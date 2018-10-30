package lifesorganizer.coolapp_engine.it.lifesorganizer.accounts;

import java.util.ArrayList;

import lifesorganizer.coolapp_engine.it.lifesorganizer.utils.SuffixTree;

public class AccountManager {
    private static final AccountManager ourInstance = new AccountManager();

    public static AccountManager getInstance() {
        return ourInstance;
    }

    private AccountManager() {
    }

    public static Account mainAccount = new Account();

    private static SuffixTree<Account> accountsById = new SuffixTree<>();
    private static SuffixTree<Account> accountsByUsername = new SuffixTree<>();
    private static SuffixTree<Account> accountsByEmail = new SuffixTree<>();
    private static SuffixTree<Account> accountsByName = new SuffixTree<>();

    public static ArrayList<Account> getAccounts(){
        return accountsById.getTStartWith("");
    }

    public static void resetAccounts(){
        accountsById = new SuffixTree<>();
        accountsByUsername = new SuffixTree<>();
        accountsByEmail = new SuffixTree<>();
        accountsByName = new SuffixTree<>();
    }

    public static void aggiungiAccount(Account account){
        accountsById.insert(account, account.getId());
        accountsByUsername.insert(account, account.getUsername());
        accountsByEmail.insert(account, account.getEmail());
        accountsByName.insert(account, account.getName());
    }

    public static Account getAccountById(String id){
        Account account = accountsById.getTEquals(id);
        return account;
    }

    public static Account getAccountByUsername(String username){
        Account account = accountsByUsername.getTEquals(username);
        return account;
    }

    public static Account getAccountByEmail(String email){
        Account account = accountsByEmail.getTEquals(email);
        return account;
    }

    public static Account getAccountByName(String name){
        Account account = accountsByName.getTEquals(name);
        return account;
    }

    public static Account creaAccount(String id){
        Account account = new Account();
        account.setId(id);
        return account;
    }

}
