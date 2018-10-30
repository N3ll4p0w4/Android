package tris.daminatoluca.it.tris;

/**
 * Created by pigro on 29/01/2018.
 */

public class Tris {

    private int row;
    private int column;
    private int[][] matrice;
    private int totali;
    private int liberi;

    private int winner;

    public Tris(){
        this.row = 3;
        this.column = 3;
        this.liberi = row*column;
        this.totali = liberi;
        matrice = new int[row][column];
    }

    public boolean isLibero(int row, int column){
        return matrice[row][column] == 0;
    }

    public void setTurno(int row, int column, int player){
        matrice[row][column] = player;
        liberi--;
    }

    public int getWinner(){
        return winner;
    }

    public int getTotali(){
        return totali;
    }

    public int getLiberi() {
        return liberi;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int[][] getMatrice(){
        return matrice;
    }

    public void stampaMatrice(){
        for(int i=0; i<row; i++){
            for(int j=0; j<column; j++){
                System.out.println(matrice[i][j]+"  ");
            }
            System.out.println("\n");
        }
    }

    public boolean hasWin(){
        boolean r = false;
        //Controllo se vittoria orizzontale
        for(int i=0; i<row; i++){
            for(int j=0; j<column; j++){
                if(matrice[i][j] == matrice[i][0] && matrice[i][j] != 0)
                    r = true;
                else {
                    r = false;
                    break;
                }
            }
            if(r) {
                winner = matrice[i][0];
                return r;
            }
        }
        //Controllo se vittoria verticale
        for(int i=0; i<column; i++){
            for(int j=0; j<row; j++){
                if(matrice[j][i] == matrice[0][i] && matrice[j][i] != 0)
                    r = true;
                else {
                    r = false;
                    break;
                }
            }
            if(r) {
                winner = matrice[0][i];
                return r;
            }
        }
        //Controllo se vittoria obliquo da as a bd
        for(int i=0; i<column; i++){
            if(matrice[i][i] == matrice[0][0] && matrice[i][i] != 0)
                r = true;
            else {
                r = false;
                break;
            }
        }
        if(r) {
            winner = matrice[0][0];
            return r;
        }
        //Controllo se vittoria obliquo da ad a bs
        for(int i=0, j=column-1; i<column; i++, j--){
            if(matrice[i][j] == matrice[0][column-1] && matrice[i][j] != 0)
                r = true;
            else {
                r = false;
                break;
            }
        }
        if(r) {
            winner = matrice[0][column-1];
            return r;
        }
        return r;
    }
}
