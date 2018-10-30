package lifesorganizer.coolapp_engine.it.lifesorganizer.accounts;

import android.graphics.Bitmap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

import lifesorganizer.coolapp_engine.it.lifesorganizer.utils.SuffixTree;
import lifesorganizer.coolapp_engine.it.lifesorganizer.utils.Utils;

import static lifesorganizer.coolapp_engine.it.lifesorganizer.accounts.AccountManager.*;

public class Account {

    private String id;
    private String username;
    private String email;
    private String name;
    private String password;
    private Bitmap immagineProfilo;

    //Amici
    private SuffixTree<Account> amici = new SuffixTree<>();

    //Richieste Da (Ricevute e da accettare o rifiutare)
    private SuffixTree<Account> richiesteDa = new SuffixTree<>();

    //Richieste A (Fatte e da aspettare risposta)
    private SuffixTree<Account> richiesteA = new SuffixTree<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Bitmap getImmagineProfilo() {
        return immagineProfilo;
    }

    public void setImmagineProfilo(Bitmap immagineProfilo) {
        this.immagineProfilo = immagineProfilo;
    }

    //Amici
    public ArrayList<Account> getAmici(){
        return amici.getTStartWith("");
    }

    public void addAmico(Account account) {
        amici.insert(account, account.getId());
    }

    public void removeAmico(Account account) {
        amici.removeT(account.getId());
    }

    //Richieste Da

    public ArrayList<Account> getRichiesteDa() {
        return richiesteDa.getTStartWith("");
    }

    public void addRichiestaDa(Account account) {
        richiesteDa.insert(account, account.getId());
    }

    public void removeRichiestaDa(Account account) {
        richiesteDa.removeT(account.getId());
    }

    public void accettaRichiestaDa(Account account){
        richiesteDa.removeT(account.getId());
        amici.insert(account, account.getId());
    }

    //Richieste A

    public ArrayList<Account> getRichiesteA() {
        return richiesteA.getTStartWith("");
    }
    public void addRichiestaA(Account account) {
        richiesteA.insert(account, account.getId());
    }

    public void removeRichiestaA(Account account) {
        richiesteA.removeT(account.getId());
    }

    public void accettaRichiestaA(Account account){
        richiesteA.removeT(account.getId());
        amici.insert(account, account.getId());
    }

    //Load

    public void minimalLoad(String accountString){
        Scanner scanner = new Scanner(accountString);

        this.setId(scanner.nextLine());
        this.setUsername(scanner.nextLine());
        this.setName(scanner.nextLine());
    }

    public void load(String accountString){
        Scanner scanner = new Scanner(accountString);

        this.setId(scanner.nextLine());
        this.setUsername(scanner.nextLine());
        this.setEmail(scanner.nextLine());
        this.setName(scanner.nextLine());
        this.setPassword(scanner.nextLine());

        //Amici
        String amici = scanner.nextLine();
        ArrayList<String> amiciStrings = Utils.getStringsFromStringWithPuntoEVirgola(amici);
        for (int i=0; i<amiciStrings.size(); i++){
            Account amico = getAccountById(amiciStrings.get(i));
            if(amico == null){
                amico = creaAccount(amiciStrings.get(i));
            }
            this.addAmico(amico);
        }

        //RichiesteDa
        String richiesteDa = scanner.nextLine();
        ArrayList<String> richiesteDaStrings = Utils.getStringsFromStringWithPuntoEVirgola(richiesteDa);
        for (int i=0; i<richiesteDaStrings.size(); i++){
            Account richiestaDa = getAccountById(richiesteDaStrings.get(i));
            if(richiestaDa == null){
                richiestaDa = creaAccount(richiesteDaStrings.get(i));
            }
            this.addRichiestaDa(richiestaDa);
        }

        //RichiesteA
        String richiesteA = scanner.nextLine();
        ArrayList<String> richiesteAStrings = Utils.getStringsFromStringWithPuntoEVirgola(richiesteA);
        for (int i=0; i<richiesteAStrings.size(); i++){
            Account richiestaA = getAccountById(richiesteAStrings.get(i));
            if(richiestaA == null){
                richiestaA = creaAccount(richiesteAStrings.get(i));
            }
            this.addRichiestaA(richiestaA);
        }

        aggiungiAccount(this);
    }
}
