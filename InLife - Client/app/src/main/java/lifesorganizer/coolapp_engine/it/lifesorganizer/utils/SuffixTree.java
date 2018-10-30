package lifesorganizer.coolapp_engine.it.lifesorganizer.utils;

import java.util.ArrayList;

public class SuffixTree<T> {

    private T object;

    private char carattere = '*';
    private ArrayList<SuffixTree<T>> suffixTrees = new ArrayList<>();

    public SuffixTree(){}

    public SuffixTree(char carattere){
        this.carattere = carattere;
    }

    public void insert(T object, String s){
        insert(object, s, 0);
    }
    private void insert(T object, String s, int at){
        if(s.length() == at){
            this.object = object;
            return;
        }

        for(int i=0; i<suffixTrees.size(); i++){
            if(s.charAt(at) == suffixTrees.get(i).getCarattere()) {
                suffixTrees.get(i).insert(object, s, at + 1);
                return;
            } else if(s.charAt(at) < suffixTrees.get(i).getCarattere()) {
                SuffixTree<T> ramo = new SuffixTree<>(s.charAt(at));
                suffixTrees.add(i, ramo);
                ramo.insert(object, s, at+1);
                return;
            }
        }
        SuffixTree<T> ramo = new SuffixTree<>(s.charAt(at));
        suffixTrees.add(ramo);
        ramo.insert(object, s, at+1);
        return;
    }

    public ArrayList<T> getTStartWith(String string){
        ArrayList<T> objects = new ArrayList<>();
        getTStartWith(objects, string, 0);
        return objects;
    }
    private ArrayList<T> getTStartWith(ArrayList<T> objects, String string, int at){
        if(string.length() <= at){
            if(this.object != null)
                objects.add(this.object);
            for(int i=0; i<suffixTrees.size(); i++){
                suffixTrees.get(i).getTStartWith(objects, string, at+1);
            }
        } else {
            for(int i=0; i<suffixTrees.size(); i++){
                if(suffixTrees.get(i).carattere == string.charAt(at)){
                    suffixTrees.get(i).getTStartWith(objects, string, at+1);
                    break;
                }
            }
        }
        return objects;
    }

    public T getTEquals(String s){
        T object;
        object = getTEquals(s, 0);
        return object;
    }
    private T getTEquals(String s, int at){
        if(s.length() == at){
            return this.object;
        } else if(s.length() > at) {
            for(int i=0; i<suffixTrees.size(); i++){
                if(suffixTrees.get(i).carattere == s.charAt(at)){
                    return suffixTrees.get(i).getTEquals(s, at+1);
                }
            }
        }
        return null;
    }

    public void removeT(String s){
        removeT(s, 0);
    }
    private void removeT(String s, int at){
        if(s.length() == at){
            this.object = null;
        } else if(s.length() > at) {
            for(int i=0; i<suffixTrees.size(); i++){
                if(suffixTrees.get(i).carattere == s.charAt(at)){
                    suffixTrees.get(i).removeT(s, at+1);
                    if(suffixTrees.get(i).sizeSuffixTrees() == 0 && suffixTrees.get(i).getT() == null)
                        suffixTrees.remove(i);
                } else if(s.charAt(at) < suffixTrees.get(i).getCarattere()) {
                    return;
                }
            }
        }
        return;
    }

    public void setT(T object){
        this.object = object;
    }

    public T getT() {
        return object;
    }

    public void setCarattere(char carattere) {
        this.carattere = carattere;
    }

    public char getCarattere() {
        return carattere;
    }

    public int sizeSuffixTrees(){
        return suffixTrees.size();
    }
}

